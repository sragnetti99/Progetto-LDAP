package it.eg.cookbook.utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

public class Utilities {

    public static final String BASE_DN = "dc=imolinfo,dc=it";
    public static final String URL = "ldap://localhost:389/";
    public static final String BASE_URL = URL + BASE_DN;
    public static final String USER_CONTEXT = "ou=people,dc=imolinfo,dc=it";
    public static final String GROUP_CONTEXT = "ou=groups,dc=imolinfo,dc=it";

    public static Hashtable<String, String> getEnv(String url) {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, url);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, "cn=admin," + BASE_DN);
        environment.put(Context.SECURITY_CREDENTIALS, "password");
        return environment;
    }

    public static boolean isUserEmpty(String user){
        return user.substring(1, user.length()-1).length() == 0;
    }

    public static  void jsonUserBuilder(NamingEnumeration<SearchResult> answer, JSONArray jArray) throws NamingException, JSONException {
        while (answer.hasMore()) {
            SearchResult result = (SearchResult) answer.next();
            Attributes a = result.getAttributes();
            JSONObject cnJson = new JSONObject();

            if (a.get("mail") != null) {
                cnJson.put("mail", a.get("mail").get());
            }
            if (a.get("userpassword") != null) {
                cnJson.put("userpassword", a.get("userpassword").get());
            }
            if (a.get("uid") != null) {
                cnJson.put("uid", a.get("uid").get());
            }
            if (a.get("objectclass") != null) {
                cnJson.put("objectclass", a.get("objectclass").get());
            }
            if (a.get("givenname") != null) {
                cnJson.put("givenname", a.get("givenname").get());
            }
            if (a.get("sn") != null) {
                cnJson.put("mail", a.get("sn").get());
            }
            if (a.get("cn") != null) {
                cnJson.put("cn", a.get("cn").get());
            }
            jArray.put(cnJson);
        }
    }

}

