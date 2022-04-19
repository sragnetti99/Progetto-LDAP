package it.eg.cookbook.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.model.UserStatus;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GroupTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String URI = "/api/v1/group/";
    private static final String URI_ID = "/api/v1/group/{groupId}";

    @Test
    @Order(1)
    void getGroupsTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che la lista di gruppi li contenga tutti
        Array[] groups = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Array[].class);
        assertEquals(4, groups.length);
    }

    @Test
    @Order(2)
    void getUsersFromGroupTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(URI_ID, "Antreem").accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        // Verifico che nel gruppo ci siano degli utenti
        Array[] members = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Array[].class);
        assertTrue(members.length > 0);
    }

    @Test
    @Order(3)
    void getUsersFromGroupTestKO() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(URI_ID, "XX").accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.GROUP_NOT_FOUND.name(), responseMessage.getCode());
        assertEquals(ResponseCode.GROUP_NOT_FOUND.getMessage(), responseMessage.getMessage());
        assertEquals(null, responseMessage.getDescription());
    }

    @Test
    @Order(4)
    void addUserInGroupTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_ID, "Antreem")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"uniquemember\": [\"cn=utente,ou=people,dc=imolinfo,dc=it\"]}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertNull(responseMessage.getSuccess());
        assertNull(responseMessage.getMessage());
        assertNull(responseMessage.getDescription());

        List<UserStatus> list = new ArrayList<>();
        UserStatus status = new UserStatus();
        status.setStatus("Utente esistente");
        status.setCn("utente");
        list.add(status);
        assertEquals(list.get(0), responseMessage.getResult().get(0));
    }

    @Test
    @Order(5)
    void addUserInGroupTestKO() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_ID, "Antreem")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"uniquemember\": [\"cn=utente,ou=people,dc=imolinfo,dc=it\"]}"))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertNull(responseMessage.getSuccess());
        assertNull(responseMessage.getMessage());
        assertNull(responseMessage.getDescription());

        List<UserStatus> list = new ArrayList<>();
        UserStatus status = new UserStatus();
        status.setStatus("Utente già presente");
        status.setCn("utente");
        list.add(status);
        assertEquals(list.get(0), responseMessage.getResult().get(0));
    }

    @Test
    @Order(6)
    void deleteUserFromGroupTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI_ID, "Antreem")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"uniquemember\": \"cn=utente,ou=people,dc=imolinfo,dc=it\"}"))
                .andReturn();
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che la response della DELETE sia corretta
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(true, responseMessage.getSuccess());
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertEquals(ResponseCode.OK.getMessage(), responseMessage.getMessage());
        assertEquals("Utente eliminato correttamente dal gruppo", responseMessage.getDescription());
    }

    @Test
    @Order(7)
    void deleteUserFromGroupTestKO() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI_ID, "XX")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"uniquemember\": \"cn=utente2,ou=people,dc=imolinfo,dc=it\"}"))
                .andReturn();

        // XX non fa parte dei gruppi esistenti
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.WRONG_DN.name(), responseMessage.getCode());
        assertEquals(ResponseCode.WRONG_DN.getMessage(), responseMessage.getMessage());
        assertEquals(null, responseMessage.getDescription());


        // Il dn di unique member è errato
        MvcResult mvcResult2 = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI_ID, "Antreem")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"uniquemember\": \"cn=utente,,dc=imolinfo,dc=it\"}"))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult2.getResponse().getStatus());

        ResponseMessage responseMessage2 = objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage2.getSuccess());
        assertEquals(ResponseCode.WRONG_DN.name(), responseMessage2.getCode());
        assertEquals(ResponseCode.WRONG_DN.getMessage(), responseMessage2.getMessage());
        assertEquals(null, responseMessage2.getDescription());

        MvcResult mvcResult3 = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI_ID, "Antreem")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"uniquemember\": \"cn=utente2,ou=people,dc=imolinfo,dc=it\"}"))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult3.getResponse().getStatus());

        ResponseMessage responseMessage3 = objectMapper.readValue(mvcResult3.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage3.getSuccess());
        assertEquals(ResponseCode.USER_NOT_IN_GROUP.name(), responseMessage3.getCode());
        assertEquals(ResponseCode.USER_NOT_IN_GROUP.getMessage(), responseMessage3.getMessage());
        assertEquals(null, responseMessage3.getDescription());

        // L'utente "uten" non esiste
        MvcResult mvcResult4 = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI_ID, "Antreem")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"uniquemember\": \"cn=uten,ou=people,dc=imolinfo,dc=it\"}"))
                .andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult4.getResponse().getStatus());

        ResponseMessage responseMessage4 = objectMapper.readValue(mvcResult4.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage4.getSuccess());
        assertEquals(ResponseCode.USER_NOT_FOUND.name(), responseMessage4.getCode());
        assertEquals(ResponseCode.USER_NOT_FOUND.getMessage(), responseMessage4.getMessage());
        assertEquals(null, responseMessage4.getDescription());
    }

}