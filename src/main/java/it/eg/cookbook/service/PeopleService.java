package it.eg.cookbook.service;

import it.eg.cookbook.Utils.PasswordUtil;
import it.eg.cookbook.model.User;
import it.eg.cookbook.Utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

@Service
public class PeopleService {

    @Autowired
    private Environment env;

    private Hashtable<String, String> getLdapContextEnv(String url) {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, env.getProperty("ldap.context"));
        environment.put(Context.PROVIDER_URL, url);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, env.getProperty("ldap.username"));
        environment.put(Context.SECURITY_CREDENTIALS, env.getProperty("ldap.password"));
        return environment;
    }

    public String getAllUsers() throws NamingException, JSONException {
        DirContext adminContext = new InitialDirContext(this.getLdapContextEnv(Utility.BASE_URL));

        JSONArray jArray = new JSONArray();
        if (jArray != null) {
            String filter = "objectclass=person";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = adminContext.search("", filter, searchControls);
            Utility.jsonUserBuilder(answer, jArray);
            answer.close();
        }
        adminContext.close();
        return jArray.toString();
    }

    public boolean save(User user) {

        try {
            DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));

            Attribute objClasses = new BasicAttribute("objectClass");
            objClasses.add("person");
            objClasses.add("inetOrgPerson");
            objClasses.add("organizationalPerson");
            objClasses.add("top");

            Attribute cn = new BasicAttribute("cn", user.getCn());
            Attribute givenname = new BasicAttribute("givenName", user.getGivenName());
            Attribute sn = new BasicAttribute("sn", user.getSn());
            Attribute mail = new BasicAttribute("mail", user.getEmail());
            Attribute username = new BasicAttribute("uid", user.getUid());

            String hasedPwd = PasswordUtil.generateSSHA(user.getPassword().getBytes(StandardCharsets.UTF_8));

            Attribute password = new BasicAttribute("userPassword", hasedPwd);
            System.out.println(hasedPwd);

            Attributes container = new BasicAttributes();
            container.put(objClasses);
            container.put(cn);
            container.put(givenname);
            container.put(sn);
            container.put(mail);
            container.put(password);
            container.put(username);

            String userDN = "cn=" + user.getCn() + "," + Utility.USER_CONTEXT;
            context.createSubcontext(userDN, container);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteUser(String cn) throws NamingException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL.substring(0, Utility.URL.length())));
        context.destroySubcontext("cn="+cn+","+ Utility.USER_CONTEXT);
    }

    public void putUser(User user) throws NamingException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));

        ModificationItem[] mods = new ModificationItem[5];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("givenName", user.getGivenName()));
        mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", user.getSn()));
        mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail", user.getEmail()));
        mods[3] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", user.getPassword()));
        mods[4] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("uid", user.getUid()));
        context.modifyAttributes("cn=" + user.getCn() + "," + Utility.USER_CONTEXT, mods);
    }

    public String findUser(String cn) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.BASE_URL));
        JSONArray jArray = new JSONArray();

        if (jArray != null) {
            String filter = "(&(objectclass=person)(cn="+ cn + "))";
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = context.search("", filter, ctrl);
            Utility.jsonUserBuilder(answer, jArray);
            answer.close();
        }
        return jArray.toString();
    }

}
