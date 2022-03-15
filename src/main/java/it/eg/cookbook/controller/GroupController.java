package it.eg.cookbook.controller;

import it.eg.cookbook.error.BusinessException;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.service.GroupService;
import it.eg.cookbook.utility.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;

@RestController
@RequestMapping("/api/v1/group")
@Slf4j
public class GroupController implements GroupApi {

    private GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllGroups() throws NamingException, JSONException {
        return groupService.getAllGroups().toString();
    }

    @GetMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllUsersInOu(@PathVariable String groupId) {
        String user = null;
        try {
            user = groupService.getAllUsersInOu(groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NamingException ne) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        }
        if(user != null && Utilities.isUserEmpty(user)){
            throw new BusinessException(ResponseCode.GROUP_USERS_NOT_FOUND);
        } else {
            return user;
        }
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUsersInGroup(@RequestParam String ou, @RequestParam String cn) {
        try {
            return groupService.getUsersInGroup(ou, cn);
        } catch (NamingException | JSONException e) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        }
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public String findUserInOu(@RequestParam String cn, @RequestParam String ou) {
        try {
            String user = groupService.findUserInOu(cn, ou);
            if(!Utilities.isUserEmpty(user)){
                return user;
            } else {
                throw new BusinessException(ResponseCode.USER_NOT_FOUND);
            }
        } catch (NamingException | JSONException e) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage deleteUserFromGroup(@RequestBody String uniqueMember, @PathVariable String groupId) throws JSONException, NamingException {
        if(!Utilities.isUserEmpty(this.groupService.findUserInGroup(uniqueMember, groupId))){
            this.groupService.deleteUserFromGroup(uniqueMember, groupId);
            return new ResponseMessage(true, ResponseCode.OK, "Utente eliminato correttamente dal gruppo");
        } else {
            throw new BusinessException(ResponseCode.USER_NOT_IN_GROUP);
        }
    }

    @PostMapping(path = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage postUser(@RequestBody String uniqueMember, @PathVariable String groupId) {
        try {
            this.groupService.addUserToGroup(uniqueMember, groupId);
            return new ResponseMessage(true, ResponseCode.OK, "Utente inserito correttamente nel gruppo");
        } catch (NamingException | JSONException e) {
            throw new BusinessException(ResponseCode.ALREADY_ADDED);
        }
    }
}




