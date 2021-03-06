/*
 * This file is part of git-as-svn. It is subject to the license terms
 * in the LICENSE file found in the top-level directory of this distribution
 * and at http://www.gnu.org/licenses/gpl-2.0.html. No part of git-as-svn,
 * including this file, may be copied, modified, propagated, or distributed
 * except according to the terms contained in the LICENSE file.
 */
package svnserver;

import org.jetbrains.annotations.NotNull;
import svnserver.auth.User;
import svnserver.repository.VcsAccess;

/**
 * Non-anonymous user.
 *
 * @author Artem V. Navrotskiy
 * @author Marat Radchenko <marat@slonopotamus.org>
 */
public final class VcsAccessNoAnonymous implements VcsAccess {
  @NotNull
  static final VcsAccess instance = new VcsAccessNoAnonymous();

  @Override
  public boolean canRead(@NotNull User user, @NotNull String branch, @NotNull String path) {
    return !user.isAnonymous();
  }

  @Override
  public boolean canWrite(@NotNull User user, @NotNull String branch, @NotNull String path) {
    return canRead(user, branch, path);
  }
}
