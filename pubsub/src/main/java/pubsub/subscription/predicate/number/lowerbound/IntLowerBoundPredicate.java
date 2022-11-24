package pubsub.subscription.predicate.number.lowerbound;

import pubsub.subscription.predicate.PredicateType;

public class IntLowerBoundPredicate extends NumberLowerBoundPredicate<Integer> {

  public IntLowerBoundPredicate(int lowerBound, boolean inclusive) {
    super(PredicateType.INT, lowerBound, inclusive);
  }

  @Override
  public boolean test(Integer value) {
    return inclusive ? lowerBound <= value : lowerBound < value;
  }
}
