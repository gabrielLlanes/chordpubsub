package chord.node.consistentfinger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.jupiter.api.Test;

import pubsub.notification.Notification;
import pubsub.subscription.SingleSubscription;
import pubsub.subscription.Subscription;
import pubsub.subscription.predicate.bool.equality.BooleanEqualityPredicate;
import pubsub.subscription.predicate.number.lowerbound.IntLowerBoundPredicate;
import pubsub.subscription.predicate.number.upperbound.DoubleUpperBoundPredicate;
import pubsub.subscription.predicate.string.equality.StringEqualityPredicate;
import pubsub.subscription.predicate.string.regex.RegexStringPredicate;

public class ConsistentFingerTest {

  @Test
  public void testTwoNodes() throws RemoteException {
    ConsistentFingerPubSubChordNodeImpl c1 = new ConsistentFingerPubSubChordNodeImpl(8, 1, true);
    ConsistentFingerPubSubChordNodeImpl c197 = new ConsistentFingerPubSubChordNodeImpl(8, 197, false);
    c197.join(c1);

    Subscription subscription = new SingleSubscription.Builder()
        .withPredicate(new DoubleUpperBoundPredicate(9000.9000, false), "a")
        .withPredicate(new StringEqualityPredicate("desiredvalue"), "b")
        .build();

    Notification notification = new Notification.Builder()
        ._put(8999.123, "a")
        ._put("desiredvalue", "b")
        .build();

    c1.subscribe(subscription);
    c197.subscribe(subscription);

    c1.publish(notification);

    ConcurrentLinkedQueue<Notification> c197Queue = c197.getNotificationQueue();

    assertEquals(c197Queue.size(), 1);
  }

  /*
   * A notification reaches the matching subscription at one node regardless of
   * which node the notification was published at
   */
  @Test
  public void testPublishLocationIndependence() throws RemoteException {

    ConsistentFingerRemotePubSubChordNode c1 = new ConsistentFingerPubSubChordNodeImpl(5, 1, true);
    ConsistentFingerPubSubChordNodeImpl c4 = new ConsistentFingerPubSubChordNodeImpl(5, 4, false);
    ConsistentFingerPubSubChordNodeImpl c9 = new ConsistentFingerPubSubChordNodeImpl(5, 9, false);
    ConsistentFingerPubSubChordNodeImpl c11 = new ConsistentFingerPubSubChordNodeImpl(5, 11, false);
    ConsistentFingerPubSubChordNodeImpl c14 = new ConsistentFingerPubSubChordNodeImpl(5, 14, false);
    ConsistentFingerPubSubChordNodeImpl c18 = new ConsistentFingerPubSubChordNodeImpl(5, 18, false);
    ConsistentFingerPubSubChordNodeImpl c20 = new ConsistentFingerPubSubChordNodeImpl(5, 20, false);
    ConsistentFingerPubSubChordNodeImpl c21 = new ConsistentFingerPubSubChordNodeImpl(5, 21, false);
    ConsistentFingerPubSubChordNodeImpl c28 = new ConsistentFingerPubSubChordNodeImpl(5, 28, false);

    c4.join(c1);
    c28.join(c4);
    c21.join(c28);
    c14.join(c28);

    Subscription subscription = new SingleSubscription.Builder()
        .withPredicate(new RegexStringPredicate(".*first.*|.*second.*"), "a", "aa")
        .withPredicate(new IntLowerBoundPredicate(-500, false), "b", "ba", "baa")
        .withPredicate(new BooleanEqualityPredicate(false), "c", "cb", "us")
        .build();

    c14.subscribe(subscription);

    c20.join(c28);
    c11.join(c28);
    c18.join(c28);
    c9.join(c28);

    Notification notification = new Notification.Builder()
        ._put("fdsasecondfdsa", "a", "aa")
        ._put(-499, "b", "ba", "baa")
        ._put(false, "c", "cb", "us")
        .build();

    c28.publish(notification);
    c11.publish(notification);
    c21.publish(notification);
    c4.publish(notification);
    c9.publish(notification);
    c18.publish(notification);
    c20.publish(notification);

    ConcurrentLinkedQueue<Notification> notificationQueue = c14.getNotificationQueue();
    assertEquals(notificationQueue.size(), 7);
  }
}
