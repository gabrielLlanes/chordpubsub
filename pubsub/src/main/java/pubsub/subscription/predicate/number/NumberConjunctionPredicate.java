// package pubsub.subscription.predicate.number;

// import java.util.HashSet;
// import java.util.Set;

// public class NumberConjunctionPredicate<T extends Number> extends
// NumberPredicate<T> {

// private Set<NumberPredicate<T>> predicates = new HashSet<>();

// public NumberConjunctionPredicate() {

// }

// @SuppressWarnings("unchecked")
// public NumberConjunctionPredicate(NumberPredicate<T>... predicates) {
// try {
// for (NumberPredicate<T> predicate : predicates) {
// this.predicates.add((NumberPredicate<T>) predicate);
// }
// } catch (ClassCastException e) {
// throw new IllegalArgumentException("inappropriate predicate was submitted to
// constructor", e);
// }
// }

// public Set<NumberPredicate<T>> getPredicates() {
// return predicates;
// }

// @SuppressWarnings("unchecked")
// @Override
// public boolean test(T jsonNode) {
// for (Object predicate : predicates) {
// NumberPredicate<T> _predicate = (NumberPredicate<T>) predicate;
// if (!_predicate.test(jsonNode))
// return false;
// }
// return true;
// }

// @SuppressWarnings("unchecked")
// @Override
// public boolean equals(Object obj) {
// if (obj instanceof NumberConjunctionPredicate) {
// try {
// NumberConjunctionPredicate<T> _obj = (NumberConjunctionPredicate<T>) obj;
// return this.predicates.equals(_obj.getPredicates());
// } catch (ClassCastException e) {
// return false;
// }
// }
// return false;
// }

// }
