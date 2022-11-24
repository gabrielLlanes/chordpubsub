package pubsub.subscription.predicate.number;

import pubsub.subscription.predicate.PredicateType;

public abstract class FloatPredicate extends NumberPredicate<Float> {

  protected FloatPredicate() {
    super(PredicateType.FLOAT);
  }

}
