package pubsub.subscription.predicate.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pubsub.subscription.predicate.Predicate;

public class ObjectPredicate implements Predicate<ObjectNode> {

  public Map<String, Predicate<? extends JsonNode>> predicates = new HashMap<>();

  private final String rootName;

  public ObjectPredicate(String rootName) {
    this.rootName = rootName;
  }

  public ObjectPredicate() {
    // "notification_root" shall indicate the root of a subscription.
    this("notification_root");
  }

  public String getRootName() {
    return rootName;
  }

  public boolean containsPredicateOnField(String field) {
    return predicates.containsKey(field);
  }

  public Predicate<? extends JsonNode> getPredicateOnField(String field) {
    return predicates.get(field);
  }

  public Map<String, Predicate<? extends JsonNode>> getPredicates() {
    return predicates;
  }

  /*
   * Test the predicate on each field against the value of the field.
   */
  public boolean test(ObjectNode objectNode) {
    for (Entry<String, Predicate<? extends JsonNode>> entry : predicates.entrySet()) {
      JsonNode jsonNode = objectNode.get(entry.getKey());
      Predicate<? extends JsonNode> predicate = entry.getValue();
      if (!predicate._test(jsonNode))
        return false;
    }
    return true;
  }

  public void addPredicate(String field, Predicate<? extends JsonNode> predicate) {
    predicates.put(field, predicate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(predicates, rootName);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ObjectPredicate) {
      ObjectPredicate _obj = (ObjectPredicate) obj;
      return this.rootName.equals(_obj.getRootName()) && this.predicates.equals(_obj.getPredicates());
    }
    return false;
  }

}
