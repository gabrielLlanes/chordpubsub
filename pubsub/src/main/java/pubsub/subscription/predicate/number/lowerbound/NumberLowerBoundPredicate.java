package pubsub.subscription.predicate.number.lowerbound;

import java.util.Objects;

import pubsub.subscription.predicate.PredicateType;
import pubsub.subscription.predicate.number.NumberPredicate;

public abstract class NumberLowerBoundPredicate<T extends Number> extends NumberPredicate<T> {

  protected final T lowerBound;

  protected final boolean inclusive;

  protected NumberLowerBoundPredicate(PredicateType predicateType, T lowerBound, boolean inclusive) {
    super(predicateType);
    this.lowerBound = lowerBound;
    this.inclusive = inclusive;
  }

  public T getLowerBound() {
    return lowerBound;
  }

  public boolean isInclusive() {
    return inclusive;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    try {
      NumberLowerBoundPredicate<T> _obj = (NumberLowerBoundPredicate<T>) obj;
      return lowerBound.equals(_obj.getLowerBound()) && (!(inclusive ^ _obj.isInclusive()));
    } catch (ClassCastException e) {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash("<", lowerBound, inclusive);
  }

}
