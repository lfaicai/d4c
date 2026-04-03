package org.faicai.d4c.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.faicai.d4c.pojo.entity.D4cUser;
import org.faicai.d4c.pojo.entity.Role;
import org.faicai.d4c.service.D4cUserService;
import org.faicai.d4c.service.UcRoleService;
import org.faicai.d4c.utils.TokenJwtUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final D4cUserService d4cUserService;
    private final UcRoleService ucRoleService;

    public JwtAuthenticationFilter(@Lazy D4cUserService d4cUserService, @Lazy UcRoleService ucRoleService) {
        this.d4cUserService = d4cUserService;
        this.ucRoleService = ucRoleService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        if (StringUtils.hasText(token) && TokenJwtUtil.validateToken(token)) {
            String account = TokenJwtUtil.getAccount(token);
            D4cUser userDetails = d4cUserService.getByAccount(account);
            if (userDetails != null) {
                // 加载用户角色并转换为GrantedAuthority
                List<GrantedAuthority> authorities = loadUserAuthorities(userDetails.getId());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 加载用户角色并转换为GrantedAuthority
     * 使用roleCode，如果没有roleCode则使用roleName
     */
    private List<GrantedAuthority> loadUserAuthorities(Long userId) {
        List<Role> roles = ucRoleService.getRolesByUserId(userId);
        return roles.stream()
                .map(role -> {
                    // 优先使用roleCode，如果没有则使用roleName
                    String roleCode = role.getRoleCode();
                    if (roleCode == null || roleCode.trim().isEmpty()) {
                        roleCode = role.getRoleName();
                    }
                    return new SimpleGrantedAuthority(roleCode);
                })
                .collect(Collectors.toList());
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(TokenJwtUtil.AUTH_HEADER_KEY);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TokenJwtUtil.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
