package it.eg.cookbook;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.eg.cookbook.model.User;
import it.eg.cookbook.service.PeopleService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.NamingException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

class PeopleIntegrationTest {

    private static PeopleService service;

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

    @BeforeAll
    static void init() throws NamingException {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://localhost:389");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=imolinfo,dc=it");
        environment.put(Context.SECURITY_CREDENTIALS, "password");
        service = new PeopleService(environment);
    }

    @Test
    void getUsers() throws JSONException, NamingException, JsonProcessingException {
        JSONArray array = new JSONArray(service.getAllUsers());
        Assertions.assertEquals(6, array.length());
    }

    @Test
    void postUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        service.save(this.createUser("utenteCreato"));
        String result = service.getAllUsers();
        Assertions.assertTrue(result.contains("utenteCreato"));
        service.deleteUser("utenteCreato");
    }

    @Test
    void deleteUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        User user = this.createUser("daCancellare");
        service.save(user);
        service.deleteUser(user.getCn());

        try {
            String result1 = service.findUser(user.getCn());
        } catch(Exception e) {
            Assertions.assertEquals("javax.naming.CommunicationException", e.getClass().getName());
        }

        String result2 = service.getAllUsers();
        Assertions.assertFalse(result2.contains(user.getCn()));
    }

    @Test
    void putUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        User user = this.createUser("daModificare");
        service.save(user);

        User modifiedUser = this.createUser("daModificare");
        modifiedUser.setSn("modificato");
        modifiedUser.setEmail("modificato@imolinfo.it");
        service.putUser(modifiedUser);

        String result1 = service.findUser(user.getCn());
        JSONArray array = new JSONArray(result1);

        for (int i = 0; i < array.length(); ++i) {
            JSONObject obj = array.getJSONObject(i);
            Assertions.assertEquals(modifiedUser.getSn(), obj.get("sn"));
            Assertions.assertEquals(modifiedUser.getEmail(), obj.get("mail"));
        }

        String result2 = service.getAllUsers();
        Assertions.assertTrue(result2.contains(user.getEmail()));
        service.deleteUser("daModificare");
    }

}

