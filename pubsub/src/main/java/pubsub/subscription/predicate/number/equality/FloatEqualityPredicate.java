package pubsub.subscription.predicate.number.equality;

import pubsub.subscription.predicate.PredicateType;

public class FloatEqualityPredicate extends NumberEqualityPredicate<Float> {

  public FloatEqualityPredicate(Float number) {
    super(PredicateType.FLOAT, number);
  }

}
