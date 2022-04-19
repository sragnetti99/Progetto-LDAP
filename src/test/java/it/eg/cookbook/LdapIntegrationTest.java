package it.eg.cookbook;

import it.eg.cookbook.model.User;
import it.eg.cookbook.service.PeopleService;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.naming.Context;
import javax.naming.NamingException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LdapIntegrationTest {

//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    @Autowired
    Environment env;

//    @InjectMocks
//    private PeopleService service;

    @Mock
    private PeopleService service;

    @BeforeAll
    public void init() {

    }

    private User createUser(String cn) {
        User user = new User();
        user.setCn(cn);
        user.setEmail("utente@imolinfo.it");
        user.setGivenName("utente");
        user.setHomeDirectory("/home/users/utente");
        user.setPassword("password");
        user.setLoginShell("/bin/bash");
        user.setSambaAcctFlags("[U]");
        user.setSambaSID("S-1-5-21-1288326302-1102467403-3443272390-3000");
        user.setSn("utente");
        user.setUid("utente");
        user.setUidNumber("1000");
        return user;
    }





    @Test
    void postUser() throws NamingException, JSONException, NoSuchAlgorithmException {


            Hashtable<String, String> environment = new Hashtable<>();
            environment.put(Context.INITIAL_CONTEXT_FACTORY, env.getProperty("ldap.context"));
            environment.put(Context.PROVIDER_URL, "ldap://192.168.2.222:389");
            environment.put(Context.SECURITY_AUTHENTICATION, "simple");
            environment.put(Context.SECURITY_PRINCIPAL, env.getProperty("ldap.username"));
            environment.put(Context.SECURITY_CREDENTIALS, env.getProperty("ldap.password"));
        User user = this.createUser("provajava1");
        PeopleService service = new PeopleService(environment);
//        PeopleService service = new PeopleService("com.sun.jndi.ldap.LdapCtxFactory", "ldap://localhost:"+port, "cn=admin,dc=imolinfo,dc=it","password");
        service.save(user);
        String result = service.getAllUsers();
        Assertions.assertTrue(result.contains("prova"));
    }

}

