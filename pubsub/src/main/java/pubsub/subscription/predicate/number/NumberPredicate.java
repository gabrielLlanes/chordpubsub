package pubsub.subscription.predicate.number;

import com.fasterxml.jackson.databind.node.NumericNode;

import pubsub.subscription.predicate.ValuePredicate;

public abstract class NumberPredicate<T extends NumericNode> implements ValuePredicate<T> {

}
