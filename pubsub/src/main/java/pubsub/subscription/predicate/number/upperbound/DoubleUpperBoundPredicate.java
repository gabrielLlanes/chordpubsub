package pubsub.subscription.predicate.number.upperbound;

import pubsub.subscription.predicate.PredicateType;

public class DoubleUpperBoundPredicate extends NumberUpperBoundPredicate<Double> {

  public DoubleUpperBoundPredicate(double upperBound, boolean inclusive) {
    super(PredicateType.DOUBLE, upperBound, inclusive);
  }

  @Override
  public boolean test(Double value) {
    return inclusive ? upperBound >= value : upperBound > value;
  }

}
