package pubsub.subscription.predicate;

/**
 * Predicate that can be applied to a single value
 * 
 * @param <T> the type of value for which the predicate can be
 *            applied.
 */
public abstract class Predicate<T> implements java.util.function.Predicate<T>, java.io.Serializable {

  protected final PredicateType predicateType;

  protected Predicate(PredicateType predicateType) {
    this.predicateType = predicateType;
  }

  public PredicateType getPredicateType() {
    return predicateType;
  }

  public abstract boolean test(T value);

  @SuppressWarnings("unchecked")
  public boolean testWithTypeCastHandling(Object value) {
    if (value == null)
      return false;
    T typeCastValue;
    try {
      typeCastValue = (T) value;
      boolean test = test(typeCastValue);
      return test;
    } catch (ClassCastException e) {
      return false;
    }
  }
}
