package pubsub.subscription.predicate.number;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.node.NumericNode;

public class NumberConjunctionPredicate<S extends Number, T extends NumericNode> extends NumberPredicate<S, T> {

  private Set<Object> predicates = new HashSet<>();

  public NumberConjunctionPredicate() {

  }

  @SuppressWarnings("unchecked")
  public NumberConjunctionPredicate(NumberPredicate<?, ?>... predicates) {
    try {
      for (NumberPredicate<?, ?> predicate : predicates) {
        this.predicates.add((NumberPredicate<S, T>) predicate);
      }
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("inappropriate predicate was submitted to constructor", e);
    }
  }

  public Set<Object> getPredicates() {
    return predicates;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean test(T jsonNode) {
    for (Object predicate : predicates) {
      NumberPredicate<S, T> _predicate = (NumberPredicate<S, T>) predicate;
      if (!_predicate.test(jsonNode))
        return false;
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NumberConjunctionPredicate) {
      try {
        NumberConjunctionPredicate<S, T> _obj = (NumberConjunctionPredicate<S, T>) obj;
        return this.predicates.equals(_obj.getPredicates());
      } catch (ClassCastException e) {
        return false;
      }
    }
    return false;
  }

}
