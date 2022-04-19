package it.eg.cookbook.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.eg.cookbook.Application;
import it.eg.cookbook.controller.PeopleController;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
import it.eg.cookbook.model.User;
import it.eg.cookbook.service.PeopleService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK, classes={ Application.class })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PeopleTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PeopleService peopleService;

    @InjectMocks
    private PeopleController peopleController;

    private static final String URI = "/api/v1/people/";

    private MockMvc mockMvc;

    @BeforeEach
    void createMock() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(peopleController).build();
    }

    private User createUser(String cn) {
        User user = new User();
        user.setCn(cn);
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
        return user;
    }

    @Test
    @Order(1)
    void getPeopleTest() throws Exception {
        String users = "[{"
               + " \"uid\": \"uesterno\","
               + " \"loginShell\": \"\","
               + " \"sambaAcctFlags\": \"\","
               + " \"userPassword\": \"[B@54a3c84b\","
               + " \"homeDirectory\": \"\","
               + " \"mail\": \"uesterno@imolinfo.it\","
               + " \"sambaSID\": \"\","
               + " \"uidNumber\": \"\","
               + " \"givenName\": \"Esterno\","
               + " \"sambaNTPassword\": \"\","
               + " \"objectclass\": \"inetOrgPerson\","
               + " \"sambaLMPassword\": \"\""
        + "},{"
                + " \"uid\": \"internou\","
                + " \"loginShell\": \"\","
                + " \"sambaAcctFlags\": \"\","
                + " \"userPassword\": \"[B@1ae9c2ec\","
                + " \"homeDirectory\": \"\","
                + " \"mail\": \"internou@imolinfo.it\","
                + " \"sambaSID\": \"\","
                + " \"uidNumber\": \"\","
                + " \"givenName\": \"internou\","
                + " \"sambaNTPassword\": \"\","
                + " \"objectclass\": \"person\","
                + " \"sambaLMPassword\": \"\""
        + "},{"
                + " \"uid\": \"sragnetti\","
                + " \"loginShell\": \"/bin/bash\","
                + " \"sambaAcctFlags\": \"[U]\","
                + " \"userPassword\": \"[B@5303fde6\","
                + " \"homeDirectory\": \"/home/users/utente1\","
                + " \"mail\": \"gf@imolinfo.it\","
                + " \"sambaSID\": \"S-1-5-21-1288326302-1102467403-3443272390-3000\","
                + " \"uidNumber\": \"1000\","
                + " \"givenName\": \"sara\","
                + " \"sambaNTPassword\": \"\","
                + " \"objectclass\": \"inetOrgPerson\","
                + " \"sambaLMPassword\": \"\""
        + "},{"
                + " \"uid\": \"utente2\","
                + " \"loginShell\": \"\","
                + " \"sambaAcctFlags\": \"\","
                + " \"userPassword\": \"[B@1e613165\","
                + " \"homeDirectory\": \"\","
                + " \"mail\": \"utente2@imolainformatica.it\","
                + " \"sambaSID\": \"\","
                + " \"uidNumber\": \"\","
                + " \"givenName\": \"utente\","
                + " \"sambaNTPassword\": \"\","
                + " \"objectclass\": \"person\","
                + " \"sambaLMPassword\": \"\""
        + "},{"
                + " \"uid\": \"utente\","
                + " \"loginShell\": \"/bin/bash\","
                + " \"sambaAcctFlags\": \"[U]\","
                + " \"userPassword\": \"[B@54ca5372\","
                + " \"homeDirectory\": \"/home/users/utente\","
                + " \"mail\": \"utente@imolinfo.it\","
                + " \"sambaSID\": \"S-1-5-21-1288326302-1102467403-3443272390-3000\","
                + " \"uidNumber\": \"1002\","
                + " \"givenName\": \"utente\","
                + " \"sambaNTPassword\": \"\","
                + " \"objectclass\": \"person\","
                + " \"sambaLMPassword\": \"\""
        + "}]";

        when(peopleService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                .andExpect(jsonPath("$[0].uid", Matchers.equalTo("uesterno")));
    }

    @Test
    @Order(2)
    void postUserTest() throws Exception {
        String userStr = objectMapper.writeValueAsString(this.createUser("utenteProva"));
        MvcResult mvcResult = this.mockMvc.perform(post(URI)
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
        String userStr = objectMapper.writeValueAsString(this.createUser("utente"));
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(URI)
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
    void deleteUser() throws Exception {
        String userStr = objectMapper.writeValueAsString(this.createUser("utenteProva"));
//        MvcResult mvcResultAdd = this.mockMvc.perform(post(URI)
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(userStr))
//                .andReturn();

        MvcResult mvcResult = this.mockMvc
                    .perform(MockMvcRequestBuilders.delete(URI)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content("{ \"cn\" : \"utenteProva\"}"))
//                    .andExpect(status().isBadRequest())
                    .andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(true, responseMessage.getSuccess());
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertEquals(ResponseCode.OK.getMessage(), responseMessage.getMessage());
        assertEquals("Utente eliminato correttamente", responseMessage.getDescription());
        assertNull(responseMessage.getResult());
    }

    @Test
    @Order(5)
    void deleteUserKO() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content("{ \"cn\" : \"utenteProva\"}"))
                .andReturn();

        // l'utente utenteProva non esiste
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.USER_NOT_FOUND.name(), responseMessage.getCode());
        assertEquals(ResponseCode.USER_NOT_FOUND.getMessage(), responseMessage.getMessage());
        assertNull(responseMessage.getDescription());
        assertNull(responseMessage.getResult());
    }

   /* @Test
    @Order(6)
    void putUserTest() throws Exception {

    }

    @Test
    @Order(7)
    void putUserTestKO() throws Exception {

    }
*/
}
