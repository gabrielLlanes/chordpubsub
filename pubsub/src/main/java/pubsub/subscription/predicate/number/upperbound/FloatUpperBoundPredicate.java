package pubsub.subscription.predicate.number.upperbound;

import com.fasterxml.jackson.databind.node.FloatNode;

public class FloatUpperBoundPredicate extends NumberUpperBoundPredicate<Float, FloatNode> {

  public FloatUpperBoundPredicate(float upperBound, boolean inclusive) {
    super(upperBound, inclusive);
  }

  @Override
  public boolean test(FloatNode jsonNode) {
    float nodeVal = jsonNode.floatValue();
    return inclusive ? upperBound >= nodeVal : upperBound > nodeVal;
  }

}
