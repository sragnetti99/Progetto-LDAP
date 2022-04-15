package it.eg.cookbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.eg.cookbook.controller.PeopleController;
import it.eg.cookbook.model.User;
import it.eg.cookbook.service.PeopleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PeopleController.class)
class PeopleControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PeopleService service;

    private static final String URI = "/api/v1/people/";

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
    void getUsers() throws Exception {
        List<User> allUsers = Arrays.asList(this.createUser("u1"), this.createUser("u2"));
        String usersStr = objectMapper.writeValueAsString(allUsers);
        given(service.getAllUsers()).willReturn(usersStr);

        mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cn", is("u1")))
                .andExpect(jsonPath("$[1].cn", is("u2")));
    }
}
