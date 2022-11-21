package pubsub.subscription;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ValueNode;

import pubsub.notification.Notification;
import pubsub.subscription.predicate.ValuePredicate;
import pubsub.subscription.predicate.object.ObjectPredicate;

public class SingleSubscription implements Subscription {

  /**
   * subscription predicates on the root object node of a notification
   */
  private final ObjectPredicate subscriptionPredicates;

  private SingleSubscription(Builder builder) {
    this.subscriptionPredicates = builder.getSubscriptionPredicates();
  }

  @Override
  public boolean matches(Notification notification) {
    return subscriptionPredicates.test(notification.getNotificationJson());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SingleSubscription)) {
      return false;
    }
    return this.subscriptionPredicates.equals(((SingleSubscription) obj).subscriptionPredicates);
  }

  @Override
  public int hashCode() {
    return subscriptionPredicates.hashCode();
  }

  public static class Builder {

    private ObjectPredicate subscriptionPredicates = new ObjectPredicate();

    private Set<List<String>> pathsApplied = new HashSet<>();

    public Builder() {
    }

    public ObjectPredicate getSubscriptionPredicates() {
      return subscriptionPredicates;
    }

    public Builder addPredicate(ValuePredicate<? extends ValueNode> predicate, String... path) {
      List<String> _path = Arrays.asList(path);
      if (!pathsApplied.contains(_path)) {
        pathsApplied.add(_path);
      } else {
        throw new IllegalArgumentException("Currently only supporting one predicate per json path");
      }
      addPredicate(predicate, _path);
      return this;
    }

    private void addPredicate(ValuePredicate<? extends ValueNode> predicate, List<String> path) {
      ObjectPredicate curr = subscriptionPredicates;
      for (int i = 0; i < path.size() - 1; i++) {
        String pathCurrPos = path.get(i);
        if (!curr.containsPredicateOnField(pathCurrPos)) {
          curr.addPredicate(pathCurrPos, new ObjectPredicate(pathCurrPos));
        }
        curr = (ObjectPredicate) curr.getPredicateOnField(pathCurrPos);
      }
      curr.addPredicate(path.get(path.size() - 1), predicate);
    }

    public SingleSubscription build() {
      return new SingleSubscription(this);
    }
  }
}
