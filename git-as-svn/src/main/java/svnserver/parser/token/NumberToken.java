package svnserver.parser.token;

import org.jetbrains.annotations.NotNull;
import svnserver.parser.SvnServerToken;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Число.
 *
 * @author Artem V. Navrotskiy <bozaro@users.noreply.github.com>
 */
public final class NumberToken implements SvnServerToken {
  private final int number;

  public NumberToken(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

  @Override
  public void write(@NotNull OutputStream stream) throws IOException {
    write(stream, number);
  }

  public static void write(@NotNull OutputStream stream, long number) throws IOException {
    stream.write(Long.toString(number).getBytes(StandardCharsets.ISO_8859_1));
    stream.write(' ');
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final NumberToken other = (NumberToken) o;
    return number == other.number;
  }

  @Override
  public int hashCode() {
    return number;
  }

  @Override
  public String toString() {
    return "Number{" + number + '}';
  }
}
