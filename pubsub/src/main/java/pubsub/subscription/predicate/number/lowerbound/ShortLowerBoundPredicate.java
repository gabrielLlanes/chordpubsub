package pubsub.subscription.predicate.number.lowerbound;

import com.fasterxml.jackson.databind.node.ShortNode;

public class ShortLowerBoundPredicate extends NumberLowerBoundPredicate<Short, ShortNode> {

  public ShortLowerBoundPredicate(short lowerBound, boolean inclusive) {
    super(lowerBound, inclusive);
  }

  @Override
  public boolean test(ShortNode jsonNode) {
    short nodeVal = jsonNode.shortValue();
    return inclusive ? lowerBound <= nodeVal : lowerBound < nodeVal;
  }
}
