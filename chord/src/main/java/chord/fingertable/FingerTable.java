package chord.fingertable;

import chord.node.RemotePubSubChordNode;
import chord.util.Util;
import chord.util.ModuloInterval;
import pubsub.subscription.Subscription;

/**
 * Finger table interface for a node. A finger table should have its predecessor
 * as the 0th entry, and for each i with 1 <= i <= degree of chord,
 * the ith entry should be successor(node.id + 2^(i-1))
 */
public interface FingerTable<T extends RemotePubSubChordNode<T>> {

  public int getId();

  public default int start(int i) {
    int id = getId();
    if (i == 1) {
      return id + 1;
    }
    return id + Util.powerOf2(i - 1);
  }

  public int size();

  public boolean containsNodeWithId(int id);

  public void set(int index, int id, T chordNode);

  public T getNode(int index);

  /**
   * Get the id in the chord of the node at the given index into the finger table
   * 
   * @param index the index of the node in the finger table whose id is returned
   * @return the id of the node in the {@code index} index of the finger table
   */
  public int getId(int index);

  /**
   * Get the interval that the maintainer node of this finger table holds the node
   * at the index into the finger table responsible for
   * 
   * @param i the index into the finger table for which to get the interval
   *          assigned by the maintainer node of this finger table.
   * @return the interval assigned by the maintainer node of this finger table to
   *         the node at the index.
   */
  public ModuloInterval assignedToEntry(int i);

  /**
   * Get the interval that the maintainer node of this fingertable holds the node
   * with the given id responsible for.
   * 
   * @param id the id of the node whose assigned interval to get
   * @return the interval that is assigned to the node with the given id, or null
   *         if there is no node with the given id in the finger table.
   */
  public ModuloInterval assignedToNode(int id);

  public void setSubscriptionToEntry(int index, Subscription subscription);

  public void addSubscriptionToEntry(int index, Subscription subscription);

  public void addSubscriptionToNode(int id, Subscription subscription);

  public Subscription getSubscriptionOfEntry(int index);

  public void removeSubscriptionOfEntry(int index, Subscription subscription);

}
