package pubsub.subscription.predicate.number.lowerbound;

import com.fasterxml.jackson.databind.node.FloatNode;

public class FloatLowerBoundPredicate extends NumberLowerBoundPredicate<Float, FloatNode> {

  public FloatLowerBoundPredicate(float lowerBound, boolean inclusive) {
    super(lowerBound, inclusive);
  }

  @Override
  public boolean test(FloatNode jsonNode) {
    float nodeVal = jsonNode.floatValue();
    return inclusive ? lowerBound <= nodeVal : lowerBound < nodeVal;
  }

}
