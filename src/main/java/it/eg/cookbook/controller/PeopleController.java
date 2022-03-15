package it.eg.cookbook.controller;

import it.eg.cookbook.error.BusinessException;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.model.User;
import it.eg.cookbook.service.PeopleService;
import it.eg.cookbook.utility.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;

@RestController
@RequestMapping("/api/v1/people")
@Slf4j
public class PeopleController implements PeopleApi {

    private PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllUsers() throws NamingException, JSONException {
        return peopleService.getAllUsers();
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public String findUser(@RequestParam String cn) throws JSONException, NamingException {
        String user = peopleService.findUser(cn);
        if(Utilities.isUserEmpty(user)){
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        } else {
            return user;
        }
    }

    @GetMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage postUser(@RequestBody User user) {
        if(this.peopleService.save(user)){
            return new ResponseMessage(true, ResponseCode.OK, "Utente inserito correttamente");
        } else {
            throw new BusinessException(ResponseCode.USER_EXISTS);
        }
    }

    @DeleteMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage deleteUser(@RequestBody String cn) throws JSONException, NamingException {
        String commonName = new JSONObject(cn).getString("cn");
        if (!Utilities.isUserEmpty(this.peopleService.findUser(commonName))) {
            this.peopleService.deleteUser(commonName);
            return new ResponseMessage(true, ResponseCode.OK, "Utente eliminato correttamente");
        } else {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
    }

    @PutMapping(path = "",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage putUser(@RequestBody User user) {
        try {
            this.peopleService.putUser(user);
            return new ResponseMessage(true, ResponseCode.OK, "Utente aggiornato correttamente");
        } catch (NamingException e) {
            e.printStackTrace();
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
    }
}
