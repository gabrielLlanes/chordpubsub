package pubsub.subscription.predicate.bool;

import pubsub.subscription.predicate.Predicate;
import pubsub.subscription.predicate.PredicateType;

public abstract class BooleanPredicate extends Predicate<Boolean> {

  protected BooleanPredicate() {
    super(PredicateType.BOOLEAN);
  }

}
