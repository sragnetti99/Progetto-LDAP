package it.eg.cookbook.controller;

import it.eg.cookbook.error.BusinessException;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.service.GroupService;
import it.eg.cookbook.service.PeopleService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import javax.naming.directory.NoSuchAttributeException;

@RestController
@RequestMapping("/api/v1/group")
@Slf4j
public class GroupController implements GroupApi {

    @Autowired
    private GroupService groupService;
    @Autowired
    private PeopleService peopleService;

    @GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllGroups() throws NamingException, JSONException {
        return groupService.getAllGroups().toString();
    }

    @GetMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUsersInGroup(@PathVariable String groupId) {
        try {
            return groupService.getUsersInGroup(groupId);
        } catch (NamingException e) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        } catch (JSONException e) {
            throw new BusinessException(ResponseCode.BAD_FORMAT);
        }
    }

    @DeleteMapping(path = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage deleteUserFromGroup(@RequestBody String uniqueMember, @PathVariable String groupId) {
        try {
            String member = new JSONObject(uniqueMember).get("uniquemember").toString();
            if(this.peopleService.findUser(member.substring(member.indexOf("=")+1, member.indexOf(","))).length() <= 2){
                 throw new BusinessException(ResponseCode.USER_NOT_FOUND);
             }
            this.groupService.deleteUserFromGroup(uniqueMember, groupId);
            return new ResponseMessage(true, ResponseCode.OK, "Utente eliminato correttamente dal gruppo");

            /* else if(this.groupService.findUserInGroup(member, groupId).length() <= 2){
                throw new BusinessException(ResponseCode.USER_NOT_IN_GROUP);
            } */
        } catch (JSONException e) {
            throw new BusinessException(ResponseCode.BAD_FORMAT);
        } catch (NoSuchAttributeException e) {
            throw new BusinessException(ResponseCode.USER_NOT_IN_GROUP);
        } catch (NamingException e) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        }
    }

    @PostMapping(path = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseMessage postUser(@RequestBody String uniqueMembers, @PathVariable String groupId) {
        try {
            return new ResponseMessage(ResponseCode.OK, this.groupService.addUsersToGroup(uniqueMembers, groupId));
        } catch (JSONException e) {
            throw new BusinessException(ResponseCode.BAD_FORMAT);
        } catch (NamingException e) {
            throw new BusinessException(ResponseCode.GROUP_NOT_FOUND);
        }
    }
}




