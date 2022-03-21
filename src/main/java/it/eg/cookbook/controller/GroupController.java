package it.eg.cookbook.controller;

import it.eg.cookbook.error.BusinessException;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.service.GroupService;
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

    @Autowired
    private GroupService groupService;

    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllGroups() throws NamingException, JSONException {
        return groupService.getAllGroups().toString();
    }

    @GetMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUsersInGroup(@PathVariable String groupId) {
        try {
            return groupService.getUsersInGroup(groupId);
        } catch (NamingException | JSONException e) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage deleteUserFromGroup(@RequestBody String uniqueMember, @PathVariable String groupId) throws JSONException, NamingException {
        if(this.groupService.findUserInGroup(uniqueMember, groupId).trim().isEmpty()){
            throw new BusinessException(ResponseCode.USER_NOT_IN_GROUP);
        } else {
            this.groupService.deleteUserFromGroup(uniqueMember, groupId);
            return new ResponseMessage(true, ResponseCode.OK, "Utente eliminato correttamente dal gruppo");
        }
    }

    @PostMapping(path = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage postUser(@RequestBody String uniqueMembers, @PathVariable String groupId) {
        try {
            if(this.groupService.addUserToGroup(uniqueMembers, groupId)){
                return new ResponseMessage(true, ResponseCode.OK, "Utente inserito correttamente nel gruppo");
            } else {
                throw new BusinessException(ResponseCode.WARNING_ADD_USERS);
            }
        } catch (NamingException e) {
            e.printStackTrace();
            throw new BusinessException(ResponseCode.ALREADY_ADDED);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }
    }
}




