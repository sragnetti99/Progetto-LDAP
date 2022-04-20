package it.eg.cookbook.service;

import it.eg.cookbook.Utils.PasswordUtil;
import it.eg.cookbook.Utils.Utility;
import it.eg.cookbook.config.EnvConfiguration;
import it.eg.cookbook.model.User;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

@Service
@Slf4j
public class PeopleService {

    private final DirContext ldapContext;

    public PeopleService() throws NamingException {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, EnvConfiguration.getProperty("ldap.context"));
        environment.put(Context.PROVIDER_URL, Utility.URL);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, EnvConfiguration.getProperty("ldap.username"));
        environment.put(Context.SECURITY_CREDENTIALS, EnvConfiguration.getProperty("ldap.password"));
        this.ldapContext =  new InitialDirContext(environment);
    }

    public PeopleService(Hashtable<String,String> env) throws NamingException {
        ldapContext = new InitialDirContext(env);
    }

    public String getAllUsers() throws NamingException, JSONException {;
        String filter = "objectclass=person";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = ldapContext.search(Utility.BASE_URL, filter, searchControls);
        JSONArray jArray = new JSONArray();
        Utility.jsonUserBuilder(answer, jArray);
        answer.close();
        return jArray.toString();
    }

    public void save(User user) throws NoSuchAlgorithmException, JSONException, NamingException {
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
        container.put(new BasicAttribute("uid", user.getCn()));
        String hashedPwd = PasswordUtil.generateSSHA(user.getPassword().getBytes(StandardCharsets.UTF_8));
        container.put(new BasicAttribute("userPassword", hashedPwd));
        container.put(new BasicAttribute("sambaSID", "S-1-5-21-1288326302-1102467403-3443272390-3000"));
        container.put(new BasicAttribute("homeDirectory", "/home/users/"+user.getCn()));
        container.put(new BasicAttribute("loginShell", "/bin/bash"));
        container.put(new BasicAttribute("uidNumber",String.valueOf(getMaxUidNumber()+1)));
        container.put(new BasicAttribute("gidNumber","500"));
        container.put(new BasicAttribute("sambaAcctFlags","[U]"));
        String userDN = "cn=" + user.getCn() + "," + Utility.USER_CONTEXT;
        log.debug(container.toString());
        ldapContext.createSubcontext(userDN, container);
    }

    public void deleteUser(String cn) throws NamingException {
        ldapContext.destroySubcontext("cn="+cn+","+ Utility.USER_CONTEXT);
    }

    public void putUser(User user) throws NamingException, JSONException {
        ModificationItem[] mods = new ModificationItem[11];
        Attribute objClasses = new BasicAttribute("objectClass","inetOrgPerson");
        objClasses.add("top");
        objClasses.add("sambaSamAccount");
        objClasses.add("posixAccount");

        mods[0] = putNewModificationAttribute("sn",user.getSn());
        mods[1] = putNewModificationAttribute("givenName",user.getGivenName());
        mods[2] = putNewModificationAttribute("mail",user.getEmail());
        mods[3] = putNewModificationAttribute("Uid",user.getUid());

        if(user.getUidNumber() == null || user.getUidNumber().isEmpty() ){
            mods[4] = putNewModificationAttribute("uidNumber",String.valueOf(getMaxUidNumber()+1));
        }else{
            mods[4] = putNewModificationAttribute("uidNumber",user.getUidNumber());
        }

        mods[5] = putNewModificationAttribute("sambaSID", "S-1-5-21-1288326302-1102467403-3443272390-3000");
        mods[6] = putNewModificationAttribute("homeDirectory","/home/users/"+user.getCn());
        mods[7] = putNewModificationAttribute("loginShell","/bin/bash");
        mods[8] = putNewModificationAttribute("sambaAcctFlags","[U]");
        mods[9] = putNewModificationAttribute("gidNumber","500");
        mods[10] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, objClasses);

        ldapContext.modifyAttributes("cn=" + user.getCn() + "," + Utility.USER_CONTEXT, mods);
    }

    private ModificationItem putNewModificationAttribute(String key, Object value) {
        return new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(key, value));
    }

    public String findUser(String cn) throws NamingException, JSONException {
        JSONArray jArray = new JSONArray();

        String filter = "(&(objectclass=person)(cn="+ cn + "))";
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = ldapContext.search(Utility.BASE_URL, filter, ctrl);
        Utility.jsonUserBuilder(answer, jArray);
        answer.close();

        return jArray.toString();
    }

    private Integer getMaxUidNumber() throws JSONException, NamingException {
        Integer maxUid = Integer.valueOf(1000);
        JSONArray jArray = new JSONArray();
        String filter = "uidNumber=*";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = ldapContext.search(Utility.BASE_URL,filter,searchControls);
        Utility.jsonUserBuilder(answer, jArray);

        for(int i =0; i<jArray.length();i++){
            JSONObject jsonObject = jArray.getJSONObject(i);
            if (jsonObject.get("uidNumber") != null){
                int integerJson = Integer.parseInt(jsonObject.get("uidNumber").toString());
                if( integerJson > maxUid){
                    maxUid = integerJson;
                }

            }
        }
        answer.close();
        return maxUid;
    }

}
