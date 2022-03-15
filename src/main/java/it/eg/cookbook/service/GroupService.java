package it.eg.cookbook.service;


import it.eg.cookbook.utility.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

@Service
public class GroupService {

    public String findUserInOu(String cn, String ou) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(Utilities.getEnv(Utilities.URL));
        JSONArray jArray = new JSONArray();

        if (jArray != null) {
            String filter = "(&(objectclass=person)(cn="+ cn + "))";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = context.search("ou=" + ou + "," + Utilities.BASE_DN, filter, searchControls);
            Utilities.jsonUserBuilder(answer, jArray);
            answer.close();
        }
        return jArray.toString();
    }

    public String getAllUsersInOu(String ou) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(Utilities.getEnv(Utilities.URL));
        JSONArray jArray = new JSONArray();

        if (jArray != null) {
            String filter = "objectclass=person";
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = context.search("ou=" + ou + "," + Utilities.BASE_DN, filter, ctrl);
            Utilities.jsonUserBuilder(answer, jArray);
            answer.close();
        }
        return jArray.toString();
    }

    public String getUsersInGroup(String ou, String cn) throws NamingException, JSONException {
        String groupDN = "cn=" + cn + "," + "ou=" + ou + "," + Utilities.BASE_DN;
        DirContext context = new InitialDirContext(Utilities.getEnv(Utilities.URL));

        JSONArray jArray = new JSONArray();
        String filter = "uniqueMember=*";
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = context.search(groupDN, filter, ctrl);

        String dn;
        SearchResult result = (SearchResult) answer.next();
        Attributes a = result.getAttributes();
        dn = result.getNameInNamespace();

        for (int i = 0; i < a.get("uniqueMember").size(); i++) {
            JSONObject cnJson = new JSONObject();
            cnJson.put("uniqueMember", a.get("uniqueMember").get(i));
            cnJson.put("dn", dn);
            jArray.put(cnJson);
        }
        answer.close();
        return jArray.toString();
    }

    public JSONArray getAllGroups() throws JSONException, NamingException {
        DirContext context = new InitialDirContext(Utilities.getEnv(Utilities.URL));
        JSONArray jArray = new JSONArray();

        if (jArray != null) {
            String filter = "objectclass=groupOfUniqueNames";
            SearchControls ctrl = new SearchControls();
            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = context.search(Utilities.GROUP_CONTEXT, filter, ctrl);
            Utilities.jsonUserBuilder(answer, jArray);
            answer.close();
        }
        return jArray;
    }

    public void addUserToGroup(String uniqueMember, String groupId) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(Utilities.getEnv(Utilities.URL));
        BasicAttribute member = new BasicAttribute("uniqueMember",  new JSONObject(uniqueMember).get("uniquemember"));
        Attributes attributes = new BasicAttributes();
        attributes.put(member);
        context.modifyAttributes("cn=" + groupId + "," + Utilities.GROUP_CONTEXT, DirContext.ADD_ATTRIBUTE, attributes);

    }

    public void deleteUserFromGroup(String uniqueMember, String groupId) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(Utilities.getEnv(Utilities.URL));
        BasicAttribute member = new BasicAttribute("uniqueMember", new JSONObject(uniqueMember).get("uniquemember"));
        Attributes attributes = new BasicAttributes();
        attributes.put(member);
        context.modifyAttributes("cn=" + groupId + "," + Utilities.GROUP_CONTEXT, DirContext.REMOVE_ATTRIBUTE, attributes);
    }

    public String findUserInGroup(String uniqueMember, String groupId) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(Utilities.getEnv(Utilities.URL));
        JSONArray jArray = new JSONArray();

        if (jArray != null) {
            String member = new JSONObject(uniqueMember).get("uniquemember").toString();
            String filter = "uniqueMember=" + member;
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = context.search("cn=" + groupId + "," + Utilities.GROUP_CONTEXT, filter, searchControls);
            Utilities.jsonUserBuilder(answer, jArray);
            answer.close();
        }
        return jArray.toString();
    }
}
