package chord.hash;

import java.io.Serializable;

import chord.util.Util;

/**
 * A class for restricting hashcode to be less than a
 * certain power of 2 for use within a chord network
 *
 * @param <T> the object being wrapped
 */
public abstract class ChordKey<T extends Serializable> implements Serializable {

  private final int modulus;

  private final T object;

  public ChordKey(int degree, T object) {
    this.modulus = Util.powerOf2(degree);
    this.object = object;
  }

  @Override
  public int hashCode() {
    int hashCodeAbs = Math.abs(object.hashCode());
    return hashCodeAbs >= modulus ? hashCodeAbs % modulus : hashCodeAbs;
  }

}
