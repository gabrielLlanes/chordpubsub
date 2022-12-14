package pubsub.subscription;

import java.util.*;

import pubsub.notification.Notification;
import pubsub.subscription.predicate.Predicate;
import pubsub.subscription.predicate.object.ObjectPredicate;

public class SingleSubscription extends Subscription {

  /**
   * subscription predicates on the root object node of a notification
   */
  private final ObjectPredicate subscriptionPredicates;

  private final UUID id = UUID.randomUUID();

  private SingleSubscription(Builder builder) {
    this.subscriptionPredicates = builder.getSubscriptionPredicates();
  }

  public UUID getId() {
    return id;
  }

  @Override
  public boolean matches(Notification notification) {
    if (notification == null) {
      return false;
    }
    return subscriptionPredicates.test(notification.getNotificationJson());
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SingleSubscription)) {
      return false;
    }
    SingleSubscription subscription = (SingleSubscription) obj;
    return id.equals(subscription.getId()) && subscriptionPredicates.equals(subscription.subscriptionPredicates);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return subscriptionPredicates.toString();
  }

  public static class Builder {

    private ObjectPredicate subscriptionPredicates = new ObjectPredicate();

    private Set<List<String>> pathsApplied = new HashSet<>();

    public Builder() {
    }

    public ObjectPredicate getSubscriptionPredicates() {
      return subscriptionPredicates;
    }

    public Builder withPredicate(Predicate<?> predicate, String... path) {
      if (path.length < 1) {
        throw new IllegalArgumentException("Specified path must not be empty");
      }
      List<String> _path = Arrays.asList(path);
      if (!pathsApplied.contains(_path)) {
        pathsApplied.add(_path);
      } else {
        throw new IllegalArgumentException("Currently only supporting one predicate per JSON path");
      }
      addPredicate(predicate, _path);
      return this;
    }

    private void addPredicate(Predicate<?> predicate, List<String> path) {
      ObjectPredicate curr = subscriptionPredicates;
      int i;
      for (i = 0; i < path.size() - 1; i++) {
        String pathCurrPos = path.get(i);
        if (!curr.containsPredicateOnField(pathCurrPos)) {
          curr.addPredicate(pathCurrPos, new ObjectPredicate(pathCurrPos));
        }
        curr = (ObjectPredicate) curr.getPredicateOnField(pathCurrPos);
      }
      curr.addPredicate(path.get(i), predicate);
    }

    public SingleSubscription build() {
      return new SingleSubscription(this);
    }
  }
}
