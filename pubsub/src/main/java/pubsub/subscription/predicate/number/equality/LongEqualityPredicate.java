package pubsub.subscription.predicate.number.equality;

import com.fasterxml.jackson.databind.node.LongNode;

public class LongEqualityPredicate extends NumberEqualityPredicate<Long, LongNode> {

  protected LongEqualityPredicate(Long number) {
    super(number);
  }

  @Override
  public boolean test(LongNode jsonNode) {
    return number.longValue() == jsonNode.longValue();
  }
}
