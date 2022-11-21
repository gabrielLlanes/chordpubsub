package chord.node;

import java.rmi.Remote;
import java.rmi.RemoteException;

import chord.util.ModuloInterval;
import pubsub.subscription.Subscription;

/**
 * Node in a chord network for pub-sub. Remote interface to be exposed. There is
 * no storing of key-value pairs in this network
 * 
 * @param <T> the extending interface
 * @see <a href="https://dl.acm.org/doi/pdf/10.1145/383059.383071">Chord: A
 *      Scalable Peer-to-peer Lookup Service for Internet Applications</a>
 * @see <a href="https://dl.acm.org/doi/abs/10.1145/966618.966627">A
 *      peer-to-peer approach to content-based publish/subscribe</a>
 */
public interface RemotePubSubChordNode<T extends RemotePubSubChordNode<T>>
    extends Remote {

  /**
   * 
   * @return the id of this node within the chord network
   */
  public int getId() throws RemoteException;

  /**
   * 
   * @return a list of integers which represents the keys under this node's
   *         management.
   */
  public ModuloInterval getManaged() throws RemoteException;

  /**
   * Ask this node for the keys which it assigns the node with id
   * responsibility for
   * 
   * @param id the id of the node asking for keys which this node assigns
   *           responsibility for
   * @return the keys of the node with id that this node assigns responsibility
   *         for
   */
  public ModuloInterval askForAssigned(int id) throws RemoteException;

  /**
   * Notify this node of a client to this node (contains this node in its finger
   * table)
   * 
   * @param client the node that is a client of this node
   */
  public void addClient(T client) throws RemoteException;

  /**
   * Notify this node that the parameter node is no longer a client
   * 
   * @param client the node that is not longer of this node
   */
  public void removeClient(T client) throws RemoteException;

  /**
   * @return this node's immediate successor node in the chord network
   */
  public T getImmediateSuccessor() throws RemoteException;

  /**
   * @return this node's immediate precedessor node in the chord network
   */
  public T getImmediatePredecessor() throws RemoteException;

  /**
   * set the immediate sucessor of this node within the chord network.
   * Useful for updating the finger table.
   * 
   * @param successor the node which will be the new predecessor of this node
   */
  public void setImmediateSuccessor(int id, T successor) throws RemoteException;

  /**
   * set the immediate predecessor of this node within the chord network.
   * Useful for updating the finger table.
   * 
   * @param predecessor the node which will be the new predecessor of this node
   */
  public void setImmediatePredecessor(int id, T predecessor) throws RemoteException;

  /**
   *
   * @param n the value whose closest preceding node in this node's finger table
   *          should be found
   * @return the node of this node's finger table which is the closest
   *         preceding finger table node of n
   */
  public T closestPrecedingFingerNode(int n) throws RemoteException;

  /**
   * request for this node to find the successor of the value n.
   * 
   * @param n the value whose successor within the chord network should be found
   */
  public T findSuccessor(int n) throws RemoteException;

  /**
   * request for this node to find the predecessor of the value n.
   * 
   * @param n the value whose precedessor within the chord network should be found
   */
  public T findPredecessor(int n) throws RemoteException;

  /**
   * Publish a notification to this node in the form of a serialized JSON string
   * using the multicast algorithm covered in the paper. This method should only
   * be called if a node is calling this method on itself or if the notification
   * matches the subscription of the graph edge
   * 
   * @param notificationDataJsonString the notification to be published to this
   *                                   node
   */
  public void publish(String topic, String notificationDataJsonString, int publisherId, ModuloInterval range)
      throws RemoteException;

  /**
   * Tell this node to update the subscription of the edge connecting this
   * node with the caller, with the given subscription
   * 
   * @param subscription the subscription with which to update the graph edge
   * @param id           the id of the caller
   */
  public void subscribe(Subscription subscription, int id) throws RemoteException;

  // public void unsubscribe(Subscription subscription) throws RemoteException;

  /**
   * Tell this node that the current subscription routing information is requested
   * 
   * @param clientId the id of the client node requesting the routing information
   *                 update
   */
  public void updateSubscriptionEdge(int clientId) throws RemoteException;
}
