package com.community.shiro;

import com.community.entity.po.SysUser;
import com.community.service.SysUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private SysUserService sysUserService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        SysUser user = sysUserService.getUserByUsername(username);
        if (user != null && user.getRoles() != null) {
            authorizationInfo.setRoles(user.getRoles());
            if (user.getPermissions() != null) {
                authorizationInfo.setStringPermissions(user.getPermissions());
            }
        }

        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();

        System.out.println("[DEBUG] CustomRealm.doGetAuthenticationInfo called, username=" + username);
        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            System.out.println("[DEBUG] CustomRealm: user NOT FOUND in DB");
            throw new UnknownAccountException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new LockedAccountException("账号已被禁用");
        }

        System.out.println("[DEBUG] CustomRealm: returning stored password=" + user.getPassword());
        return new SimpleAuthenticationInfo(
                username,
                user.getPassword(),
                getName()
        );
    }
}
