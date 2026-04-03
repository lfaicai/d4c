package org.faicai.d4c.utils;

import org.faicai.d4c.pojo.entity.D4cUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    /**
     * 获取当前认证的Authentication对象
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前登录的用户id
     */
    public static Long getCurrentUserId() {
        D4cUser d4cUser = getCurrentUser();
        return d4cUser.getId();
    }

    /**
     * 获取当前登录的自定义用户对象
     */
    public static D4cUser getCurrentUser() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
            !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof D4cUser) {
                return (D4cUser) principal;
            }
            // 根据你的配置，这里也可能是UserDetails，需要其他处理
        }
        throw new RuntimeException("无法获取当前用户或用户未认证"); // 或者返回null
    }

    /**
     * 检查当前用户是否拥有指定角色
     */
    public static boolean hasRole(String roleName) {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                   .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + roleName)); // 注意权限名称通常有ROLE_前缀
        }
        return false;
    }
}