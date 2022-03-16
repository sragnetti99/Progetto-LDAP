package it.eg.cookbook.controller;

import it.eg.cookbook.error.BusinessException;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.service.GroupService;
import it.eg.cookbook.service.PeopleService;
import it.eg.cookbook.utilities.Utility;
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
    private PeopleService peopleService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
        this.peopleService = new PeopleService();
    }

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
        if(!Utility.isUserEmpty(this.groupService.findUserInGroup(uniqueMember, groupId))){
            this.groupService.deleteUserFromGroup(uniqueMember, groupId);
            return new ResponseMessage(true, ResponseCode.OK, "Utente eliminato correttamente dal gruppo");
        } else {
            throw new BusinessException(ResponseCode.USER_NOT_IN_GROUP);
        }
    }

    @PostMapping(path = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage postUser(@RequestBody String uniqueMember, @PathVariable String groupId) {
        try {
            if(!Utility.isUserEmpty(this.peopleService.findUser(uniqueMember.substring(uniqueMember.indexOf("=")+1, uniqueMember.indexOf(","))))){
                this.groupService.addUserToGroup(uniqueMember, groupId);
                return new ResponseMessage(true, ResponseCode.OK, "Utente inserito correttamente nel gruppo");
            } else {
                throw new BusinessException(ResponseCode.USER_NOT_FOUND);
            }
        } catch (NamingException | JSONException e) {
            throw new BusinessException(ResponseCode.ALREADY_ADDED);
        }
    }
}




