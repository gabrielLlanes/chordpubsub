package pubsub.subscription.predicate.number.upperbound;

import com.fasterxml.jackson.databind.node.IntNode;

public class IntUpperBoundPredicate extends NumberUpperBoundPredicate<Integer, IntNode> {

  public IntUpperBoundPredicate(int upperBound, boolean inclusive) {
    super(upperBound, inclusive);
  }

  @Override
  public boolean test(IntNode jsonNode) {
    int nodeVal = jsonNode.intValue();
    return inclusive ? upperBound >= nodeVal : upperBound > nodeVal;
  }
}
