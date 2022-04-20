package it.eg.cookbook;

import it.eg.cookbook.Utils.Utility;
import it.eg.cookbook.model.User;
import it.eg.cookbook.service.GroupService;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Hashtable;

class GroupIntegrationTest {

    private static GroupService service;

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
        service = new GroupService(environment);
    }

    @Test
    void getAllGroups() throws JSONException, NamingException {
        Assertions.assertEquals(4, service.getAllGroups().length());
        Assertions.assertEquals("Antreem", service.getAllGroups().getJSONObject(0).get("name"));
        Assertions.assertEquals("Betoola", service.getAllGroups().getJSONObject(1).get("name"));
        Assertions.assertEquals("external", service.getAllGroups().getJSONObject(2).get("name"));
        Assertions.assertEquals("internal", service.getAllGroups().getJSONObject(3).get("name"));
    }

    @Test
    void getGroupByName() throws JSONException, NamingException {
        String users = service.getUsersInGroup("Antreem");
        JSONArray array = new JSONArray(users);
        Assertions.assertEquals(1, array.length());
        Assertions.assertTrue(users.contains("internou"));
    }

    @Test
    void insertUserInGroup() throws NamingException, JSONException {
        User u1 = this.createUser("utente1");
        User u2 = this.createUser("utente2");

        String users = "{ \"uniquemember\": [\"cn=" + u1.getCn() +",ou=people,dc=imolinfo,dc=it\", \"cn=" + u2.getCn() + ",ou=people,dc=imolinfo,dc=it\"]}";
        service.addUsersToGroup(users, "Antreem");
        String resultU1 = service.findUserInGroup("cn=utente1," + Utility.USER_CONTEXT, "Antreem");
        String resultU2 = service.findUserInGroup("cn=utente2," + Utility.USER_CONTEXT, "Antreem");
        Assertions.assertTrue(resultU1.length() > 2);
        Assertions.assertTrue(resultU2.length() > 2);
    }

    @Test
    void deleteUserFromGroup() throws JSONException, NamingException {
        service.deleteUserFromGroup("{ \"uniquemember\": \"cn=utente1 ," + Utility.USER_CONTEXT + "\"}" , "Antreem");
        service.deleteUserFromGroup("{ \"uniquemember\": \"cn=utente2 ," + Utility.USER_CONTEXT + "\"}"  , "Antreem");
        String resultU1 = service.findUserInGroup("cn=utente1," + Utility.USER_CONTEXT, "Antreem");
        String resultU2 = service.findUserInGroup("cn=utente2," + Utility.USER_CONTEXT, "Antreem");
        Assertions.assertFalse(resultU1.length() > 2);
        Assertions.assertFalse(resultU2.length() > 2);
    }

}
