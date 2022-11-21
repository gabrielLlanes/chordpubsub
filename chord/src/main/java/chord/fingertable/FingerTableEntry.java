package chord.fingertable;

import chord.node.RemotePubSubChordNode;
import pubsub.subscription.Subscription;
import pubsub.subscription.TopicsRegexSubscription;

public class FingerTableEntry<T extends RemotePubSubChordNode<T>> {

  private final int id;

  private final T node;

  private Subscription subscription;

  public FingerTableEntry(int id, T node) {
    this.id = id;
    this.node = node;
    this.subscription = new TopicsRegexSubscription();
  }

  public int getId() {
    return id;
  }

  public T getNode() {
    return node;
  }

  public void setSubscription(Subscription subscription) {
    this.subscription = new TopicsRegexSubscription(subscription);
  }

  public void addSubscription(Subscription subscription) {
    this.subscription.merge(subscription);
  }

  public void removeSubscription(Subscription subscription) {
    //subscription.removeSubscription(subscription);
    //TODO
  }

  public Subscription getSubscription() {
    return this.subscription;
  }
}
