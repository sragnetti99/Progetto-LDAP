package it.eg.cookbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.model.User;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Array;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PeopleControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String URI = "/api/v1/people/";

    @Test
    @Order(1)
    public void getPeopleTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che l'array restituito contenga tutti gli utenti
        Array[] groups = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Array[].class);
        assertEquals(6, groups.length);
    }

    @Test
    @Order(2)
    void postUserTest() throws Exception {
        User user = new User();
        user.setCn("utenteProva");
        user.setEmail("utente@imolinfo.it");
        user.setGivenName("utente");
        user.setHomeDirectory("/home/users/utente");
        user.setPassword("password");
        user.setLoginShell("/bin/bash");
        user.setSambaAcctFlags("[U]");
        user.setSambaSID("S-1-5-21-1288326302-1102467403-3443272390-3000");
        user.setSn("utente");
        user.setUid("utente");
        user.setUidNumber("1000");

        String userStr = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userStr))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(true, responseMessage.getSuccess());
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertEquals(ResponseCode.OK.getMessage(), responseMessage.getMessage());
        assertEquals("Utente inserito correttamente", responseMessage.getDescription());
        assertNull(responseMessage.getResult());
    }

    @Test
    @Order(3)
    void postUserTestKO() throws Exception {
        User user = new User();
        user.setCn("utenteProva");
        user.setEmail("utente@imolinfo.it");
        user.setGivenName("utente");
        user.setHomeDirectory("/home/users/utente");
        user.setPassword("password");
        user.setLoginShell("/bin/bash");
        user.setSambaAcctFlags("[U]");
        user.setSambaSID("S-1-5-21-1288326302-1102467403-3443272390-3000");
        user.setSn("utente");
        user.setUid("utente");
        user.setUidNumber("1000");

        String userStr = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(userStr))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.USER_EXISTS.name(), responseMessage.getCode());
        assertEquals(ResponseCode.USER_EXISTS.getMessage(), responseMessage.getMessage());
        assertEquals("Utente già presente", responseMessage.getDescription());
        assertNull(responseMessage.getResult());
    }

    @Test
    @Order(4)
    void putUserTest() throws Exception {

    }

    @Test
    @Order(5)
    void putUserTestKO() throws Exception {

    }

    @Test
    @Order(6)
    void deleteUser() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{ \"cn\" : \"utenteProva\"}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che la response della DELETE sia corretta
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(true, responseMessage.getSuccess());
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertEquals(ResponseCode.OK.getMessage(), responseMessage.getMessage());
        assertEquals("Utente eliminato correttamente", responseMessage.getDescription());
        assertNull(responseMessage.getResult());
    }

    @Test
    @Order(7)
    void deleteUserKO() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{ \"cn\" : \"utenteProva\"}"))
                .andReturn();

        // l'utente sragnetti non esiste
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.USER_NOT_FOUND.name(), responseMessage.getCode());
        assertEquals(ResponseCode.USER_NOT_FOUND.getMessage(), responseMessage.getMessage());
        assertNull(responseMessage.getDescription());
        assertNull(responseMessage.getResult());
    }


}
