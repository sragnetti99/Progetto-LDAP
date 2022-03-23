package it.eg.cookbook.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class LdapProperties {

    @Value("${ldap.context}")
    private String initialContextFactory;

    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.username}")
    private String username;

    @Value("${ldap.password}")
    private String password;
}
