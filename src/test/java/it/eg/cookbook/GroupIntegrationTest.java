package it.eg.cookbook;

import it.eg.cookbook.Utils.Utility;
import it.eg.cookbook.model.User;
import it.eg.cookbook.service.GroupService;
import it.eg.cookbook.service.PeopleService;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

class GroupIntegrationTest {

    private static GroupService service;
    private static PeopleService peopleService;
    private static Hashtable<String, String> environment;
    private static DirContext context;

    @BeforeAll
    static void init() throws NamingException {
        environment = TestUtils.getEnvironment();
        context = new InitialDirContext(environment);
        TestUtils.createOuGroup("people", context);
        TestUtils.createOuGroup("groups", context);
        TestUtils.createCnGroup("cn=test,ou=groups,dc=imolinfo,dc=it", "test", context);
        service = new GroupService(environment);
        peopleService = new PeopleService(environment);
    }

    @Test
    void getAllGroups() throws JSONException, NamingException {
        Assertions.assertEquals(1, service.getAllGroups().length());
        Assertions.assertEquals("test", service.getAllGroups().getJSONObject(0).get("name"));
    }

    @Test
    void insertUserInGroup() throws NamingException, JSONException, NoSuchAlgorithmException {
        User u1 = TestUtils.createUser("utente1");
        User u2 = TestUtils.createUser("utente2");
        peopleService.save(u1);
        peopleService.save(u2);

        String users = "{ \"uniquemember\": [\"cn=" + u1.getCn() +",ou=people,dc=imolinfo,dc=it\", \"cn=" + u2.getCn() + ",ou=people,dc=imolinfo,dc=it\"]}";
        service.addUsersToGroup(users, "test");
        String resultU1 = service.findUserInGroup("cn=utente1," + Utility.USER_CONTEXT, "test");
        String resultU2 = service.findUserInGroup("cn=utente2," + Utility.USER_CONTEXT, "test");
        Assertions.assertTrue(resultU1.length() > 2);
        Assertions.assertTrue(resultU2.length() > 2);
    }

    @Test
    void deleteUserFromGroup() throws JSONException, NamingException {
        service.deleteUserFromGroup("{ \"uniquemember\": \"cn=utente1 ," + Utility.USER_CONTEXT + "\"}" , "test");
        service.deleteUserFromGroup("{ \"uniquemember\": \"cn=utente2 ," + Utility.USER_CONTEXT + "\"}"  , "test");
        String resultU1 = service.findUserInGroup("cn=utente1," + Utility.USER_CONTEXT, "test");
        String resultU2 = service.findUserInGroup("cn=utente2," + Utility.USER_CONTEXT, "test");
        Assertions.assertFalse(resultU1.length() > 2);
        Assertions.assertFalse(resultU2.length() > 2);

        peopleService.deleteUser("utente1");
        peopleService.deleteUser("utente2");
        TestUtils.deleteAll(context);
    }
}
