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

    public static void jsonUserBuilder(NamingEnumeration<SearchResult> answer, JSONArray jArray) throws NamingException, JSONException {
        while (answer.hasMore()) {
            SearchResult result = (SearchResult) answer.next();
            Attributes a = result.getAttributes();
            JSONObject cnJson = new JSONObject();

            checkIfNullThanReturnEmpty(a,"mail",cnJson);

            checkIfNullThanReturnEmpty(a,"userPassword",cnJson);

            checkIfNullThanReturnEmpty(a,"uid",cnJson);

            checkIfNullThanReturnEmpty(a,"objectclass",cnJson);

            checkIfNullThanReturnEmpty(a,"givenName",cnJson);

            checkIfNullThanReturnEmpty(a,"sambaLMPassword",cnJson);

            checkIfNullThanReturnEmpty(a,"sambaNTPassword",cnJson);

            checkIfNullThanReturnEmpty(a,"sambaSID",cnJson);

            checkIfNullThanReturnEmpty(a,"homeDirectory",cnJson);

            checkIfNullThanReturnEmpty(a,"loginShell",cnJson);

            checkIfNullThanReturnEmpty(a,"uidNumber",cnJson);

            checkIfNullThanReturnEmpty(a,"sambaAcctFlags",cnJson);


            jArray.put(cnJson);
        }


    }

    private static void checkIfNullThanReturnEmpty(Attributes a, String key, JSONObject cnJson) throws JSONException, NamingException {
        if (a.get(key) != null) {
            cnJson.put(key, a.get(key).get());
        }else{
            cnJson.put(key,"");
        }
    }

}

