package pubsub.subscription.predicate.number;

import pubsub.subscription.predicate.PredicateType;

public abstract class DoublePredicate extends NumberPredicate<Double> {

  protected DoublePredicate() {
    super(PredicateType.DOUBLE);
  }

}
