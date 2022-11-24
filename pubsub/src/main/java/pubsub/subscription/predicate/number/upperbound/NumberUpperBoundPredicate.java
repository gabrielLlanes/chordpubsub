package pubsub.subscription.predicate.number.upperbound;

import java.util.Objects;

import pubsub.subscription.predicate.PredicateType;
import pubsub.subscription.predicate.number.NumberPredicate;

public abstract class NumberUpperBoundPredicate<T extends Number> extends NumberPredicate<T> {

  protected final T upperBound;

  protected final boolean inclusive;

  protected NumberUpperBoundPredicate(PredicateType predicateType, T upperBound, boolean inclusive) {
    super(predicateType);
    this.upperBound = upperBound;
    this.inclusive = inclusive;
  }

  public T getUpperBound() {
    return upperBound;
  }

  public boolean isInclusive() {
    return inclusive;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    try {
      NumberUpperBoundPredicate<T> _obj = (NumberUpperBoundPredicate<T>) obj;
      return upperBound.equals(_obj.getUpperBound()) && (!(inclusive ^ _obj.isInclusive()));
    } catch (ClassCastException e) {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(">", upperBound, inclusive);
  }

}
