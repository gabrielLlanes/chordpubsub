package chord.node.consistentfinger;

import java.rmi.RemoteException;

import chord.node.RemotePubSubChordNode;

/**
 * Interface of node for a chord in which finger tables are aggressively
 * maintained.
 */
public interface ConsistentFingerRemotePubSubChordNode
    extends RemotePubSubChordNode<ConsistentFingerRemotePubSubChordNode> {

  /**
   * @param i         the index of this node's finger table which may be updated
   * @param chordNode the chordNode that might replace the ith entry of this
   *                  node's finger table
   */
  public void updateFingerTable(int i, ConsistentFingerRemotePubSubChordNode chordNode) throws RemoteException;

}
