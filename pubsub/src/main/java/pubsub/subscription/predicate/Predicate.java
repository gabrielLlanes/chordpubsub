package pubsub.subscription.predicate;

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Predicate that can be applied to a single JsonNode
 * 
 * @param <T> the extending class of JsonNode for which the predicate can be
 *            applied.
 */
public interface Predicate<T extends JsonNode> extends java.util.function.Predicate<T>, Serializable {

  public boolean test(T jsonNode);

  @SuppressWarnings("unchecked")
  public default boolean _test(JsonNode jsonNode) {
    if (jsonNode == null)
      return false;
    T _jsonNode;
    try {
      _jsonNode = (T) jsonNode;
    } catch (ClassCastException e) {
      return false;
    }
    return test(_jsonNode);
  }
}
