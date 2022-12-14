package pubsub.subscription;

import java.util.HashSet;
import java.util.Set;

import pubsub.notification.Notification;

public class DisjunctionSubscription extends Subscription {

  private Set<Subscription> subscriptions;

  public DisjunctionSubscription(Set<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }

  public DisjunctionSubscription(Subscription... subscriptions) {
    this.subscriptions = new HashSet<>();
    for (Subscription subscription : subscriptions) {
      if (subscription != null)
        addSubscription(subscription);
    }
  }

  public int size() {
    return subscriptions.size();
  }

  public DisjunctionSubscription() {
    this.subscriptions = new HashSet<>();
  }

  public Set<Subscription> getSubscriptions() {
    return subscriptions;
  }

  public void addSubscription(Subscription subscription) {
    if (subscription instanceof DisjunctionSubscription) {
      for(Subscription _subscription: ((DisjunctionSubscription) subscription).getSubscriptions()) {
        addSubscription(_subscription);
      }
    } else if(subscription instanceof SingleSubscription) {
      if(!subscriptions.contains(subscription)) {
        subscriptions.add(subscription);
      }
    }
  }

  public void removeSubscription(Subscription subscription) {
    subscriptions.remove(subscription);
  }

  @Override
  public boolean matches(Notification notification) {
    for (Subscription subscription : subscriptions) {
      if (subscription.matches(notification)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DisjunctionSubscription) {
      return subscriptions.equals(((DisjunctionSubscription) obj).subscriptions);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return subscriptions.hashCode();
  }
}