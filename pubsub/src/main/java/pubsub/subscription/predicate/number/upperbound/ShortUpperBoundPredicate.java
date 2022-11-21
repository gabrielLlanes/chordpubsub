package pubsub.subscription.predicate.number.upperbound;

import com.fasterxml.jackson.databind.node.ShortNode;

public class ShortUpperBoundPredicate extends NumberUpperBoundPredicate<Short, ShortNode> {

  public ShortUpperBoundPredicate(short upperBound, boolean inclusive) {
    super(upperBound, inclusive);
  }

  @Override
  public boolean test(ShortNode jsonNode) {
    short nodeVal = jsonNode.shortValue();
    return inclusive ? upperBound >= nodeVal : upperBound > nodeVal;
  }
}
