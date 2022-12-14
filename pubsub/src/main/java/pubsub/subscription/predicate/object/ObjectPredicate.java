package pubsub.subscription.predicate.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import pubsub.subscription.predicate.Predicate;
import pubsub.subscription.predicate.PredicateType;

public class ObjectPredicate extends Predicate<ObjectNode> {

  public Map<String, Predicate<?>> predicates = new HashMap<>();

  private final String rootName;

  public ObjectPredicate(String rootName) {
    super(PredicateType.OBJECT);
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

  public Predicate<?> getPredicateOnField(String field) {
    return predicates.get(field);
  }

  public Map<String, Predicate<?>> getPredicates() {
    return predicates;
  }

  /*
   * Test the predicate on each field against the value of the field.
   */
  public boolean test(ObjectNode objectNode) {
    for (Entry<String, Predicate<?>> entry : predicates.entrySet()) {
      JsonNode jsonNode = objectNode.get(entry.getKey());
      Predicate<?> predicate = entry.getValue();
      boolean test = false;
      if (jsonNode == null) {
        return false;
      }
      if (jsonNode instanceof ValueNode) {
        ValueNode valueNode = (ValueNode) jsonNode;
        if (valueNode instanceof BooleanNode) {
          test = predicate.testWithTypeCastHandling(valueNode.booleanValue());
        } else if (valueNode instanceof IntNode && !(predicate.getPredicateType() == PredicateType.LONG)) {
          test = predicate.testWithTypeCastHandling(valueNode.intValue());
        } else if (valueNode instanceof IntNode && predicate.getPredicateType() == PredicateType.LONG) {
          test = predicate.testWithTypeCastHandling(valueNode.longValue());
        } else if (valueNode instanceof LongNode) {
          test = predicate.testWithTypeCastHandling(valueNode.longValue());
        } else if (valueNode instanceof FloatNode && !(predicate.getPredicateType() == PredicateType.DOUBLE)) {
          test = predicate.testWithTypeCastHandling(valueNode.floatValue());
        } else if (valueNode instanceof FloatNode && predicate.getPredicateType() == PredicateType.DOUBLE) {
          test = predicate.testWithTypeCastHandling(valueNode.doubleValue());
        } else if (valueNode instanceof DoubleNode && !(predicate.getPredicateType() == PredicateType.FLOAT)) {
          test = predicate.testWithTypeCastHandling(valueNode.doubleValue());
        } else if (valueNode instanceof DoubleNode && predicate.getPredicateType() == PredicateType.FLOAT) {
          test = predicate.testWithTypeCastHandling(valueNode.floatValue());
        } else if (valueNode instanceof TextNode) {
          test = predicate.testWithTypeCastHandling(valueNode.textValue());
        } else {
          return false;
        }
      } else if (jsonNode instanceof ObjectNode) {
        ObjectNode nestedObjectNode = (ObjectNode) jsonNode;
        test = predicate.testWithTypeCastHandling(nestedObjectNode);
      } else {
        return false;
      }
      if (!test) {
        return false;
      }
    }
    return true;
  }

  public void addPredicate(String field, Predicate<?> predicate) {
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

  @Override
  public String toString() {
    return String.format("{object predicate: %s}", predicates);
  }

}
