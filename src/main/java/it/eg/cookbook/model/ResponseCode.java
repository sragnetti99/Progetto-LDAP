package it.eg.cookbook.model;

public enum ResponseCode {

    OK("Ok"),
    GROUP_NOT_FOUND("Gruppo non trovato"),
    USER_NOT_FOUND("Utente non trovato"),
    USER_EXISTS("Utente già presente"),
    USER_NOT_IN_GROUP("L'utente non è presente all'interno del gruppo"),
    BAD_FORMAT("Il body inserito non è nel formato corretto"),
    WRONG_DN("Errore nell'inserimento del dn"),
    GENERIC("Errore generico");

    private String message;

    public String getMessage() {
        return message;
    }

    ResponseCode(String message) {
        this.message = message;
    }

}
