package pubsub.subscription.predicate.number.lowerbound;

import pubsub.subscription.predicate.PredicateType;

public class DoubleLowerBoundPredicate extends NumberLowerBoundPredicate<Double> {

  public DoubleLowerBoundPredicate(double lowerBound, boolean inclusive) {
    super(PredicateType.DOUBLE, lowerBound, inclusive);
  }

  @Override
  public boolean test(Double value) {
    return inclusive ? lowerBound <= value : lowerBound < value;
  }

}
