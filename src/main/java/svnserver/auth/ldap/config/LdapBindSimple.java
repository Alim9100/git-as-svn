/*
 * This file is part of git-as-svn. It is subject to the license terms
 * in the LICENSE file found in the top-level directory of this distribution
 * and at http://www.gnu.org/licenses/gpl-2.0.html. No part of git-as-svn,
 * including this file, may be copied, modified, propagated, or distributed
 * except according to the terms contained in the LICENSE file.
 */
package svnserver.auth.ldap.config;

import com.unboundid.ldap.sdk.BindRequest;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import svnserver.config.serializer.ConfigType;

/**
 * @author Artem V. Navrotskiy <bozaro@users.noreply.github.com>
 * @author Marat Radchenko <marat@slonopotamus.org>
 * @see SimpleBindRequest
 */
@ConfigType("Simple")
public final class LdapBindSimple implements LdapBind {
  @Nullable
  private String bindDn;
  @Nullable
  private String password;

  public LdapBindSimple() {
  }

  public LdapBindSimple(@Nullable String bindDn, @Nullable String password) {
    this.bindDn = bindDn;
    this.password = password;
  }

  @Override
  @NotNull
  public BindRequest createBindRequest() {
    return new SimpleBindRequest(bindDn, password);
  }
}
