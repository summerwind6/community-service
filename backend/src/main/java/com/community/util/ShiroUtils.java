package com.community.util;

import com.community.entity.po.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class ShiroUtils {

    private ShiroUtils() {
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static String getCurrentUsername() {
        Subject subject = getSubject();
        if (subject.isAuthenticated() && subject.getPrincipal() != null) {
            return (String) subject.getPrincipal();
        }
        return null;
    }

    public static boolean isAuthenticated() {
        return getSubject().isAuthenticated();
    }

    public static boolean hasRole(String role) {
        return getSubject().hasRole(role);
    }

    public static boolean hasAllRoles(String... roles) {
        return getSubject().hasAllRoles(java.util.List.of(roles));
    }

    public static boolean isPermitted(String permission) {
        return getSubject().isPermitted(permission);
    }

    public static boolean isPermittedAll(String... permissions) {
        return getSubject().isPermittedAll(permissions);
    }

    public static void logout() {
        getSubject().logout();
    }
}
