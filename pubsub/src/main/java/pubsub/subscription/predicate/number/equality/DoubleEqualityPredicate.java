package pubsub.subscription.predicate.number.equality;

import pubsub.subscription.predicate.PredicateType;

public class DoubleEqualityPredicate extends NumberEqualityPredicate<Double> {

  public DoubleEqualityPredicate(Double number) {
    super(PredicateType.DOUBLE, number);
  }

  @Override
  public String toString() {
    return String.format("{predicate: double equal to %s}", number);
  }

}
