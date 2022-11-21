package pubsub.subscription.predicate.number.lowerbound;

import com.fasterxml.jackson.databind.node.LongNode;

public class LongLowerBoundPredicate extends NumberLowerBoundPredicate<Long, LongNode> {

  public LongLowerBoundPredicate(long lowerBound, boolean inclusive) {
    super(lowerBound, inclusive);
  }

  @Override
  public boolean test(LongNode jsonNode) {
    long nodeVal = jsonNode.longValue();
    return inclusive ? lowerBound <= nodeVal : lowerBound < nodeVal;
  }

}
