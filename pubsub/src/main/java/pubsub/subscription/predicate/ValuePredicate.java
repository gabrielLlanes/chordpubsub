package pubsub.subscription.predicate;

import com.fasterxml.jackson.databind.node.ValueNode;

public interface ValuePredicate<T extends ValueNode> extends Predicate<T> {

}
