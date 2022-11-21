package chord.node.consistentfinger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import chord.node.RemotePubSubChordNode;
import pubsub.notification.Notification;
import pubsub.subscription.SingleSubscription;
import pubsub.subscription.Subscription;
import pubsub.subscription.predicate.bool.equality.BooleanEqualityPredicate;
import pubsub.subscription.predicate.number.lowerbound.IntLowerBoundPredicate;
import pubsub.subscription.predicate.number.upperbound.DoubleUpperBoundPredicate;
import pubsub.subscription.predicate.string.equality.StringEqualityPredicate;
import pubsub.subscription.predicate.string.regex.RegexStringPredicate;

public class ConsistentFingerTest {

  final static ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void test1() throws RemoteException {
    ConsistentFingerPubSubChordNodeImpl c1 = new ConsistentFingerPubSubChordNodeImpl(8, 1, true);
    ConsistentFingerPubSubChordNodeImpl c197 = new ConsistentFingerPubSubChordNodeImpl(8, 197);

    c197.join(c1);
    Subscription subscription = new SingleSubscription.Builder()
        .addPredicate(new DoubleUpperBoundPredicate(9000.9000, false), "a")
        .addPredicate(new StringEqualityPredicate("desiredvalue"), "b")
        .build();

    Notification notification = new Notification(
        objectMapper.createObjectNode().put("a", 8999.123).put("b", "desiredvalue"));

    c1.subscribe(subscription);
    c197.subscribe(subscription);

    c1.publish(notification);

    // ConcurrentLinkedQueue<Notification> c1Queue = c1.getNotificationQueue();
    ConcurrentLinkedQueue<Notification> c197Queue = c197.getNotificationQueue();

    // assertEquals(c1Queue.size(), 1);
    assertEquals(c197Queue.size(), 1);

    // new ObjectPred
  }

  @Test
  public void test() throws RemoteException {
    ConsistentFingerRemotePubSubChordNode c1 = new ConsistentFingerPubSubChordNodeImpl(5, 1, true);
    ConsistentFingerPubSubChordNodeImpl c4 = new ConsistentFingerPubSubChordNodeImpl(5, 4);
    ConsistentFingerPubSubChordNodeImpl c9 = new ConsistentFingerPubSubChordNodeImpl(5, 9);
    ConsistentFingerPubSubChordNodeImpl c11 = new ConsistentFingerPubSubChordNodeImpl(5, 11);
    ConsistentFingerPubSubChordNodeImpl c14 = new ConsistentFingerPubSubChordNodeImpl(5, 14);
    ConsistentFingerPubSubChordNodeImpl c18 = new ConsistentFingerPubSubChordNodeImpl(5, 18);
    ConsistentFingerPubSubChordNodeImpl c20 = new ConsistentFingerPubSubChordNodeImpl(5, 20);
    ConsistentFingerPubSubChordNodeImpl c21 = new ConsistentFingerPubSubChordNodeImpl(5, 21);
    ConsistentFingerPubSubChordNodeImpl c28 = new ConsistentFingerPubSubChordNodeImpl(5, 28);

    c4.join(c1);
    c28.join(c4);
    c21.join(c28);
    c14.join(c28);

    Subscription subscription = new SingleSubscription.Builder()
        .addPredicate(new RegexStringPredicate(".*first.*|.*second.*"), "a", "aa")
        .addPredicate(new IntLowerBoundPredicate(-500, false), "b", "ba", "baa")
        .addPredicate(new BooleanEqualityPredicate(false), "c", "cb", "us")
        .build();

    c14.subscribe(subscription);

    c20.join(c28);
    c11.join(c28);
    c18.join(c28);
    c9.join(c28);
    // printNodes(c1, c4, c9, c11, c14, c18, c20, c21, c28);

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode notificationJson = mapper.createObjectNode();
    notificationJson.set("a", mapper.createObjectNode());
    ((ObjectNode) notificationJson.get("a")).put("aa", "fdsasecondfdsa");
    notificationJson.set("b", mapper.createObjectNode());
    ((ObjectNode) notificationJson.get("b")).set("ba", mapper.createObjectNode());
    ((ObjectNode) notificationJson.get("b").get("ba")).put("baa", -499);
    notificationJson.set("c", mapper.createObjectNode());
    ((ObjectNode) notificationJson.get("c")).set("cb", mapper.createObjectNode());
    ((ObjectNode) notificationJson.get("c").get("cb")).put("us", false);
    Notification notification = new Notification(notificationJson);

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

  private void printNodes(RemotePubSubChordNode<?>... chordNodes) {
    for (RemotePubSubChordNode<?> chordNode : chordNodes) {
      System.out.println(chordNode);
      System.out.println();
    }
  }

}
