package pubsub.subscription.predicate.number;

import pubsub.subscription.predicate.Predicate;
import pubsub.subscription.predicate.PredicateType;

public abstract class NumberPredicate<T extends Number> extends Predicate<T> {

  protected NumberPredicate(PredicateType predicateType) {
    super(predicateType);
  }

}
