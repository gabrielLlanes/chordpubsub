package chord.util;

public class ModuloInterval implements java.io.Serializable {

  public int a;
  public int b;

  public ModuloInterval(int a, int b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public String toString() {
    return String.format("[%d, %d]", a, b);
  }

}
