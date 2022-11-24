package pubsub.subscription.predicate.number.upperbound;

import pubsub.subscription.predicate.PredicateType;

public class IntUpperBoundPredicate extends NumberUpperBoundPredicate<Integer> {

  public IntUpperBoundPredicate(int upperBound, boolean inclusive) {
    super(PredicateType.INT, upperBound, inclusive);
  }

  @Override
  public boolean test(Integer value) {
    return inclusive ? upperBound >= value : upperBound > value;
  }
}
