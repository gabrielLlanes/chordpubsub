package pubsub.subscription.predicate.number.equality;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.NumericNode;

import pubsub.subscription.predicate.number.NumberPredicate;

public abstract class NumberEqualityPredicate<S extends Number, T extends NumericNode> extends NumberPredicate<S, T> {

  protected final S number;

  protected NumberEqualityPredicate(S number) {
    this.number = number;
  }

  public S getNumber() {
    return number;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    try {
      NumberEqualityPredicate<S, T> _obj = (NumberEqualityPredicate<S, T>) obj;
      return number.equals(_obj.getNumber());
    } catch (ClassCastException e) {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash("=", number);
  }
}
