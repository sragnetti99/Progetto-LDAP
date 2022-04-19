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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.naming.NamingException;
import java.security.NoSuchAlgorithmException;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LdapIntegrationTest {

//    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//    @Mock
//    Environment env;
//
//    @InjectMocks
//    private PeopleService service;

    @Mock
    private PeopleService service;

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
//        MockitoAnnotations.initMocks(LdapIntegrationTest.class);
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

    @Container
    public GenericContainer ldap = new GenericContainer(DockerImageName.parse("osixia/openldap:1.5.0"))
            .withEnv("LDAP_ADMIN_PASSWORD", "password")
            .withEnv("LDAP_BASE_DN", "dc=imolinfo,dc=it")
            .withEnv("LDAP_CONFIG_PASSWORD", "password")
            .withEnv("LDAP_DOMAIN", "imolinfo.it")
            .withEnv("LDAP_ORGANISATION", "Imolainformatica")
            .withExposedPorts(389);

//    @Test
//    public void testStartContainer() throws NamingException {
//        String address = ldap.getHost();
//        Integer port = ldap.getFirstMappedPort();
//        this.service = new PeopleService();
//    }


    @Test
    void postUser() throws NamingException, JSONException, NoSuchAlgorithmException {
        User user = this.createUser("prova");
        Integer port = ldap.getFirstMappedPort();
        PeopleService service = new PeopleService();
//        PeopleService service = new PeopleService("com.sun.jndi.ldap.LdapCtxFactory", "ldap://localhost:"+port, "cn=admin,dc=imolinfo,dc=it","password");
        service.save(user);
        String result = service.getAllUsers();
        Assertions.assertTrue(result.contains("prova"));
    }

}

