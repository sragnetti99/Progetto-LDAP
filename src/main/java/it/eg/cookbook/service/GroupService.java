package it.eg.cookbook.service;

import it.eg.cookbook.Utils.Utility;
import it.eg.cookbook.model.UserStatus;
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private Environment env;

    private final DirContext ldapContext;

    public GroupService() throws NamingException {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, env.getProperty("ldap.context"));
        environment.put(Context.PROVIDER_URL, Utility.URL);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, env.getProperty("ldap.username"));
        environment.put(Context.SECURITY_CREDENTIALS, env.getProperty("ldap.password"));
        this.ldapContext =  new InitialDirContext(environment);
    }

    public GroupService(Hashtable<String,String> env) throws NamingException {
        ldapContext = new InitialDirContext(env);
    }

    public String getUsersInGroup(String groupId) throws NamingException, JSONException {
        String groupDN = "cn=" + groupId + "," + "ou=groups," + Utility.BASE_DN;

        JSONArray jArray = new JSONArray();
        String filter = "uniqueMember=*";
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = ldapContext.search(groupDN, filter, ctrl);

        String dn;
        SearchResult result = answer.next();
        Attributes a = result.getAttributes();
        dn = result.getNameInNamespace();

        for (int i = 0; i < a.get("uniqueMember").size(); i++) {
            JSONObject cnJson = new JSONObject();
            String member = a.get("uniqueMember").get(i).toString();
            cnJson.put("user", member.substring(member.indexOf("=") + 1, member.indexOf(",")));
            cnJson.put("location", dn.substring(dn.indexOf("=") + 1, dn.indexOf(",")));
            jArray.put(cnJson);
        }
        answer.close();
        return jArray.toString();
    }

    public JSONArray getAllGroups() throws JSONException, NamingException {
        JSONArray jArray = new JSONArray();

        String filter = "objectclass=groupOfUniqueNames";
        SearchControls ctrl = new SearchControls();
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = ldapContext.search(Utility.GROUP_CONTEXT, filter, ctrl);
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
        BasicAttribute member = new BasicAttribute("uniquemember", new JSONObject(uniqueMember).get("uniquemember"));
        Attributes attributes = new BasicAttributes();
        attributes.put(member);
        ldapContext.modifyAttributes("cn=" + groupId + "," + Utility.GROUP_CONTEXT, DirContext.REMOVE_ATTRIBUTE, attributes);
    }

    public String findUserInGroup(String uniqueMember, String groupId) throws NamingException, JSONException {
        JSONArray jArray = new JSONArray();

        String filter = "uniquemember=" + uniqueMember;
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> answer = ldapContext.search("cn=" + groupId + "," + Utility.GROUP_CONTEXT, filter, searchControls);
        Utility.jsonUserBuilder(answer, jArray);
        answer.close();

        return jArray.toString();
    }

    public List<UserStatus> addUsersToGroup(String users, String groupId) throws JSONException, NamingException {
        JSONObject jsonObject = new JSONObject(users);
        JSONArray members = jsonObject.getJSONArray("uniquemember");
        Attributes attributes = new BasicAttributes();
        List<UserStatus> result = new ArrayList<>();
        for (int i = 0; i < members.length(); i++) {
            UserStatus us = new UserStatus();

            String currentMember = members.get(i).toString();
            String cn = currentMember.substring(currentMember.indexOf("=") + 1, currentMember.indexOf(","));
            us.setCn(cn);

            if (this.findUserInGroup(currentMember, groupId).length() > 2) {
                us.setStatus("Utente già presente");
            } else if (this.peopleService.findUser(cn).length() <= 2) {
                us.setStatus("Utente non esistente");
            } else {
                us.setStatus("Utente esistente");
                BasicAttribute memberAtt = new BasicAttribute("uniquemember", "cn=" + cn + "," + Utility.USER_CONTEXT);
                attributes.put(memberAtt);
                ldapContext.modifyAttributes("cn=" + groupId + "," + Utility.GROUP_CONTEXT, DirContext.ADD_ATTRIBUTE, attributes);
            }
            result.add(us);
        }
        return result;
    }

}
