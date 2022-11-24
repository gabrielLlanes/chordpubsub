package pubsub.subscription.predicate.string.regex;

import java.util.Objects;
import java.util.regex.Pattern;

import pubsub.subscription.predicate.string.StringPredicate;

public class RegexStringPredicate extends StringPredicate {

  private final String regex;

  public RegexStringPredicate(String regex) {
    this.regex = regex;
  }

  public String getRegex() {
    return regex;
  }

  @Override
  public boolean test(String value) {
    return Pattern.matches(regex, value);
  }

  @Override
  public boolean equals(Object obj) {
    try {
      RegexStringPredicate _obj = (RegexStringPredicate) obj;
      return regex.equals(_obj.getRegex());
    } catch (ClassCastException e) {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash("regexstringpredicate", regex);
  }

  @Override
  public String toString() {
    return String.format("{predicate: %s regex}", regex);
  }

}
