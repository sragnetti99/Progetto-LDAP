package it.eg.cookbook.controller;

import it.eg.cookbook.error.BusinessException;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.model.User;
import it.eg.cookbook.service.PeopleService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/people")
@Slf4j
public class PeopleController implements PeopleApi {

    @Autowired
    private PeopleService peopleService;

    @GetMapping(path = {"/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllUsers() {
        try {
            return peopleService.getAllUsers();
        } catch (NamingException e) {
            throw new BusinessException(ResponseCode.GENERIC);
        } catch (JSONException e) {
            throw new BusinessException(ResponseCode.BAD_FORMAT);
        }
    }

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage postUser(@RequestBody User user) {
        try {
            this.peopleService.save(user);
            return new ResponseMessage(true, ResponseCode.OK, "Utente inserito correttamente");
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(ResponseCode.GENERIC);
        } catch (JSONException e) {
            throw new BusinessException(ResponseCode.BAD_FORMAT);
        } catch (NamingException e) {
            throw new BusinessException(ResponseCode.USER_EXISTS);
        }
    }

    @DeleteMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage deleteUser(@RequestBody String cn) throws NamingException {
        try {
            String commonName = new JSONObject(cn).getString("cn");
            if (this.peopleService.findUser(commonName) == null || this.peopleService.findUser(commonName).length() <= 2) {
                throw new BusinessException(ResponseCode.USER_NOT_FOUND);
            } else {
                this.peopleService.deleteUser(commonName);
                return new ResponseMessage(true, ResponseCode.OK, "Utente eliminato correttamente");
            }
        } catch (JSONException e) {
            throw new BusinessException(ResponseCode.BAD_FORMAT);
        }
    }

    @PutMapping(path = "/",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage putUser(@RequestBody User user) {
        try {
            this.peopleService.putUser(user);
            return new ResponseMessage(true, ResponseCode.OK, "Utente aggiornato correttamente");
        } catch (NamingException e) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        } catch (JSONException e) {
            throw new BusinessException(ResponseCode.BAD_FORMAT);
        }
    }
}
