package pubsub.subscription.predicate.string.equality;

import java.util.Objects;

import pubsub.subscription.predicate.string.StringPredicate;

public class StringEqualityPredicate extends StringPredicate {

  private final String string;

  public StringEqualityPredicate(String string) {
    this.string = string;
  }

  @Override
  public boolean test(String value) {
    return this.string.equals(value);
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
