package pubsub.subscription.predicate.number.equality;

import pubsub.subscription.predicate.PredicateType;

public class IntEqualityPredicate extends NumberEqualityPredicate<Integer> {

  public IntEqualityPredicate(Integer number) {
    super(PredicateType.INT, number);
  }

}
