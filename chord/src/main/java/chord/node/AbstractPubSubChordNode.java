package chord.node;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import chord.fingertable.FingerTableImpl;
import chord.util.Modulo;
import chord.util.Util;
import chord.util.Modulo.ModuloInterval;
import pubsub.notification.Notification;
import pubsub.subscription.DisjunctionSubscription;
import pubsub.subscription.Subscription;

/**
 * Base class for universal chord operations and members
 */
public abstract class AbstractPubSubChordNode<T extends RemotePubSubChordNode<T>>
    implements RemotePubSubChordNode<T> {

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
  transient public ConcurrentLinkedQueue<Notification> notificationQueue = new ConcurrentLinkedQueue<>();

  /**
   * utility for arithmetic modulo the modulus
   */
  transient protected final Modulo modulo;

  protected AbstractPubSubChordNode(int degree, int id) throws RemoteException {
    this.degree = degree;
    int modulus = Util.powerOf2(degree);
    this.modulus = modulus;
    this.id = id;
    this.fingerTable = new FingerTableImpl<>(degree, id);
    this.modulo = new Modulo(modulus);
  }

  protected AbstractPubSubChordNode(int degree, int id, boolean initial) throws RemoteException {
    this.degree = degree;
    int modulus = Util.powerOf2(degree);
    this.modulus = modulus;
    this.id = id;
    this.fingerTable = new FingerTableImpl<>(degree, id, new Object[degree + 1]);
    for (int i = 0; i <= degree; i++) {
      fingerTable.set(i, id, self());
    }
    this.modulo = new Modulo(modulus);
  }

  /**
   * dynamic version of "this" to avoid need to cast
   */
  protected abstract T self();

  /**
   * join the network using information from the node
   * 
   * @param chordNode the existing node of the network utilized to join this node
   *                  to the chord
   */
  public abstract void join(T chordNode) throws RemoteException;

  @Override
  public final int getId() {
    return id;
  }

  public ConcurrentLinkedQueue<Notification> getNotificationQueue() {
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
    this.loopBackSubscription = loopBackSubscription;
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
  public synchronized T findSuccessor(int n) throws RemoteException {
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
  public synchronized T findPredecessor(int n) throws RemoteException {
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
  public void publish(Notification notification, int publisherId, ModuloInterval range) throws RemoteException {
    if (loopBackSubscription != null && loopBackSubscription.matches(notification)) {
      notificationQueue.offer(notification);
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
        DisjunctionSubscription subscriptions = fingerTable.getSubscriptionOfEntry(i);
        if (subscriptions != null && subscriptions.matches(notification)) {
          T fingerTableIthNode = fingerTable.getNode(i);
          int intersectionIntervalRight = modulo.inClosed(assigned.b, range) ? assigned.b : range.b;
          // System.out
          // .println(String.format("Publish at %d requested by %d. Now publishing to %d
          // with interval %s", getId(),
          // publisherId, fingerTableIthId, modulo.new ModuloInterval(assigned.a,
          // intersectionIntervalRight)));
          fingerTableIthNode.publish(notification, getId(), modulo.interval(assigned.a, intersectionIntervalRight));
        }
      }
    }
  }

  /*
   * This node publishes a notification for the rest of the interested nodes in
   * the chord to receive. This is the origin of a notification publication, and
   * then the notification is propagated to the rest of the chord.
   */
  public void publish(Notification notification) throws RemoteException {
    Set<Integer> publishedTo = new HashSet<>();
    for (int i = 1; i <= fingerTable.size(); i++) {
      int currNodeId = fingerTable.getId(i);
      if (publishedTo.contains(currNodeId)) {
        continue;
      }
      publishedTo.add(currNodeId);
      ModuloInterval intervalAssignedToEntry = fingerTable.assignedToEntry(i);
      System.out
          .println(
              String.format("Notification published by %d to %d. Initial Interval is %s", getId(), currNodeId,
                  intervalAssignedToEntry));
      fingerTable.getNode(i).publish(notification, getId(), intervalAssignedToEntry);
    }
  }

  /*
   * Uses the interval intersection test and subscription union algorithm of
   * <a href="https://dl.acm.org/doi/abs/10.1145/966618.966627">A
   * peer-to-peer approach to content-based publish/subscribe</a>
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
        DisjunctionSubscription union = computeFilterUnion(assignedByClient);
        client.subscribe(union, getId());
      }
    }
  }

  // DisjunctionSubscription propagate = new
  // DisjunctionSubscription(subscription);
  // Set<Integer> tested = new HashSet<>();
  // for (int i = 1, fingerTableIthId = fingerTable.getId(i); i <=
  // fingerTable.size(); i++) {
  // if (tested.contains(fingerTableIthId)) {
  // continue;
  // }
  // tested.add(fingerTableIthId);
  // ModuloInterval fingerTableIthInterval = fingerTable.assignedToEntry(i);
  // if (fingerTableIthId != getId()
  // && fingerTableIthId != callerId
  // && modulo.intersects(assignedByClient, fingerTableIthInterval)) {
  // propagate.addSubscription(fingerTable.getSubscriptionOfEntry(i));
  // }
  // }

  /*
   * The filter union algorithm
   */
  public DisjunctionSubscription computeFilterUnion(ModuloInterval assignedByClient) {
    DisjunctionSubscription union = new DisjunctionSubscription();
    Set<Integer> tested = new HashSet<>();
    for (int i = 1, fingerTableCurrId = fingerTable.getId(i); i < fingerTable.size(); i++) {
      if (tested.contains(fingerTableCurrId)) {
        continue;
      }
      tested.add(fingerTableCurrId);
      ModuloInterval fingerTableCurrInterval = fingerTable.assignedToEntry(i);
      if (fingerTableCurrId != getId()
          && modulo.intersects(assignedByClient, fingerTableCurrInterval)) {
        union.addSubscription(fingerTable.getSubscriptionOfEntry(i));
      }
    }
    return union;
  }

  /*
   * Sets the subscription of this node specifically, and then starts the
   * propagation of changes to the rest of the chord.
   */
  public void subscribe(Subscription subscription) throws RemoteException {
    setLoopBackSubscription(subscription);
    for (T client : clients) {
      if (client.getId() != getId()) {
        client.subscribe(subscription, id);
      }
    }
  }

  @Override
  public void updateSubscriptionEdge(int clientId) throws RemoteException {
    for (T client : clients) {
      if (client.getId() == clientId) {
        ModuloInterval assignedByClient = client.askForAssigned(getId());
        DisjunctionSubscription union = computeFilterUnion(assignedByClient);
        if (loopBackSubscription != null) {
          union.addSubscription(loopBackSubscription);
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
    return String.format("{chord %s:\nfinger table: %s\nclients: %s}", getId(), fingerTable, clientIds);
  }
}
