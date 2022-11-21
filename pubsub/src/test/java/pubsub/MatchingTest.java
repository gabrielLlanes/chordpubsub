package pubsub;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pubsub.notification.Notification;
import pubsub.subscription.SingleSubscription;
import pubsub.subscription.Subscription;
import pubsub.subscription.predicate.bool.equality.BooleanEqualityPredicate;
import pubsub.subscription.predicate.number.equality.DoubleEqualityPredicate;
import pubsub.subscription.predicate.number.lowerbound.FloatLowerBoundPredicate;
import pubsub.subscription.predicate.number.lowerbound.IntLowerBoundPredicate;
import pubsub.subscription.predicate.number.upperbound.IntUpperBoundPredicate;
import pubsub.subscription.predicate.string.regex.RegexStringPredicate;

public class MatchingTest {

  final ObjectMapper mapper = Notification.getMapper();

  @Test
  public void notificationMatchTest() {

    Subscription subscription = new SingleSubscription.Builder()
        .withPredicate(new IntUpperBoundPredicate(150, true), "a")
        .withPredicate(new BooleanEqualityPredicate(true), "b")
        .withPredicate(new RegexStringPredicate("^this.*string$"), "c")
        .withPredicate(new DoubleEqualityPredicate(1234.567), "d", "e", "f", "g")
        .withPredicate(new FloatLowerBoundPredicate(745.999f, false), "h", "i")
        .build();

    Notification notification = new Notification.Builder()
        ._put(150, "a")
        ._put(true, "b")
        ._put("this is a string", "c")
        ._put(1234.567, "d", "e", "f", "g")
        ._put(746f, "h", "i")
        .build();

    assertTrue(subscription.matches(notification));
  }

  @Test
  public void notificationMatchFailTest() {
    Subscription subscription = new SingleSubscription.Builder()
        .withPredicate(new RegexStringPredicate(".*first.*|.*second.*"), "a", "aa")
        .withPredicate(new IntLowerBoundPredicate(-500, false), "b", "ba", "baa")
        .withPredicate(new BooleanEqualityPredicate(false), "c", "cb", "us")
        .build();

    Notification notification = new Notification.Builder()
        ._put("fdsasecondfdsa", "a", "aa")
        ._put(345.75, "b", "ba", "baa")
        ._put("false", "c", "cb", "us")
        .build();

    assertFalse(subscription.matches(notification));
  }

  @Test
  public void asdf() {
    ObjectNode o = mapper.createObjectNode();
    JsonNode n = IntNode.valueOf(5);
    o.set("a", n);
    o.get("a");
  }
}
