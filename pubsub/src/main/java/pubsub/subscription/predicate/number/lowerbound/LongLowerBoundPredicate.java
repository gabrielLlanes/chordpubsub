package pubsub.subscription.predicate.number.lowerbound;

import pubsub.subscription.predicate.PredicateType;

public class LongLowerBoundPredicate extends NumberLowerBoundPredicate<Long> {

  public LongLowerBoundPredicate(long lowerBound, boolean inclusive) {
    super(PredicateType.LONG, lowerBound, inclusive);
  }

  @Override
  public boolean test(Long value) {
    return inclusive ? lowerBound <= value : lowerBound < value;
  }

}
