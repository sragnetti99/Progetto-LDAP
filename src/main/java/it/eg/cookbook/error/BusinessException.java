package it.eg.cookbook.error;

import it.eg.cookbook.model.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ResponseCode responseCode;

    public BusinessException(ResponseCode businessErrorCode) {
        super();
        this.responseCode = businessErrorCode;
    }
}
