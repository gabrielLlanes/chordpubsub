package pubsub.subscription.predicate.number.equality;

import com.fasterxml.jackson.databind.node.IntNode;

public class IntEqualityPredicate extends NumberEqualityPredicate<Integer, IntNode> {

  protected IntEqualityPredicate(Integer number) {
    super(number);
  }

  @Override
  public boolean test(IntNode jsonNode) {
    return number.intValue() == jsonNode.intValue();
  }
}
