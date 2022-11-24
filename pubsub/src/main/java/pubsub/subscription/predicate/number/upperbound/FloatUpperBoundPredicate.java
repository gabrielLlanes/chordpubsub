package pubsub.subscription.predicate.number.upperbound;

import pubsub.subscription.predicate.PredicateType;

public class FloatUpperBoundPredicate extends NumberUpperBoundPredicate<Float> {

  public FloatUpperBoundPredicate(float upperBound, boolean inclusive) {
    super(PredicateType.FLOAT, upperBound, inclusive);
  }

  @Override
  public boolean test(Float value) {
    return inclusive ? upperBound >= value : upperBound > value;
  }

}
