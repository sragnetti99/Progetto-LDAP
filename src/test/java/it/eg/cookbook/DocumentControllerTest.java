package it.eg.cookbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.eg.cookbook.model.Document;
import it.eg.cookbook.model.ResponseCode;
import it.eg.cookbook.model.ResponseMessage;
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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DocumentControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static final String URI = "/api/v1/document";
    private static final String URI_ID = "/api/v1/document/{documentId}";

    @Test
    @Order(1)
    void getDocumentsTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che la lista di documenti non sia vuota
        Document[] documents = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Document[].class);
        assertEquals(3, documents.length);
    }

    @Test
    @Order(2)
    void getDocumentTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(URI_ID, "doc-1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        Document documento = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Document.class);
        assertEquals("doc-1", documento.getId());
    }

    @Test
    @Order(3)
    void getDocumentTestKO() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get(URI_ID, "XX").accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.DOCUMENTO_NON_TROVATO.name(), responseMessage.getCode());
        assertEquals(ResponseCode.DOCUMENTO_NON_TROVATO.getMessage(), responseMessage.getMessage());
        assertEquals(null, responseMessage.getDescription());
    }

    @Test
    @Order(4)
    void deleteDocumentTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI_ID, "doc-1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(true, responseMessage.getSuccess());
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertEquals(ResponseCode.OK.getMessage(), responseMessage.getMessage());
        assertEquals("Documento eliminato correttamente", responseMessage.getDescription());
    }

    @Test
    @Order(5)
    void deleteDocumentTestKO() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.delete(URI_ID, "XX").accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.DOCUMENTO_NON_TROVATO.name(), responseMessage.getCode());
        assertEquals(ResponseCode.DOCUMENTO_NON_TROVATO.getMessage(), responseMessage.getMessage());
        assertEquals(null, responseMessage.getDescription());
    }

    @Test
    @Order(6)
    void postDocumentTest() throws Exception {
        Document document = new Document("doc-5", "Documento 5", "Descrizione Documento 5");
        String documentStr = objectMapper.writeValueAsString(document);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(documentStr))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(true, responseMessage.getSuccess());
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertEquals(ResponseCode.OK.getMessage(), responseMessage.getMessage());
        assertEquals("Documento creato correttamente", responseMessage.getDescription());
    }

    @Test
    @Order(7)
    void postDocumentTestKO() throws Exception {
        Document document = new Document("doc-2", "Documento 5", "Descrizione Documento 5");
        String documentStr = objectMapper.writeValueAsString(document);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(documentStr))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.DOCUMENTO_GIA_PRESENTE.name(), responseMessage.getCode());
        assertEquals(ResponseCode.DOCUMENTO_GIA_PRESENTE.getMessage(), responseMessage.getMessage());
        assertEquals(null, responseMessage.getDescription());
    }

    @Test
    @Order(8)
    void putDocumentTest() throws Exception {
        Document document = new Document("doc-2", "Documento 2", "Descrizione corretta");
        String documentStr = objectMapper.writeValueAsString(document);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(documentStr))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseMessage.class);
        assertEquals(true, responseMessage.getSuccess());
        assertEquals(ResponseCode.OK.name(), responseMessage.getCode());
        assertEquals(ResponseCode.OK.getMessage(), responseMessage.getMessage());
        assertEquals("Documento aggiornato correttamente", responseMessage.getDescription());
    }

    @Test
    @Order(9)
    void putDocumentTestKO() throws Exception {
        Document document = new Document("doc-6", "Documento 6", "Descrizione Documento 6");
        String documentStr = objectMapper.writeValueAsString(document);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(documentStr))
                .andReturn();

        // Verifico lo stato della risposta
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

        // Verifico che lo Documento sia corretto
        ResponseMessage responseMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ResponseMessage.class);
        assertEquals(false, responseMessage.getSuccess());
        assertEquals(ResponseCode.DOCUMENTO_NON_TROVATO.name(), responseMessage.getCode());
        assertEquals(ResponseCode.DOCUMENTO_NON_TROVATO.getMessage(), responseMessage.getMessage());
    }

}