package it.eg.cookbook.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.model.User;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.NamingException;

@Api(value = "/api/v1/people", produces = MediaType.APPLICATION_JSON_VALUE, tags = "Rest API - People CRUD")
public interface PeopleApi {

    @ApiOperation(value = "Ritorna tutti gli utenti")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class)
    })
    String getAllUsers(@PathVariable(required = false) String cn);

    @ApiOperation(value = "Aggiunge un nuovo utente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class)
    })
    ResponseMessage postUser(@RequestBody User user) throws NamingException;

    @ApiOperation(value = "Elimina un utente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class)
    })
    ResponseMessage deleteUser(@RequestBody String cn) throws JSONException, NamingException;

    @ApiOperation(value = "Modifica un utente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class)
    })
    ResponseMessage putUser(@RequestBody User user) throws NamingException;

}
