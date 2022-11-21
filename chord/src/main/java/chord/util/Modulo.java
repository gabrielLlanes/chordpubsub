package chord.util;

/**
 * Utility class for operations modulo a certain modulus.
 */
public class Modulo {

  private int modulus;

  public Modulo(int modulus) {
    this.modulus = modulus;
  }

  // a <= n < b mod modulus
  public boolean inLeftHalfClosed(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n >= a && n < b;
    } else {
      return (n >= a && n < modulus) || (n >= 0 && n < b);
    }
  }

  public boolean inLeftHalfClosed(int n, ModuloInterval interval) {
    return inLeftHalfClosed(n, interval.a, interval.b);
  }

  // a < n <= b mod modulus
  public boolean inRightHalfClosed(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n > a && n <= b;
    } else {
      return (n > a && n < modulus) || (n >= 0 && n <= b);
    }
  }

  public boolean inRightHalfClosed(int n, ModuloInterval interval) {
    return inRightHalfClosed(n, interval.a, interval.b);
  }

  // a < n < b mod modulus
  public boolean inOpen(int n, int a, int b) {
    if (a == b)
      return false;
    else if (a < b) {
      return n > a && n < b;
    } else {
      return (n > a && n < modulus) || (n >= 0 && n < b);
    }
  }

  public boolean inOpen(int n, ModuloInterval interval) {
    return inOpen(n, interval.a, interval.b);
  }

  // a <= n <= b mod modulus
  public boolean inClosed(int n, int a, int b) {
    if (a == b)
      return n == a;
    else if (a < b) {
      return n >= a && n <= b;
    } else {
      return (n >= a && n < modulus) || (n >= 0 && n <= b);
    }
  }

  public boolean inClosed(int n, ModuloInterval interval) {
    return inClosed(n, interval.a, interval.b);
  }

  private ModuloInterval regularIntersection(int a1, int b1, int a2, int b2) {
    if (a1 > b1 || a2 > b2) {
      throw new IllegalArgumentException();
    }
    if (a1 > b2 || a2 > b1) {
      return null;
    }
    if (b1 >= a2) {
      return new ModuloInterval(a2, b1);
    }
    return new ModuloInterval(a1, b2);
  }

  public boolean intersects(int a1, int b1, int a2, int b2) {
    if (a1 > b1 && a2 > b2) {
      return true;
    } else if (a1 > b1 && a2 <= b2) {
      return regularIntersection(a1, modulus - 1, a2, b2) != null || regularIntersection(0, b1, a2, b2) != null;
    } else if (a1 <= b1 && a2 > b2) {
      return regularIntersection(a1, b1, a2, modulus - 1) != null || regularIntersection(a1, b1, 0, b2) != null;
    } else {
      return regularIntersection(a1, b1, a2, b2) != null;
    }
  }

  public boolean intersects(ModuloInterval i1, ModuloInterval i2) {
    return intersects(i1.a, i1.b, i2.a, i2.b);
  }

  public ModuloInterval interval(int a, int b) {
    return new ModuloInterval(mod(a), mod(b));
  }

  // n mod modulus
  public int mod(int n) {
    if (n >= 0) {
      return n % modulus;
    } else {
      return mod((n % modulus) + modulus);
    }
  }

  // hashcode mod modulus
  public int hashCode(Object o) {
    int hashCodeAbs = Math.abs(o.hashCode());
    return hashCodeAbs >= modulus ? hashCodeAbs % modulus : hashCodeAbs;
  }

  // hashcode mod modulus
  public static int hashCode(Object o, int modulus) {
    int hashCodeAbs = Math.abs(o.hashCode());
    return hashCodeAbs >= modulus ? hashCodeAbs % modulus : hashCodeAbs;
  }

}
