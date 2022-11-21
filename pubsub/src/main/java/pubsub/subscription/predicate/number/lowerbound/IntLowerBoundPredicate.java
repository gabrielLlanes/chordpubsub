package pubsub.subscription.predicate.number.lowerbound;

import com.fasterxml.jackson.databind.node.IntNode;

public class IntLowerBoundPredicate extends NumberLowerBoundPredicate<Integer, IntNode> {

  public IntLowerBoundPredicate(int lowerBound, boolean inclusive) {
    super(lowerBound, inclusive);
  }

  @Override
  public boolean test(IntNode jsonNode) {
    int nodeVal = jsonNode.intValue();
    return inclusive ? lowerBound <= nodeVal : lowerBound < nodeVal;
  }
}
