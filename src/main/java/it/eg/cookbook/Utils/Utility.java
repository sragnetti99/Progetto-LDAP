package it.eg.cookbook.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchResult;

public class Utility {

    public static final String BASE_DN = "dc=imolinfo,dc=it";
    public static final String URL = "ldap://192.168.3.168:389/";
    public static final String BASE_URL = URL + BASE_DN;
    public static final String USER_CONTEXT = "ou=people,dc=imolinfo,dc=it";
    public static final String GROUP_CONTEXT = "ou=groups,dc=imolinfo,dc=it";

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
            if (a.get("sambaLMPassword") != null) {
                cnJson.put("sambaLMPassword", a.get("sambaLMPassword").get());
            }
            if (a.get("sambaNTPassword") != null) {
                cnJson.put("sambaNTPassword", a.get("sambaNTPassword").get());
            }
            if (a.get("sambaSID") != null) {
                cnJson.put("sambaSID", a.get("sambaSID").get());
            }
            if (a.get("homeDirectory") != null) {
                cnJson.put("homeDirectory", a.get("homeDirectory").get());
            }
            if (a.get("loginShell") != null) {
                cnJson.put("loginShell", a.get("loginShell").get());
            }
            if (a.get("loginShell") != null) {
                cnJson.put("loginShell", a.get("loginShell").get());
            }

            if (a.get("uidNumber") != null) {
                cnJson.put("uidNumber", a.get("uidNumber").get());
            }


            jArray.put(cnJson);
        }


    }

}

