package pubsub.subscription;

import pubsub.notification.Notification;

public interface Subscription extends java.io.Serializable {

  public boolean matches(Notification notification);

}
