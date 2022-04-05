package it.eg.cookbook.model;

public enum ResponseCode {

    OK("Ok"),
    DOCUMENTO_NON_TROVATO("Documento non trovato"),
    DOCUMENTO_GIA_PRESENTE("Id documento è già presente"),
    GROUP_NOT_FOUND("Gruppo non trovato"),
    USER_NOT_FOUND("Utente non trovato"),
    USER_EXISTS("Utente già presente"),
    USER_NOT_IN_GROUP("L'utente non è presente all'interno del gruppo"),
    BAD_FORMAT("Il body inserito non è nel formato corretto"),
    GENERIC("Errore generico");

    private String message;

    public String getMessage() {
        return message;
    }

    ResponseCode(String message) {
        this.message = message;
    }

}
