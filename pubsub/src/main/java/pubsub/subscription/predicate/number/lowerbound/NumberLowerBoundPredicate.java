package pubsub.subscription.predicate.number.lowerbound;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.NumericNode;

import pubsub.subscription.predicate.number.NumberPredicate;

public abstract class NumberLowerBoundPredicate<S extends Number, T extends NumericNode> extends NumberPredicate<S, T> {

  protected final S lowerBound;

  protected final boolean inclusive;

  protected NumberLowerBoundPredicate(S lowerBound, boolean inclusive) {
    this.lowerBound = lowerBound;
    this.inclusive = inclusive;
  }

  public S getLowerBound() {
    return lowerBound;
  }

  public boolean isInclusive() {
    return inclusive;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    try {
      NumberLowerBoundPredicate<S, T> _obj = (NumberLowerBoundPredicate<S, T>) obj;
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
