package pubsub.subscription.predicate.string.equality;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.TextNode;

import pubsub.subscription.predicate.ValuePredicate;

public class StringEqualityPredicate implements ValuePredicate<TextNode> {

  private final String string;

  public StringEqualityPredicate(String string) {
    this.string = string;
  }

  @Override
  public boolean test(TextNode jsonNode) {
    return jsonNode.textValue().equals(string);
  }

  public String getString() {
    return string;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof StringEqualityPredicate) {
      return string.equals(((StringEqualityPredicate) obj).getString());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash("stringequalitypredicate", string);
  }

}
