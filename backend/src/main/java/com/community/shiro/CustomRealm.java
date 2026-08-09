package com.community.shiro;

import com.community.entity.po.SysUser;
import com.community.entity.vo.UserVO;
import com.community.service.SysUserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
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

        UserVO user = sysUserService.getUserInfoByUsername(username);
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

        SysUser user = sysUserService.getUserByUsername(username);
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new LockedAccountException("账号已被禁用");
        }

        return new SimpleAuthenticationInfo(
                username,
                user.getPassword(),
                getName()
        );
    }
}
