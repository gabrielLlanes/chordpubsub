package pubsub.subscription.predicate.number;

import pubsub.subscription.predicate.PredicateType;

public abstract class LongPredicate extends NumberPredicate<Long> {

  protected LongPredicate() {
    super(PredicateType.LONG);
  }

}
