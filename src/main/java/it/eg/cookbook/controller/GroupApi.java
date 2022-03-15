package it.eg.cookbook.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.eg.cookbook.model.Document;
import it.eg.cookbook.model.ResponseMessage;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.NamingException;
import java.util.List;

@Api(value = "/api/v1/group", produces = MediaType.APPLICATION_JSON_VALUE, tags = "Rest API - Group CRUD")
public interface GroupApi {

    @ApiOperation(value = "Ritorna tutti i gruppi")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class)
    })
    String getAllGroups() throws NamingException, JSONException;

    @ApiOperation(value = "Ritorna la lista di utenti presenti in un Organizational Unit")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 400, message = "Errore nel reperimento degli utenti", response = String.class)
    })
    String getAllUsersInOu(@PathVariable String groupId);

    @ApiOperation(value = "Ritorna la lista di tutti gli uniqueMember di un gruppo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 400, message = "Errore nel reperimento degli uniqueMember", response = String.class)
    })
    String getUsersInGroup(@RequestParam String cn, @RequestParam String ou);

    @ApiOperation(value = "Ritorna l'utente cercato se è presente nel gruppo indicato")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 400, message = "Errore nel reperimento dell'utente", response = String.class)
    })
    String findUserInOu(@RequestParam String cn, @RequestParam String ou);

    @ApiOperation(value = "Elimina un utente da un gruppo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 400, message = "Impossibile cancellare l'utente dal gruppo")
    })
    ResponseMessage deleteUserFromGroup(@RequestBody String uniqueMember, @PathVariable String groupId) throws JSONException, NamingException;


    @ApiOperation(value = "Aggiunge un utente ad un gruppo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 400, message = "Impossibile inserire l'utente nel gruppo", response = ResponseMessage.class)
    })
    ResponseMessage postUser(@RequestBody String uniqueMember, @PathVariable("id") String groupId);

}
