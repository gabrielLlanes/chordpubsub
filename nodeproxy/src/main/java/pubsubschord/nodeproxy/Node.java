package pubsubschord.nodeproxy;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;

import chord.node.consistentfinger.ConsistentFingerPubSubChordNodeImpl;
import chord.node.consistentfinger.ConsistentFingerRemotePubSubChordNode;
import pubsub.notification.Notification;
import pubsub.subscription.Subscription;

public class Node {

  private static final Logger log = System.getLogger(Node.class.getName());

  private static Node instance;

  private final ConsistentFingerPubSubChordNodeImpl node;

  private final ExecutorService pool = Executors.newSingleThreadExecutor();

  public static Node getInstance() throws RemoteException {
    if (instance != null) {
      return instance;
    }
    instance = new Node();
    return instance;
  }

  private Node() throws RemoteException {
    Map<String, String> env = System.getenv();
    // the power of 2 serving as the modulus of the key space
    Integer degree = Integer.parseInt(env.get("CHORD_DEGREE"));
    // the hostname of the node to join the chord
    String hostName = env.get("CHORD_HOSTNAME");
    // port for Java RMI discovery
    Integer rmiPort = Integer.parseInt(env.get("RMI_PORT"));
    // flag to determine whether the node should be the first in the chord
    Boolean firstNode = Boolean.parseBoolean(env.get("CHORD_FIRST_NODE"));
    // hostname of the existing node at which to join the chord
    String joinNodeHostName = env.get("CHORD_JOIN_NODE_HOSTNAME");
    // port of existing node for RMI discovery
    String joinNodePort = env.get("CHORD_JOIN_NODE_PORT");

    InetAddress address;

    while (true) {
      try {
        address = InetAddress.getByName(hostName);
        break;
      } catch (UnknownHostException e) {
        log.log(Level.WARNING, "Hostname {0} was unknown. Retrying after one second.\n", hostName);
        sleep(1000);
      }
    }

    InetAddress joinNodeAddress;

    while (true) {
      try {
        joinNodeAddress = InetAddress.getByName(joinNodeHostName);
        break;
      } catch (UnknownHostException e) {
        log.log(Level.WARNING, "Join node hostname {0} was unknown. Retrying after one second.\n", joinNodeHostName);
        sleep(1000);
      }
    }

    System.setProperty("java.rmi.server.hostname", address.getHostAddress());

    log.log(Level.INFO, String.format("Creating RMI registry at %s/%d.\n", address.getHostAddress(), rmiPort));

    Registry registry = LocateRegistry.createRegistry(rmiPort);

    /*
     * logic for joining the chord when the node is the first in the chord.
     */
    if (!firstNode) {
      node = new ConsistentFingerPubSubChordNodeImpl(degree, address.hashCode(), rmiPort, false);
      ConsistentFingerRemotePubSubChordNode joinNode = null;
      boolean joined = false;

      while (!joined) {
        sleep(1000);
        try {
          joinNode = (ConsistentFingerRemotePubSubChordNode) Naming.lookup(String.format("rmi://%s:%s/node",
              joinNodeAddress.getHostAddress(),
              joinNodePort));
          node.join(joinNode);
          joined = true;
          /*
           * a node that is not the first node must not be available for RMI discovery
           * until it has joined the chord
           */
          registry.rebind("node", node);
          log.log(Level.INFO,
              "Successful join to join node at IP address {0}. Now in chord with IP address {1}, and node ID {2}.\n",
              InetAddress.getByName(joinNodeHostName).getHostAddress(), address.getHostAddress(), node.getId());
        } catch (IOException | NotBoundException e) {
          log.log(Level.WARNING,
              "Error occurred during node join attempt. Retrying after one second. Error was:\n{0}\nStack trace:\n{1}\n",
              e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
      }
      /*
       * logic for joining as the first node in the chord
       */
    } else {
      node = new ConsistentFingerPubSubChordNodeImpl(degree, address.hashCode(), rmiPort, true);
      // the first node can immediately be available for RMI discovery in the chord
      registry.rebind("node", node);
      log.log(Level.INFO, "Now joined the chord as the initial node with IP address {0} and node ID {1}.\n",
          address.getHostAddress(), node.getId());
    }
  }

  public Future<Boolean> subscribe(Subscription subscription) {
    return pool.submit(() -> {
      try {
        node.subscribe(subscription);
        return true;
      } catch (RemoteException e) {
        log.log(Level.WARNING, "Remote exception occurred during subscription: %s", e);
        return false;
      }
    });
  }

  public Future<Boolean> publish(Notification notification) {
    return pool.submit(() -> {
      try {
        node.publish(notification);
        return true;
      } catch (RemoteException e) {
        log.log(Level.WARNING, "Remote exception occurred during publish: %s", e);
        return false;
      }
    });
  }

  public LinkedBlockingQueue<Notification> getNotificationQueue() {
    return node.getNotificationQueue();
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
    }
  }

}
