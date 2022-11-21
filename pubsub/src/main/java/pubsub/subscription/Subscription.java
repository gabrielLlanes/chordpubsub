package pubsub.subscription;

import java.io.Serializable;

import pubsub.notification.Notification;

public interface Subscription extends Serializable {

  public boolean matches(Notification notification);

}
