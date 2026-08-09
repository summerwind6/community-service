package com.community.config;

import com.community.shiro.CustomRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ShiroConfig {

    @Bean
    public CustomRealm customRealm() {
        return new CustomRealm();
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        shiroFilter.setLoginUrl("/auth/unauthorized");
        shiroFilter.setUnauthorizedUrl("/auth/unauthorized");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // Shiro matches paths below server.servlet.context-path (/api), so do not include /api here.
        filterChainDefinitionMap.put("/auth/login", "anon");
        filterChainDefinitionMap.put("/auth/unauthorized", "anon");
        filterChainDefinitionMap.put("/apply/public", "anon");
        filterChainDefinitionMap.put("/apply/progress", "anon");
        filterChainDefinitionMap.put("/item/enabled", "anon");
        filterChainDefinitionMap.put("/guide/public", "anon");
        filterChainDefinitionMap.put("/upload/public", "anon");
        filterChainDefinitionMap.put("/evaluation/public", "anon");
        filterChainDefinitionMap.put("/actuator/health", "anon");
        filterChainDefinitionMap.put("/swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/v3/api-docs/**", "anon");
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilter;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
