package chord.node.consistentfinger;

import java.nio.file.Path;
import java.util.Random;

/*
 * Class for generating random strings.
 */
public class RandomString {
  static Random rg = new Random();
  static int l_lower = 'a';
  static int h_lower = 'z';
  static int diff_lower = h_lower - l_lower;
  static int l_upper = 'A';
  static int h_upper = 'Z';
  static int diff_upper = h_upper - l_upper;
  static int l_num = '0';
  static int h_num = '9';
  static int diff_num = h_num - l_num;

  static final int STRING_SIZE = 8192;

  static Path oPath = Path.of("./long-text.txt");

  static public String getRandomAlnumString(int n) {
    if (n < 0)
      return "default";
    String s = "";
    for (int i = 0; i < n - 1; i++) {
      char c = (char) chooseChar();
      if (!Character.isLetterOrDigit(c)) {
        throw new IllegalArgumentException("Not alnum");
      }
      s += Character.toString(c);
    }
    return s + "\n";
  }

  static public String getRandomASCIIString(int n) {
    if (n < 0)
      return "default";
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n - 1; i++) {
      char c = (char) (33 + rg.nextInt(94));
      sb.append(c);
    }
    return sb.append('\n').toString();
  }

  static private int chooseRange() {
    return rg.nextInt(3);
  }

  static private int chooseChar() {
    switch (chooseRange()) {
      case 0:
        return l_lower + rg.nextInt(diff_lower) + 1;
      case 1:
        return l_upper + rg.nextInt(diff_upper) + 1;
      case 2:
        return l_num + rg.nextInt(diff_num) + 1;
      default:
        return 'a';
    }
  }
}
