/**
 * This file is part of git-as-svn. It is subject to the license terms
 * in the LICENSE file found in the top-level directory of this distribution
 * and at http://www.gnu.org/licenses/gpl-2.0.html. No part of git-as-svn,
 * including this file, may be copied, modified, propagated, or distributed
 * except according to the terms contained in the LICENSE file.
 */
package svnserver.repository.git.push;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import svnserver.auth.User;
import svnserver.config.ConfigHelper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Git push mode.
 *
 * @author Artem V. Navrotskiy <bozaro@users.noreply.github.com>
 */
public class GitPushEmbedded implements GitPusher {
  @NotNull
  private static final Logger log = LoggerFactory.getLogger(GitPushEmbedded.class);
  @NotNull
  private final String preReceive;
  @NotNull
  private final String postReceive;

  public GitPushEmbedded(@NotNull String preReceive, @NotNull String postReceive) {
    this.preReceive = preReceive;
    this.postReceive = postReceive;
  }

  @Override
  public boolean push(@NotNull Repository repository, @NotNull ObjectId ReceiveId, @NotNull String branch, @NotNull User userInfo) throws SVNException, IOException {
    final RefUpdate refUpdate = repository.updateRef(branch);
    refUpdate.getOldObjectId();
    refUpdate.setNewObjectId(ReceiveId);
    runHook(repository, refUpdate, preReceive, userInfo);
    final RefUpdate.Result result = refUpdate.update();
    switch (result) {
      case REJECTED:
        return false;
      case NEW:
      case FAST_FORWARD:
        runHook(repository, refUpdate, postReceive, userInfo);
        return true;
      default:
        log.error("Unexpected push error: {}", result);
        throw new SVNException(SVNErrorMessage.create(SVNErrorCode.IO_WRITE_ERROR, result.name()));
    }
  }

  private void runHook(@NotNull Repository repository, @NotNull RefUpdate refUpdate, @NotNull String hook, @NotNull User userInfo) throws IOException, SVNException {
    if (hook.isEmpty()) {
      return;
    }
    final File script = ConfigHelper.joinPath(ConfigHelper.joinPath(repository.getDirectory(), "hooks"), hook);
    if (script.isFile()) {
      try {
        final ProcessBuilder processBuilder = new ProcessBuilder(script.getAbsolutePath())
            .directory(repository.getDirectory())
            .redirectErrorStream(true);
        processBuilder.environment().put("LANG", "en_US.utf8");
        updateEnvironment(processBuilder.environment(), userInfo);
        final Process process = processBuilder.start();
        try (Writer stdin = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
          stdin.write(getObjectId(refUpdate.getOldObjectId()));
          stdin.write(' ');
          stdin.write(getObjectId(refUpdate.getNewObjectId()));
          stdin.write(' ');
          stdin.write(refUpdate.getName());
          stdin.write('\n');
        }
        final String hookMessage = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
          throw new SVNException(SVNErrorMessage.create(SVNErrorCode.REPOS_HOOK_FAILURE, "Commit blocked by hook with output:\n" + hookMessage));
        }
      } catch (InterruptedException e) {
        log.error("Hook interrupted: " + script.getAbsolutePath(), e);
        throw new SVNException(SVNErrorMessage.create(SVNErrorCode.IO_WRITE_ERROR, e));
      }
    }
  }

  @NotNull
  private static String getObjectId(@Nullable ObjectId objectId) {
    return objectId == null ? ObjectId.zeroId().getName() : objectId.getName();
  }
}