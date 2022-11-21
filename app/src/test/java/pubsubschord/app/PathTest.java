package pubsubschord.app;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.Filter;
import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.JsonPath.parse;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PathTest {
  @Test
  public void asdf() {
    Filter f = Filter.filter(where("asdf").gt(7));
    var o = parse("{\"asdf\":6}").read("$.asdf", f);
    System.out.println(o);

    Predicate<Integer> pInt = new IntPredicate();
    Predicate<Float> pFloat = new FloatPredicate();

    Set<Predicate<? extends Number>> preds = new HashSet<>();

    preds.add(pInt);
    preds.add(pFloat);
  }

  private static class IntPredicate implements Predicate<Integer> {
    @Override
    public boolean test(Integer n) {
      return n < 5;
    }
  }

  private static class FloatPredicate implements Predicate<Float> {
    @Override
    public boolean test(Float t) {
      return t == 3.141f;
    }
  }
}
