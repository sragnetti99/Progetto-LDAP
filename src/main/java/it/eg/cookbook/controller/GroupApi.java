package it.eg.cookbook.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.eg.cookbook.model.ResponseMessage;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.NamingException;

@Api(value = "/api/v1/group", produces = MediaType.APPLICATION_JSON_VALUE, tags = "Rest API - Group CRUD")
public interface GroupApi {

    @ApiOperation(value = "Ritorna tutti i gruppi")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class)
    })
    String getAllGroups() throws NamingException, JSONException;

    @ApiOperation(value = "Ritorna la lista di tutti gli uniqueMember di un gruppo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Gruppo non trovato", response = String.class),
            @ApiResponse(code = 400, message = "Formato del parametro errato", response = String.class)
    })
    String getUsersInGroup(@PathVariable String groupId);

    @ApiOperation(value = "Elimina un utente da un gruppo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 409, message = "Impossibile eliminare l'utente dal gruppo"),
            @ApiResponse(code = 404, message = "Utente o gruppo non trovato"),
            @ApiResponse(code = 400, message = "Formato del parametro errato", response = String.class)
    })
    ResponseMessage deleteUserFromGroup(@RequestBody String uniqueMember, @PathVariable String groupId) throws NamingException;


    @ApiOperation(value = "Aggiunge un utente ad un gruppo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 409, message = "Impossibile inserire l'utente nel gruppo", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Gruppo non trovato"),
            @ApiResponse(code = 400, message = "Formato del parametro errato", response = String.class)
    })
    ResponseMessage postUser(@RequestBody String uniqueMembers, @PathVariable("id") String groupId) throws NamingException;

}
