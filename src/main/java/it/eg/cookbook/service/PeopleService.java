package it.eg.cookbook.service;

import it.eg.cookbook.Utils.PasswordUtil;
import it.eg.cookbook.model.User;
import it.eg.cookbook.Utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
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
        String filter = "objectclass=person";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = adminContext.search("", filter, searchControls);
        Utility.jsonUserBuilder(answer, jArray);
        answer.close();

        adminContext.close();
        return jArray.toString();
    }

    public boolean save(User user) {

        try {
            getMaxUidNumber();
            DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));
            Attribute objClasses = new BasicAttribute("objectClass");
            objClasses.add("person");
            objClasses.add("inetOrgPerson");
            objClasses.add("organizationalPerson");
            objClasses.add("sambaSamAccount");
            objClasses.add("posixAccount");
            objClasses.add("top");

            Attributes container = new BasicAttributes();
            container.put(objClasses);
            container.put(new BasicAttribute("cn", user.getCn()));
            container.put( new BasicAttribute("givenName", user.getGivenName()));
            container.put(new BasicAttribute("sn", user.getSn()));
            container.put(new BasicAttribute("mail", user.getEmail()));
            String hashedPwd = PasswordUtil.generateSSHA(user.getPassword().getBytes(StandardCharsets.UTF_8));
            container.put(new BasicAttribute("userPassword", hashedPwd));
            container.put(new BasicAttribute("sambaLMPassword", hashedPwd));
            container.put(new BasicAttribute("sambaNTPassword", hashedPwd));
            container.put(new BasicAttribute("sambaSID", "S-1-5-21-1288326302-1102467403-3443272390-3000"));
            container.put(new BasicAttribute("homeDirectory", "/home/users/"+user.getCn()));
            container.put(new BasicAttribute("homeDirectory", "/bin/bash"));
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

    public void putUser(User user) throws NamingException, NoSuchAlgorithmException, JSONException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));

        ModificationItem[] mods = new ModificationItem[12];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("givenName", user.getGivenName()));
        mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", user.getSn()));
        mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail", user.getEmail()));
        mods[3] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", user.getPassword()));
        mods[4] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("uid", user.getUid()));
        if(user.getUidNumber() == null || user.getUidNumber().isEmpty() ){
            mods[5] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("uidNumber", getMaxUidNumber()));
        }else{
            mods[5] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("uidNumber", user.getUidNumber()));
        }

        String hashedPwd = PasswordUtil.generateSSHA(user.getPassword().getBytes(StandardCharsets.UTF_8));
        mods[6] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", hashedPwd));
        mods[7] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sambaLMPassword", hashedPwd));
        mods[8] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sambaNTPassword", hashedPwd));
        mods[9] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sambaSID", "S-1-5-21-1288326302-1102467403-3443272390-3000"));
        mods[10] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("homeDirectory", "/home/users/"+user.getCn()));
        mods[11] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("shellLogin", "/bin/bash"));

        context.modifyAttributes("cn=" + user.getCn() + "," + Utility.USER_CONTEXT, mods);
    }

    public String findUser(String cn) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.BASE_URL));
        JSONArray jArray = new JSONArray();

        String filter = "(&(objectclass=person)(cn="+ cn + "))";
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = context.search("", filter, ctrl);
        Utility.jsonUserBuilder(answer, jArray);
        answer.close();

        return jArray.toString();
    }

    private Integer getMaxUidNumber() throws JSONException, NamingException {
        Integer maxUid = new Integer(0);
        DirContext adminContext = new InitialDirContext(this.getLdapContextEnv(Utility.BASE_URL));

        JSONArray jArray = new JSONArray();
        String filter = "uidNumber=*";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = adminContext.search("",filter,searchControls);
        Utility.jsonUserBuilder(answer, jArray);

        for(int i =0; i<jArray.length();i++){
            JSONObject jsonObject = jArray.getJSONObject(i);
            if (jsonObject.get("uidNumber") != null){
                Integer integerJson = Integer.parseInt(jsonObject.get("uidNumber").toString());
                if( integerJson > maxUid){
                    maxUid = integerJson;
                }

            }
        }
        answer.close();

        adminContext.close();

        return maxUid;
    }

}
