package it.eg.cookbook.service;

import it.eg.cookbook.utilities.Utility;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Service
public class GroupService {

    @Autowired
    private PeopleService peopleService;

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

    public String getUsersInGroup(String groupId) throws NamingException, JSONException {
        String groupDN = "cn=" + groupId + "," + "ou=groups," + Utility.BASE_DN;
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));

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
            String member = a.get("uniqueMember").get(i).toString();
            cnJson.put("user", member.substring(member.indexOf("=")+1, member.indexOf(",")));
            cnJson.put("location", dn.substring(dn.indexOf("=")+1, dn.indexOf(",")));
            jArray.put(cnJson);
        }
        answer.close();
        return jArray.toString();
    }

    public JSONArray getAllGroups() throws JSONException, NamingException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));
        JSONArray jArray = new JSONArray();

        String filter = "objectclass=groupOfUniqueNames";
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = context.search(Utility.GROUP_CONTEXT, filter, ctrl);

        while (answer.hasMore()) {
            JSONObject cnJson = new JSONObject();
            String cn = answer.next().getAttributes().get("cn").get().toString();
            cnJson.put("name", cn);
            jArray.put(cnJson);
        }
        answer.close();

        return jArray;
    }

    public void deleteUserFromGroup(String uniqueMember, String groupId) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));
        BasicAttribute member = new BasicAttribute("uniquemember", new JSONObject(uniqueMember).get("uniquemember"));
        Attributes attributes = new BasicAttributes();
        attributes.put(member);
        context.modifyAttributes("cn=" + groupId + "," + Utility.GROUP_CONTEXT, DirContext.REMOVE_ATTRIBUTE, attributes);
    }

    public String findUserInGroup(String uniqueMember, String groupId) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));
        JSONArray jArray = new JSONArray();

        String member = new JSONObject(uniqueMember).get("uniquemember").toString();
        String filter = "uniquemember=" + member;
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = context.search("cn=" + groupId + "," + Utility.GROUP_CONTEXT, filter, searchControls);
        Utility.jsonUserBuilder(answer, jArray);
        answer.close();

        return jArray.toString();
    }

   /* public boolean addUserToGroup(String uniqueMembers, String groupId) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(Utility.getEnv(Utility.URL));
        Attributes attributes = new BasicAttributes();
        JSONObject jsonObject = new JSONObject(uniqueMembers);
        JSONArray members = jsonObject.getJSONArray("uniquemember");
        boolean isUserAdded = true;
        for (int i = 0; i < members.length(); i++) {
            String cn = members.getString(i).substring(members.getString(i).indexOf("=")+1, members.getString(i).indexOf(","));
            if (this.peopleService.findUser(cn).length()<=2) {
                isUserAdded = false;
            } else {
                String currentMember = members.getString(i);
                BasicAttribute memberAtt = new BasicAttribute("uniqueMember", currentMember);
                attributes.put(memberAtt);
                context.modifyAttributes("cn=" + groupId + "," + Utility.GROUP_CONTEXT, DirContext.ADD_ATTRIBUTE, attributes);
            }
        }
        return isUserAdded;
    }
    */

    // MAP --> cn utente, esiste ? true : false
    public Map<String, Boolean> getUserMap(String users) throws JSONException, NamingException {
        Map<String, Boolean> map = new HashMap<>();
        JSONObject jsonObject = new JSONObject(users);
        JSONArray members = jsonObject.getJSONArray("uniquemember");
        for (int i = 0; i < members.length(); i++) {
            String cn = members.getString(i).substring(members.getString(i).indexOf("=") + 1, members.getString(i).indexOf(","));
            map.put(cn, this.peopleService.findUser(cn).length() > 2);
        }
        return map;
    }

    public void addUsersToGroup(List<String> usersToAdd, String groupId) throws NamingException {
        DirContext context = new InitialDirContext(this.getLdapContextEnv(Utility.URL));
        Attributes attributes = new BasicAttributes();
        for (int i = 0; i < usersToAdd.size(); i++) {
            String currentMember = usersToAdd.get(i);
            System.out.println(currentMember);
            BasicAttribute memberAtt = new BasicAttribute("uniquemember", "cn=" + currentMember + "," + Utility.USER_CONTEXT);
            attributes.put(memberAtt);
            context.modifyAttributes("cn=" + groupId + "," + Utility.GROUP_CONTEXT, DirContext.ADD_ATTRIBUTE, attributes);
        }
    }
}
