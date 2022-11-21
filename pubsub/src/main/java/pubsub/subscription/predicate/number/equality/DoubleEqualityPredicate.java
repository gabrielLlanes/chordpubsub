package pubsub.subscription.predicate.number.equality;

import com.fasterxml.jackson.databind.node.DoubleNode;

public class DoubleEqualityPredicate extends NumberEqualityPredicate<Double, DoubleNode> {

  protected DoubleEqualityPredicate(Double number) {
    super(number);
  }

  @Override
  public boolean test(DoubleNode jsonNode) {
    return number.doubleValue() == jsonNode.doubleValue();
  }

}
