package pubsub.subscription.predicate.number.lowerbound;

import com.fasterxml.jackson.databind.node.DoubleNode;

public class DoubleLowerBoundPredicate extends NumberLowerBoundPredicate<Double, DoubleNode> {

  public DoubleLowerBoundPredicate(double lowerBound, boolean inclusive) {
    super(lowerBound, inclusive);
  }

  @Override
  public boolean test(DoubleNode jsonNode) {
    double nodeVal = jsonNode.doubleValue();
    return inclusive ? lowerBound <= nodeVal : lowerBound < nodeVal;
  }

}
