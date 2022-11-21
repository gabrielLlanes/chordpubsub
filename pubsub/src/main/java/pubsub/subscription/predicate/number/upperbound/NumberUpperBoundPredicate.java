package pubsub.subscription.predicate.number.upperbound;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.NumericNode;

import pubsub.subscription.predicate.number.NumberPredicate;

public abstract class NumberUpperBoundPredicate<S extends Number, T extends NumericNode> extends NumberPredicate<T> {

  protected final S upperBound;

  protected final boolean inclusive;

  protected NumberUpperBoundPredicate(S upperBound, boolean inclusive) {
    this.upperBound = upperBound;
    this.inclusive = inclusive;
  }

  public S getUpperBound() {
    return upperBound;
  }

  public boolean isInclusive() {
    return inclusive;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    try {
      NumberUpperBoundPredicate<S, T> _obj = (NumberUpperBoundPredicate<S, T>) obj;
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
