package pubsub.subscription.predicate.number.upperbound;

import pubsub.subscription.predicate.PredicateType;

public class LongUpperBoundPredicate extends NumberUpperBoundPredicate<Long> {

  public LongUpperBoundPredicate(long upperBound, boolean inclusive) {
    super(PredicateType.LONG, upperBound, inclusive);
  }

  @Override
  public boolean test(Long value) {
    return inclusive ? upperBound >= value : upperBound > value;
  }

}
