package pubsub.subscription.predicate.number.upperbound;

import com.fasterxml.jackson.databind.node.DoubleNode;

public class DoubleUpperBoundPredicate extends NumberUpperBoundPredicate<Double, DoubleNode> {

  public DoubleUpperBoundPredicate(double upperBound, boolean inclusive) {
    super(upperBound, inclusive);
  }

  @Override
  public boolean test(DoubleNode jsonNode) {
    double nodeVal = jsonNode.doubleValue();
    return inclusive ? upperBound >= nodeVal : upperBound > nodeVal;
  }

}
