package pubsub.subscription.predicate.number.equality;

import java.util.Objects;

import pubsub.subscription.predicate.PredicateType;
import pubsub.subscription.predicate.number.NumberPredicate;

public abstract class NumberEqualityPredicate<T extends Number> extends NumberPredicate<T> {

  protected final T number;

  protected NumberEqualityPredicate(PredicateType predicateType, T number) {
    super(predicateType);
    this.number = number;
  }

  public T getNumber() {
    return number;
  }

  @Override
  public final boolean test(T value) {
    return value.equals(number);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    try {
      NumberEqualityPredicate<T> _obj = (NumberEqualityPredicate<T>) obj;
      return number.equals(_obj.getNumber());
    } catch (ClassCastException e) {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash("=", number);
  }
}
