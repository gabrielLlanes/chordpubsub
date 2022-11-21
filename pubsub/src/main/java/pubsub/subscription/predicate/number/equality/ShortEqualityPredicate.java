package pubsub.subscription.predicate.number.equality;

import com.fasterxml.jackson.databind.node.ShortNode;

public class ShortEqualityPredicate extends NumberEqualityPredicate<Short, ShortNode> {

  protected ShortEqualityPredicate(Short number) {
    super(number);
  }

  @Override
  public boolean test(ShortNode jsonNode) {
    return number.shortValue() == jsonNode.shortValue();
  }

}
