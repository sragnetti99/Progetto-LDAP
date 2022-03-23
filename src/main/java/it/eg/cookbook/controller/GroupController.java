package it.eg.cookbook.controller;

import it.eg.cookbook.config.LdapProperties;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
       /* try {
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
        } */

        try {
            Map<String, Boolean> map = this.groupService.getUserMap(uniqueMembers);
            List<String> usersToAdd =  map.entrySet().stream()
                    .filter(e-> e.getValue().equals(true))
                    .map(e->e.getKey())
                    .collect(Collectors.toList());

            List<String> notExistingUsers =  map.entrySet().stream()
                    .filter(e-> e.getValue().equals(false))
                    .map(e->e.getKey())
                    .collect(Collectors.toList());

            this.groupService.addUsersToGroup(usersToAdd, groupId);
            if(notExistingUsers.isEmpty()){
                return new ResponseMessage(true, ResponseCode.OK, "Utenti inseriti correttamente nel gruppo");
            } else {
                return new ResponseMessage(false, ResponseCode.GENERIC, "I seguenti utenti non sono stati inseriti correttamente nel gruppo perchè non esistono:" + notExistingUsers);
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




