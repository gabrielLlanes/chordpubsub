package chord.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ModuloTest {
  @Test
  public void moduloTest() {
    Modulo modulo = new Modulo(512);

    int q = -123123123 / 512;

    assertEquals(-123123123 + (512 * -q) + 512, modulo.mod(-123123123));
  }
}
