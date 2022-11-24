package pubsub.subscription.predicate.string.regex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RegexStringPredicateUnitTest {
  @Test
  public void testRegexPredicateSimpleMatch() {
    RegexStringPredicate predicate = new RegexStringPredicate("matches");
    assertTrue(predicate.test("matches"));
  }

  @Test
  public void testRegexPredicateWildcardMatch() {
    RegexStringPredicate predicate = new RegexStringPredicate(".*matching.*working.*");
    assertTrue(predicate.test("Iwonderifthematchingisworking."));
  }
}
