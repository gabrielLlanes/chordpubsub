package pubsub.subscription.predicate.number;

import pubsub.subscription.predicate.PredicateType;

public abstract class IntPredicate extends NumberPredicate<Integer> {

  protected IntPredicate() {
    super(PredicateType.INT);
  }

}
