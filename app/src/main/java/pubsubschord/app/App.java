package pubsubschord.app;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import chord.node.consistentfinger.ConsistentFingerPubSubChordNodeImpl;
import chord.node.consistentfinger.ConsistentFingerRemotePubSubChordNode;

public class App {

  private static final Logger log = System.getLogger(App.class.getName());

  private static Map<String, String> env = System.getenv();

  public static void main(String[] args) throws RemoteException, UnknownHostException, MalformedURLException {
    // the power of 2 serving as the modulus of the key space
    Integer degree = Integer.parseInt(env.get("CHORD_DEGREE"));
    // the hostname of the node to join the chord
    String hostName = env.get("CHORD_HOSTNAME");
    // port for Java RMI discovery
    Integer rmiPort = Integer.parseInt(env.get("RMI_PORT"));
    // flag to determine whether or not the node should be the first in the chord
    Boolean firstNode = Boolean.parseBoolean(env.get("CHORD_FIRST_NODE"));

    System.setProperty("java.rmi.server.hostname", hostName);

    Registry registry = LocateRegistry.createRegistry(rmiPort);
    InetAddress address = InetAddress.getByName(hostName);
    ConsistentFingerPubSubChordNodeImpl node;

    /*
     * logic for joining the chord when the node is the first in the chord.
     */
    if (!firstNode) {
      node = new ConsistentFingerPubSubChordNodeImpl(degree, address.hashCode());
      String joinNodeHostName = env.get("CHORD_JOIN_NODE_HOSTNAME");
      String joinNodePort = env.get("CHORD_JOIN_NODE_PORT");
      InetAddress joinNodeAddress = InetAddress.getByName(joinNodeHostName);
      ConsistentFingerRemotePubSubChordNode joinNode = null;
      boolean joined = false;

      while (!joined) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        try {
          joinNode = (ConsistentFingerRemotePubSubChordNode) Naming
              .lookup(String.format("rmi://%s:%s/node", joinNodeAddress.getHostAddress(),
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
              "Error occurred during node join attempt. Retrying after one second. Error was:\n{0}\n", e.getMessage());
        }
      }

      /*
       * logic for joining as the first node in the chord
       */
    } else {
      node = new ConsistentFingerPubSubChordNodeImpl(degree, address.hashCode(), true);
      // the first node can immediately be available for RMI discovery in the chord
      registry.rebind("node", node);
      log.log(Level.INFO, "Now joined the chord as the initial node with IP address {0} and node ID {1}.\n",
          address.getHostAddress(), node.getId());
    }
  }
}
