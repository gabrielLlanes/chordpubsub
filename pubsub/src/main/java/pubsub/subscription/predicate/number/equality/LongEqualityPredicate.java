package pubsub.subscription.predicate.number.equality;

import pubsub.subscription.predicate.PredicateType;

public class LongEqualityPredicate extends NumberEqualityPredicate<Long> {

  public LongEqualityPredicate(Long number) {
    super(PredicateType.LONG, number);
  }

}
