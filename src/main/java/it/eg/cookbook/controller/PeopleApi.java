package it.eg.cookbook.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.model.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.NamingException;

@Api(value = "/api/v1/people", produces = MediaType.APPLICATION_JSON_VALUE, tags = "Rest API - People CRUD")
public interface PeopleApi {

    @ApiOperation(value = "Ritorna tutti gli utenti")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Utenti non trovati", response = String.class)
    })
    String getAllUsers();

    @ApiOperation(value = "Aggiunge un nuovo utente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Utente già esistente", response = String.class)
    })
    ResponseMessage postUser(@RequestBody User user) throws NamingException;

    @ApiOperation(value = "Elimina un utente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Utente non trovato", response = String.class),
            @ApiResponse(code = 400, message = "Formato del parametro errato", response = String.class)
    })
    ResponseMessage deleteUser(@RequestBody String cn) throws NamingException;

    @ApiOperation(value = "Modifica un utente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ResponseMessage.class),
            @ApiResponse(code = 404, message = "Utente non trovato", response = String.class),
            @ApiResponse(code = 400, message = "Formato del parametro errato", response = String.class)
    })
    ResponseMessage putUser(@RequestBody User user) throws NamingException;

}
