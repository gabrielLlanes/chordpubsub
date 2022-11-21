package pubsub.subscription;

import pubsub.notification.Notification;

import java.util.UUID;

public abstract class Subscription implements java.io.Serializable {

  public abstract boolean matches(Notification notification);

  public void merge(Subscription subscription) {

  }

}
