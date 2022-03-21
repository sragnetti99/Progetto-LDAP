package it.eg.cookbook.service;


import it.eg.cookbook.model.User;
import it.eg.cookbook.utilities.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

@Service
public class PeopleService {

    public String getAllUsers() throws NamingException, JSONException {
        DirContext adminContext = new InitialDirContext(Utility.getEnv(Utility.BASE_URL));

        JSONArray jArray = new JSONArray();
        if (jArray != null) {
            String filter = "objectclass=person";
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> answer = adminContext.search("", filter, searchControls);
            Utility.jsonUserBuilder(answer, jArray);
            answer.close();
        }
        adminContext.close();
        return jArray.toString();
    }

    /*      PROGETTO DATABASE:
    private String generateSalt() {
        int lenght = 4;
        String abcCapitals = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String abcLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "01234567890123456789";
        String total = abcCapitals + abcLowerCase + numbers;
        String response = "";
        char letters[] = new char[lenght];
        for (int i = 0; i < lenght; i++) {
            Random r = new Random();
            char letter = total.charAt(r.nextInt(total.length()));
            letters[i] = letter;
        }
        response = Arrays.toString(letters).replaceAll("\\s+", "");
        response = response.replaceAll(",", "");
        return response;
    }

    private String generateHash(String password, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return generatedPassword;
    }
    */
    private String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
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
            Attribute password = new BasicAttribute("userPassword", hashPassword(user.getPassword()));

           /*
             PROGETTO DATABASE:
            String salt = this.generateSalt();
            String passwordHash = this.generateHash(user.getPassword(), salt.substring(1, salt.length() - 1));
            Attribute password = new BasicAttribute("userPassword", passwordHash);*/
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
            e.printStackTrace();
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
