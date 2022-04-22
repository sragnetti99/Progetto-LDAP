package it.eg.cookbook;

import it.eg.cookbook.model.User;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import java.util.Hashtable;

public class TestUtils {

    public static Hashtable<String,String> getEnvironment() {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldap://localhost:389");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=imolinfo,dc=it");
        environment.put(Context.SECURITY_CREDENTIALS, "password");
        return environment;
    }

    public static User createUser(String cn) {
        User user = new User();
        user.setCn(cn);
        user.setMail(cn+"@imolainformatica.it");
        user.setGivenName(cn);
        user.setPassword("password");
        user.setSn(cn);
        user.setUid(cn);
        return user;
    }

    public static void createOuGroup(String groupName, DirContext context) throws NamingException {
        Attributes entry = new BasicAttributes(true);
        entry.put(new BasicAttribute("ou", groupName));
        entry.put(new BasicAttribute("objectclass", "top"));
        entry.put(new BasicAttribute("objectclass", "organizationalUnit"));
        if (groupName != null) {
            context.createSubcontext("ou="+ groupName +",dc=imolinfo,dc=it", entry);
        }
    }

    public static void createCnGroup(String groupDN, String groupName, DirContext context) throws NamingException {
        Attributes entry = new BasicAttributes(true);
        entry.put(new BasicAttribute("cn", groupName));
        entry.put(new BasicAttribute("objectclass", "top"));
        entry.put(new BasicAttribute("objectclass", "groupOfUniqueNames"));
        entry.put(new BasicAttribute("uniqueMember", ""));
        if (groupDN != null & groupName != null) {
            context.createSubcontext(groupDN, entry);
        }
    }
}
