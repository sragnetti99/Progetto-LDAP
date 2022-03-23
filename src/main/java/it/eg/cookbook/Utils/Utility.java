package it.eg.cookbook.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class Utility {

    public static final String BASE_DN = "dc=imolinfo,dc=it";
    public static final String URL = "ldap://localhost:389/";
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

