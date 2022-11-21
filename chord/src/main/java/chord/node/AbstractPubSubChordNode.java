package chord.node;

import java.lang.System.Logger;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.System.Logger.Level;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;

import chord.fingertable.FingerTableImpl;
import chord.util.Modulo;
import chord.util.ModuloInterval;
import chord.util.Util;
import pubsub.notification.Notification;
import pubsub.subscription.Subscription;
import pubsub.subscription.TopicsRegexSubscription;

/**
 * Base class for universal chord operations and members
 */
public abstract class AbstractPubSubChordNode<T extends RemotePubSubChordNode<T>>
    extends UnicastRemoteObject
    implements RemotePubSubChordNode<T> {

  private static final Logger log = System.getLogger(AbstractPubSubChordNode.class.getName());

  /*
   * Use Hazelcast distributed concurrency for node joins and subscription
   * updates. One important problem in this pub-sub chord is the fact that all
   * operations that update subscription and/or routing information, such as node
   * joins or subscriptions, need to be exclusive with respect to each other, and
   * some do not, such as notification publishing. This is a
   * variation of the read-write mutual lock problem. Since Hazelcast provides no
   * mechanism for distributed read-write locking, this publish-subscribe chord
   * does not make any guarantees. Operations updating
   * subscription/routing information will be mutually exclusive with each other,
   * but not with notification publishing. Notification delivery may fail if
   * routing information updates are being made at the same time that a
   * notification is being published. Hence, this chord works better when there
   * subscription updates are not expected to be frequent.
   */
  private static final String lockName = "LOCK";
  private Config config = new Config();
  private HazelcastInstance instance;
  private CPSubsystem cpSubsystem;
  protected FencedLock lock;

  /* the identifier of this node in the chord network */
  protected final int id;

  /* the exponent for 2 for which this chord network is based on */
  protected final int degree;

  /*
   * the modulus value for which this chord network is based on, and which all
   * modular arithmetic by modulo done relative to
   */
  protected final int modulus;

  /* the finger table of this node */
  transient protected FingerTableImpl<T> fingerTable;

  /* the nodes of the chord whose finger table contains this node */
  transient protected Set<T> clients = ConcurrentHashMap.newKeySet();

  /*
   * contains the keys and values falling under jurisdiction of this node.
   */
  transient protected ModuloInterval managed;

  /*
   * Loopback subscription that represents the subscriptions of this node. Always
   * matched against a notification that is published to this node.
   */
  transient private Subscription loopBackSubscription;

  /*
   * A queue in which to store all notifications that match the loopback
   * subscription, for receipt by an outside application.
   */
  transient private LinkedBlockingQueue<Notification> notificationQueue = new LinkedBlockingQueue<>();

  /**
   * utility for arithmetic modulo the modulus
   */
  transient protected final Modulo modulo;

  private void hazelcastConfig() {//
    // config.getCPSubsystemConfig().setCPMemberCount(3);
    // config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
    // config.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(true)
    // .setProperty("namespace", "chordpubsub");
    instance = Hazelcast.newHazelcastInstance(config);
    cpSubsystem = instance.getCPSubsystem();
    lock = cpSubsystem.getLock(lockName);
  }

  protected AbstractPubSubChordNode(int degree, int id, boolean initial) throws RemoteException {
    super(1099);
    hazelcastConfig();
    this.degree = degree;
    int modulus = Util.powerOf2(degree);
    this.modulus = modulus;
    this.modulo = new Modulo(modulus);
    this.id = modulo.mod(id);
    this.fingerTable = new FingerTableImpl<>(degree, this.id);
    if (initial) {
      for (int i = 0; i <= degree; i++) {
        fingerTable.set(i, this.id, self());
      }
    }
  }

  protected AbstractPubSubChordNode(int degree, int id, int port, boolean initial) throws RemoteException {
    super(port);
    hazelcastConfig();
    this.degree = degree;
    int modulus = Util.powerOf2(degree);
    this.modulus = modulus;
    this.modulo = new Modulo(modulus);
    this.id = modulo.mod(id);
    this.fingerTable = new FingerTableImpl<>(degree, this.id);
    if (initial) {
      for (int i = 0; i <= degree; i++) {
        fingerTable.set(i, this.id, self());
      }
    }
  }

  /**
   * dynamic version of "this" to avoid need to cast
   */
  protected abstract T self();

  /**
   * join the network using information from the given node
   * 
   * @param chordNode the existing node of the network utilized to join this node
   *                  to the chord
   */
  public abstract void join(T chordNode) throws RemoteException;

  @Override
  public final int getId() {
    return id;
  }

  public LinkedBlockingQueue<Notification> getNotificationQueue() {
    return notificationQueue;
  }

  @Override
  public ModuloInterval getManaged() throws RemoteException {
    if (managed == null) {
      managed = modulo.interval(getId(), getImmediateSuccessor().getId() - 1);
    }
    return managed;
  }

  public void setLoopBackSubscription(Subscription loopBackSubscription) {

  }

  public Subscription getLoopBackSubscription() {
    return loopBackSubscription;
  }

  @Override
  public ModuloInterval askForAssigned(int id) throws RemoteException {
    return fingerTable.assignedToNode(id);
  }

  @Override
  public void addClient(T client) {
    clients.add(client);
  }

  @Override
  public void removeClient(T client) throws RemoteException {
    clients.remove(client);
  }

  @Override
  public T getImmediateSuccessor() {
    return fingerTable.getNode(1);
  }

  @Override
  public T getImmediatePredecessor() {
    return fingerTable.getNode(0);
  }

  @Override
  public void setImmediateSuccessor(int id, T successor) throws RemoteException {
    fingerTable.set(1, id, successor);
    successor.addClient(self());
  }

  @Override
  public void setImmediatePredecessor(int id, T predecessor) {
    fingerTable.set(0, id, predecessor);
    addClient(predecessor);
  }

  @Override
  public T closestPrecedingFingerNode(int n) throws RemoteException {
    /*
     * find the maximum i such that id < fingerTable(i) <= n. if no entry from the
     * finger table is applicable, returns this node.
     */
    for (int i = fingerTable.size(); i >= 1; i--) {
      T chordNode = fingerTable.getNode(i);
      if (modulo.inRightHalfClosed(fingerTable.getId(i), getId(), n)) {
        return chordNode;
      }
    }
    return self();
  }

  @Override
  public T findSuccessor(int n) throws RemoteException {
    T nPredecessor = findPredecessor(n);
    // if predecessor of n has id equal to n, then
    // the successor also has id equal to n
    if (nPredecessor.getId() == n) {
      return nPredecessor;
    } else {
      return nPredecessor.getImmediateSuccessor();
    }
  }

  @Override
  public T findPredecessor(int n) throws RemoteException {
    T currPredecessor = self();
    T currSuccessor = currPredecessor.getImmediateSuccessor();
    // while currPredecessor <= n < currSuccessor is false
    int currPredecessorId = currPredecessor.getId();
    int currSuccessorId = currSuccessor.getId();
    while (!modulo.inLeftHalfClosed(n, currPredecessorId, currSuccessorId)
        && currPredecessorId != currSuccessorId) {
      currPredecessor = currPredecessor.closestPrecedingFingerNode(n);
      currSuccessor = currPredecessor.getImmediateSuccessor();
      currPredecessorId = currPredecessor.getId();
      currSuccessorId = currSuccessor.getId();
    }
    return currPredecessor;
  }

  /*
   * Implementation of the mutlicast algorithm
   */
  @Override
  public void publish(String topic, String notificationDataJsonString, int publisherId, ModuloInterval range)
      throws RemoteException {
    Notification notification = null;
    try {
      notification = new Notification(topic, notificationDataJsonString);
    } catch (JsonProcessingException e) {
      log.log(Level.WARNING, "Could not deserialize the notification string: {0}", notificationDataJsonString);
      return;
    }
    if (loopBackSubscription != null && loopBackSubscription.matches(notification)) {
      try {
        boolean offered = notificationQueue.offer(notification, 3, TimeUnit.SECONDS);
        if (!offered) {
          log.log(Level.WARNING, "Queue offer timed out. Notification discarded.");
        }
      } catch (InterruptedException e) {
        log.log(Level.WARNING, "Interrupted while blocking on notification offer.");
      }
      log.log(Level.INFO, "Subscription matched notification: {0}\n", notification.notificationJsonString());
    } else if (loopBackSubscription != null && !loopBackSubscription.matches(notification)) {
      log.log(Level.INFO, "DID NOT MATCH: {0} AND {1}\n", notificationDataJsonString, loopBackSubscription);
    }
    Set<Integer> tested = new HashSet<>();
    for (int i = 1; i <= fingerTable.size(); i++) {
      int fingerTableIthId = fingerTable.getId(i);
      if (tested.contains(fingerTableIthId) || fingerTableIthId == getId() || fingerTableIthId == publisherId) {
        continue;
      }
      tested.add(fingerTableIthId);
      ModuloInterval assigned = fingerTable.assignedToEntry(i);
      if (modulo.inClosed(assigned.a, range)) {
        Subscription subscription = fingerTable.getSubscriptionOfEntry(i);
        if (subscription != null && subscription.matches(notification)) {
          T fingerTableIthNode = fingerTable.getNode(i);
          int intersectionIntervalRight = modulo.inClosed(assigned.b, range) ? assigned.b : range.b;
          log.log(Level.DEBUG, "Notification publishing by {0} to {1}. Initial Interval is {2}\n", getId(),
              fingerTableIthId, modulo.interval(assigned.a, intersectionIntervalRight));
          fingerTableIthNode.publish(topic, notificationDataJsonString, getId(),
              modulo.interval(assigned.a, intersectionIntervalRight));
        }
      }
    }
  }

  /*
   * This node publishes a notification for the rest of the interested nodes in
   * the chord to receive. This is the origin of a notification publication, and
   * then the notification is checked on the rest of the chord.
   */
  public void publish(Notification notification) throws RemoteException {
    Set<Integer> publishedTo = new HashSet<>();
    for (int i = 1; i <= fingerTable.size(); i++) {
      int currNodeId = fingerTable.getId(i);
      if (publishedTo.contains(currNodeId) || currNodeId == getId()) {
        continue;
      }
      publishedTo.add(currNodeId);
      ModuloInterval intervalAssignedToEntry = fingerTable.assignedToEntry(i);
      log.log(Level.DEBUG,
          "Notification publishing by {0} to {1}. Initial Interval is {2}\n", getId(), currNodeId,
          intervalAssignedToEntry);
      fingerTable.getNode(i).publish(notification.getTopic(), notification.notificationJsonString(), getId(),
          intervalAssignedToEntry);
    }
  }

  /*
   * Uses the interval intersection test and filter union algorithm implemented
   * below
   */
  @Override
  public void subscribe(Subscription subscription, int callerId) throws RemoteException {
    fingerTable.addSubscriptionToNode(callerId, subscription);
    ModuloInterval assignedToCaller = fingerTable.assignedToNode(callerId);
    for (T client : clients) {
      /*
       * don't propagage the subscription update back to the caller
       */
      if (client.getId() == callerId) {
        continue;
      }
      /*
       * For each client, see if the interval assigned by the client to this node has
       * a nonempty intersection with the interval assigned by this node to the
       * calling node.
       */
      ModuloInterval assignedByClient = client.askForAssigned(getId());
      if (modulo.intersects(assignedToCaller, assignedByClient)) {
        Subscription union = computeFilterUnion(assignedByClient);
        client.subscribe(union, getId());
      }
    }
  }

  /*
   * The filter union algorithm of <a
   * href="https://dl.acm.org/doi/abs/10.1145/966618.966627">A peer-to-peer
   * approach to content-based publish/subscribe</a>
   */
  public Subscription computeFilterUnion(ModuloInterval assignedByClient) {
    Subscription union = new TopicsRegexSubscription();
    Set<Integer> tested = new HashSet<>();
    for (int i = 1, fingerTableCurrId = fingerTable.getId(i); i < fingerTable.size(); i++) {
      if (tested.contains(fingerTableCurrId)) {
        continue;
      }
      tested.add(fingerTableCurrId);
      ModuloInterval fingerTableCurrInterval = fingerTable.assignedToEntry(i);
      if (fingerTableCurrId != getId()
          && modulo.intersects(assignedByClient, fingerTableCurrInterval)) {
        union.merge(fingerTable.getSubscriptionOfEntry(i));
      }
    }
    return union;
  }

  /*
   * Sets the subscription of this node specifically, and then starts the
   * propagation of changes to the rest of the chord.
   */
  public void subscribe(Subscription subscription) throws RemoteException {
    lock.lock();
    if (loopBackSubscription == null) {
      loopBackSubscription = new TopicsRegexSubscription(subscription);
    } else {
      loopBackSubscription.merge(subscription);
    }
    // loopBackSubscription = subscription;
    for (T client : clients) {
      if (client.getId() != getId()) {
        client.subscribe(loopBackSubscription, id);
      }
    }
    lock.unlock();
  }

  @Override
  public void updateSubscriptionEdge(int clientId) throws RemoteException {
    for (T client : clients) {
      if (client.getId() == clientId) {
        ModuloInterval assignedByClient = client.askForAssigned(getId());
        Subscription union = computeFilterUnion(assignedByClient);
        if (loopBackSubscription != null) {
          union.merge(loopBackSubscription);
        }
        client.subscribe(union, getId());
        return;
      }
    }
  }

  @Override
  public String toString() {
    ArrayList<Integer> a = new ArrayList<>();
    String clientIds;
    try {
      for (T client : clients) {
        a.add(client.getId());
      }
      a.sort(Integer::compareTo);
      clientIds = a.toString();
    } catch (RemoteException e) {
      clientIds = "could not fetch client ids";
    }
    return String.format("{chord %s:\nfinger table:\n%s\nclients: %s}", getId(), fingerTable, clientIds);
  }
}
