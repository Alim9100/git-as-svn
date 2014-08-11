package svnserver.parser.token;

import org.jetbrains.annotations.NotNull;
import svnserver.parser.SvnServerToken;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Начало списка.
 *
 * @author Artem V. Navrotskiy <bozaro@users.noreply.github.com>
 */
public final class ListBeginToken implements SvnServerToken {
  @NotNull
  public static final ListBeginToken instance = new ListBeginToken();
  @NotNull
  private static final byte[] TOKEN = {'(', ' '};

  private ListBeginToken() {
  }

  @Override
  public void write(OutputStream stream) throws IOException {
    stream.write(TOKEN);
  }

  @Override
  public String toString() {
    return "ListBegin";
  }
}
