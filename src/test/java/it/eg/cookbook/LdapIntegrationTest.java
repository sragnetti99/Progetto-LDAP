package it.eg.cookbook;

import it.eg.cookbook.model.User;
import it.eg.cookbook.service.PeopleService;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.NamingException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

class LdapIntegrationTest {

    private User createUser(String cn) {
        User user = new User();
        user.setCn(cn);
        user.setEmail("utente@imolinfo.it");
        user.setGivenName("utente");
        user.setPassword("password");
        user.setSn("utente");
        user.setUid("utente");
        return user;
    }

    @Test
    void postUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://localhost:389");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=imolinfo,dc=it");
        environment.put(Context.SECURITY_CREDENTIALS, "password");
        User user = this.createUser("prova");
        PeopleService service = new PeopleService(environment);
        service.save(user);
        String result = service.getAllUsers();
        Assertions.assertTrue(result.contains("prova"));
    }

}

