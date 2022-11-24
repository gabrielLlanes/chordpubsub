package pubsub.subscription.predicate.number.lowerbound;

import pubsub.subscription.predicate.PredicateType;

public class FloatLowerBoundPredicate extends NumberLowerBoundPredicate<Float> {

  public FloatLowerBoundPredicate(float lowerBound, boolean inclusive) {
    super(PredicateType.FLOAT, lowerBound, inclusive);
  }

  @Override
  public boolean test(Float value) {
    return inclusive ? lowerBound <= value : lowerBound < value;
  }

  @Override
  public String toString() {
    return String.format("{predicate: float greater than than %s, %s inclusive}", lowerBound, inclusive);
  }

}
