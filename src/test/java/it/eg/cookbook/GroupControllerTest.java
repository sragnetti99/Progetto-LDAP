package it.eg.cookbook;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import it.eg.cookbook.controller.GroupController;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GroupControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockBean
    GroupController controller;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(389);

    private String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String responseString = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return responseString;
    }

    private String convertHttpResponseToString(HttpResponse httpResponse) throws IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        return convertInputStreamToString(inputStream);
    }

    private String convertInputStreamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String string = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return string;
    }

    // Server set-up
    @Test
    @Order(1)
    public void testSetUp() throws Exception {
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();
        configureFor("localhost", 389);
        stubFor(get(urlEqualTo("/api/v1/group")).willReturn(aResponse().withBody("Hello world!")));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("http://localhost:389/api/v1/group");
        HttpResponse httpResponse = httpClient.execute(request);
        String responseString = convertResponseToString(httpResponse);

        verify(getRequestedFor(urlEqualTo("/api/v1/group")));
        System.out.println(responseString);
        assertEquals("Hello world!", responseString);

        wireMockServer.stop();
    }

    // URL Matching
    @Test
    @Order(2)
    public void testURL() throws Exception {
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();
        configureFor("localhost", 389);

        stubFor(get(urlPathMatching("/api/v1/group"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("\"testing-library\": \"WireMock\"")));


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:389/api/v1/group");
        HttpResponse httpResponse = httpClient.execute(request);
        String stringResponse = convertHttpResponseToString(httpResponse);

        verify(getRequestedFor(urlEqualTo("/api/v1/group")));
        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
        assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue());
        assertEquals("\"testing-library\": \"WireMock\"", stringResponse);

        wireMockServer.stop();
    }

    // Request body matching
    @Test
    @Order(3)
    public void testBody() throws Exception {
        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();
        configureFor("localhost", 389);
        String groupName = "Antreem";

        stubFor(delete(urlPathMatching("/api/v1/group/"+ groupName))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing("\"uniquemember\": \"cn=utente,ou=people,dc=imolinfo,dc=it\""))
                .willReturn(aResponse().withStatus(200)));

//        InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("wiremock_intro.json");
//        String jsonString = convertInputStreamToString(jsonInputStream);
//        StringEntity entity = new StringEntity(jsonString);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:389/api/v1/group/" + groupName);
        request.addHeader("Content-Type", "application/json");
        //request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);

        verify(deleteRequestedFor(urlEqualTo("/api/v1/group/"+ groupName))
                .withHeader("Content-Type", equalTo("application/json")));
        assertEquals(200, response.getStatusLine().getStatusCode());

        wireMockServer.stop();
    }

    @Test
    @Order(4)
    public void getAllGroups() throws Exception {
        Mockito.when(controller.getAllGroups()).thenReturn("");

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:389/api/v1/group")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[2].name", is("Betoola")));
    }


}