package pubsub.subscription.predicate.number.upperbound;

import com.fasterxml.jackson.databind.node.LongNode;

public class LongUpperBoundPredicate extends NumberUpperBoundPredicate<Long, LongNode> {

  public LongUpperBoundPredicate(long upperBound, boolean inclusive) {
    super(upperBound, inclusive);
  }

  @Override
  public boolean test(LongNode jsonNode) {
    long nodeVal = jsonNode.longValue();
    return inclusive ? upperBound >= nodeVal : upperBound > nodeVal;
  }

}
