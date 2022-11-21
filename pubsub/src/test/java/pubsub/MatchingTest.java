package pubsub;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pubsub.notification.Notification;
import pubsub.subscription.Subscription;
import pubsub.subscription.SingleSubscription;
import pubsub.subscription.predicate.bool.equality.BooleanEqualityPredicate;
import pubsub.subscription.predicate.number.lowerbound.IntLowerBoundPredicate;
import pubsub.subscription.predicate.string.regex.RegexStringPredicate;

public class MatchingTest {

  final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void notificationMatchTest() {
    Subscription subscription = new SingleSubscription.Builder()
        .addPredicate(new RegexStringPredicate(".*first.*|.*second.*"), "a", "aa")
        .addPredicate(new IntLowerBoundPredicate(-500, false), "b", "ba", "baa")
        .addPredicate(new BooleanEqualityPredicate(false), "c", "cb", "us")
        .build();

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

    assertTrue(subscription.matches(notification));
  }
}
