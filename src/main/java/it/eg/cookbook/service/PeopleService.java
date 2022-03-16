package it.eg.cookbook.service;


import it.eg.cookbook.model.User;
import it.eg.cookbook.utilities.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

@Service
public class PeopleService {

    public String getAllUsers(String cn) throws NamingException, JSONException {
        DirContext adminContext = new InitialDirContext(Utility.getEnv(Utility.BASE_URL));

        JSONArray jArray = new JSONArray();
        if (jArray != null) {
            String filter = cn != null ? "(&(objectclass=person)(cn="+ cn + "))" : "objectclass=person";
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
            DirContext context = new InitialDirContext(Utility.getEnv(Utility.URL));

            Attribute objClasses = new BasicAttribute("objectClass");
            objClasses.add("person");
            objClasses.add("inetOrgPerson");
            objClasses.add("organizationalPerson");
            objClasses.add("top");

            Attribute cn = new BasicAttribute("cn", user.getCn());
            Attribute givenname = new BasicAttribute("givenName", user.getGivenName());
            Attribute sn = new BasicAttribute("sn", user.getSn());
            Attribute mail = new BasicAttribute("mail", user.getEmail());
            Attribute password = new BasicAttribute("userPassword", user.getPassword());
            Attribute username = new BasicAttribute("uid", user.getUid());

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
            return false;
        }
    }

    public void deleteUser(String cn) throws NamingException {
        DirContext context = new InitialDirContext(Utility.getEnv("ldap://localhost:389"));
        context.destroySubcontext("cn="+cn+","+ Utility.USER_CONTEXT);
    }

    public void putUser(User user) throws NamingException {
        DirContext context = new InitialDirContext(Utility.getEnv(Utility.URL));

        ModificationItem[] mods = new ModificationItem[5];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("givenName", user.getGivenName()));
        mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", user.getSn()));
        mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail", user.getEmail()));
        mods[3] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", user.getPassword()));
        mods[4] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("uid", user.getUid()));
        context.modifyAttributes("cn=" + user.getCn() + "," + Utility.USER_CONTEXT, mods);
    }

    public String findUser(String cn) throws NamingException, JSONException {
        DirContext context = new InitialDirContext(Utility.getEnv(Utility.BASE_URL));
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
