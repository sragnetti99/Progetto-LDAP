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

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

class PeopleIntegrationTest {

    private static PeopleService peopleService;

    @BeforeAll
    static void init() throws NamingException, JSONException, NoSuchAlgorithmException {
        Hashtable<String, String> environment = TestUtils.getEnvironment();
        DirContext context = new InitialDirContext(environment);
        TestUtils.createOuGroup("people", context);
        TestUtils.createOuGroup("groups", context);
        TestUtils.createCnGroup("cn=test,ou=groups,dc=imolinfo,dc=it", "test", context);
        peopleService = new PeopleService(environment);
        peopleService.save(TestUtils.createUser("utente1"));
        peopleService.save(TestUtils.createUser("utente2"));
    }

    @Test
    void getUsers() throws JSONException, NamingException, JsonProcessingException {
        JSONArray array = new JSONArray(peopleService.getAllUsers());
        Assertions.assertEquals(2, array.length());
    }

    @Test
    void postUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        peopleService.save(TestUtils.createUser("utente3"));
        String result = peopleService.getAllUsers();
        Assertions.assertTrue(result.contains("utente3"));
        peopleService.deleteUser("utente3");
    }

    @Test
    void deleteUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        User user = TestUtils.createUser("daCancellare");
        peopleService.save(user);
        peopleService.deleteUser(user.getCn());

        try {
            String result1 = peopleService.findUser(user.getCn());
        } catch(Exception e) {
            Assertions.assertEquals("javax.naming.CommunicationException", e.getClass().getName());
        }

        String result2 = peopleService.getAllUsers();
        Assertions.assertFalse(result2.contains(user.getCn()));
    }

    @Test
    void putUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        User user = TestUtils.createUser("daModificare");
        peopleService.save(user);

        User modifiedUser = TestUtils.createUser("daModificare");
        modifiedUser.setSn("modificato");
        modifiedUser.setMail("modificato@imolinfo.it");
        peopleService.putUser(modifiedUser);

        String result1 = peopleService.findUser(user.getCn());
        JSONArray array = new JSONArray(result1);

        for (int i = 0; i < array.length(); ++i) {
            JSONObject obj = array.getJSONObject(i);
            Assertions.assertEquals(modifiedUser.getSn(), obj.get("sn"));
            Assertions.assertEquals(modifiedUser.getMail(), obj.get("mail"));
        }

        String result2 = peopleService.getAllUsers();
        Assertions.assertTrue(result2.contains(modifiedUser.getMail()));
        Assertions.assertTrue(result2.contains(modifiedUser.getSn()));
        peopleService.deleteUser("daModificare");
    }

}

