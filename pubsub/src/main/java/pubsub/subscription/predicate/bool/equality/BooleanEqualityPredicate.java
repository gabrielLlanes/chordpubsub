package pubsub.subscription.predicate.bool.equality;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.BooleanNode;

import pubsub.subscription.predicate.bool.BooleanPredicate;

public class BooleanEqualityPredicate extends BooleanPredicate {

  private boolean bool;

  public BooleanEqualityPredicate(boolean bool) {
    this.bool = bool;
  }

  public boolean getBoolean() {
    return bool;
  }

  @Override
  public boolean test(BooleanNode jsonNode) {
    return !(bool ^ jsonNode.booleanValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(bool);
  }

  @Override
  public boolean equals(Object obj) {
    try {
      BooleanEqualityPredicate _obj = (BooleanEqualityPredicate) obj;
      return !(bool ^ _obj.getBoolean());
    } catch (ClassCastException e) {
      return false;
    }
  }
}
