package pubsub.subscription.predicate.string;

import pubsub.subscription.predicate.Predicate;
import pubsub.subscription.predicate.PredicateType;

public abstract class StringPredicate extends Predicate<String> {

  protected StringPredicate() {
    super(PredicateType.STRING);
  }

}
