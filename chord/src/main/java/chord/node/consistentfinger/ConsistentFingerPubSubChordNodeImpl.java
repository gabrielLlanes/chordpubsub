package chord.node.consistentfinger;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import chord.node.AbstractPubSubChordNode;
import chord.util.Util;

public class ConsistentFingerPubSubChordNodeImpl
    extends AbstractPubSubChordNode<ConsistentFingerRemotePubSubChordNode>
    implements ConsistentFingerRemotePubSubChordNode, ConsistentFingerPubSubChordNode {

  public ConsistentFingerPubSubChordNodeImpl(int degree, int id, boolean initial) throws RemoteException {
    super(degree, id, initial);
  }

  public ConsistentFingerPubSubChordNodeImpl(int degree, int id, int port, boolean initial) throws RemoteException {
    super(degree, id, port, initial);
  }

  @Override
  public void initFingerTable(ConsistentFingerRemotePubSubChordNode chordNode) throws RemoteException {
    ConsistentFingerRemotePubSubChordNode successor = chordNode.findSuccessor(getId());
    ConsistentFingerRemotePubSubChordNode predecessor = successor.getImmediatePredecessor();
    setImmediatePredecessor(predecessor.getId(), predecessor);
    setImmediateSuccessor(successor.getId(), successor);
    /* this new node can do a trivial update for its successor and predecessor. */
    predecessor.setImmediateSuccessor(getId(), this);
    successor.setImmediatePredecessor(getId(), this);
    /*
     * setting the finger table entries of this node using the previous entry,
     * if applicable. if not applicable, ask the parameter node for the successor of
     * the current finger table "start" position (i.e, this.id + 2^i-1 for finger
     * table entry i)
     */
    for (int i = 2; i <= degree; i++) {
      int currFingerStart = (getId() + Util.powerOf2(i - 1)) % modulus;
      ConsistentFingerRemotePubSubChordNode prevFinger = fingerTable.getNode(i - 1);
      int prevFingerId = prevFinger.getId();
      if (modulo.inOpen(currFingerStart, getId(), prevFingerId) || (getId() == prevFingerId)) {
        fingerTable.set(i, prevFingerId, prevFinger);
      } else {
        ConsistentFingerRemotePubSubChordNode foundSuccessor = chordNode.findSuccessor(currFingerStart);
        fingerTable.set(i, foundSuccessor.getId(), foundSuccessor);
        foundSuccessor.addClient(this);
      }
    }

  }

  @Override
  public void updateOthers() throws RemoteException {
    for (int i = 1; i <= degree; i++) {
      /*
       * predecessor(id - 2^(i-1)) is the first node whose ith finger table
       * entry MIGHT be updated with this node.
       */
      ConsistentFingerRemotePubSubChordNode nodeToCheck = findPredecessor(modulo.mod(getId() - Util.powerOf2(i - 1)));
      nodeToCheck.updateFingerTable(i, this);
    }
  }

  @Override
  public void updateFingerTable(int i, ConsistentFingerRemotePubSubChordNode chordNode) throws RemoteException {
    ConsistentFingerRemotePubSubChordNode fingerTableIthNode = fingerTable.getNode(i);
    int chordNodeId = chordNode.getId();
    int fingerTableIthId = fingerTable.getId(i);
    /*
     * only update the fingerTable(i) if chordNode.id < fingerTable(i)
     */
    if (modulo.inOpen(chordNodeId, getId(), fingerTableIthId) || getId() == fingerTableIthId) {
      fingerTable.set(i, chordNodeId, chordNode);
      chordNode.addClient(this);
      if (!fingerTable.containsNodeWithId(fingerTableIthId)) {
        fingerTableIthNode.removeClient(this);
      }
      /*
       * predecessor may need to apply the update only if this node applied the
       * update.
       */
      getImmediatePredecessor().updateFingerTable(i, chordNode);
    }
  }

  @Override
  public void join(ConsistentFingerRemotePubSubChordNode chordNode) throws RemoteException {
    lock.lock();
    // initialize the finger table of this node
    initFingerTable(chordNode);
    // and then update the other nodes of this node's entry into the chord.
    updateOthers();
    Set<Integer> tested = new HashSet<>();
    for (int i = 1; i <= fingerTable.size(); i++) {
      int fingerTableCurrId = fingerTable.getId(i);
      if (tested.contains(fingerTableCurrId) || fingerTableCurrId == getId()) {
        continue;
      }
      tested.add(fingerTableCurrId);
      ConsistentFingerRemotePubSubChordNode fingerTableCurrNode = fingerTable.getNode(i);
      fingerTableCurrNode.updateSubscriptionEdge(getId());
    }
    lock.unlock();
  }

  @Override
  protected ConsistentFingerRemotePubSubChordNode self() {
    return this;
  }
}
