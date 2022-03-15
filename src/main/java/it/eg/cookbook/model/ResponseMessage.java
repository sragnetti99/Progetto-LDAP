package it.eg.cookbook.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.eg.cookbook.error.BusinessException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
public class ResponseMessage {

    @ApiModelProperty(notes = "Esito risposta", position = 1, required = true, example = "true")
    private Boolean success;

    @ApiModelProperty(notes = "Codice", position = 2, required = true, example = "TIMEOUT")
    private String code;

    @ApiModelProperty(notes = "Messaggio di risposta", position = 3, required = true, example = "Documento inserito corerttamente")
    private String message;

    @ApiModelProperty(notes = "Descrizione", position = 4, required = true, example = "Documento inserito corerttamente")
    private String description;

    public ResponseMessage(Boolean success, ResponseCode responseCode, String description) {
        this.success = success;
        this.code = responseCode.name();
        this.message = responseCode.getMessage();
        this.description = description;
    }

    public ResponseMessage(Exception exception) {
        this.success = false;
        this.code = null;
        this.message = exception.getMessage();
        this.description = null;
    }

    public ResponseMessage(BusinessException exception) {
        this.success = false;
        this.code = exception.getResponseCode().name();
        this.message = exception.getResponseCode().getMessage();
        this.description = exception.getCause() == null ? null : exception.getCause().getMessage();
    }

}
