package chord.fingertable;

import chord.node.RemotePubSubChordNode;
import pubsub.subscription.DisjunctionSubscription;
import pubsub.subscription.Subscription;

public class FingerTableEntry<T extends RemotePubSubChordNode<T>> {

  private final int id;

  private final T node;

  private DisjunctionSubscription disjunctionSubscription;

  public FingerTableEntry(int id, T node) {
    this.id = id;
    this.node = node;
    this.disjunctionSubscription = new DisjunctionSubscription();
  }

  public int getId() {
    return id;
  }

  public T getNode() {
    return node;
  }

  public void setSubscription(Subscription subscription) {
    disjunctionSubscription = new DisjunctionSubscription(subscription);
  }

  public void addSubscription(Subscription subscription) {
    disjunctionSubscription.addSubscription(subscription);
  }

  public void removeSubscription(Subscription subscription) {
    disjunctionSubscription.removeSubscription(subscription);
  }

  public DisjunctionSubscription getDisjunctionSubscription() {
    return disjunctionSubscription;
  }
}
