package pubsub.subscription.predicate.number.equality;

import com.fasterxml.jackson.databind.node.FloatNode;

public class FloatEqualityPredicate extends NumberEqualityPredicate<Float, FloatNode> {

  protected FloatEqualityPredicate(Float number) {
    super(number);
  }

  @Override
  public boolean test(FloatNode jsonNode) {
    return number.floatValue() == jsonNode.floatValue();
  }
}
