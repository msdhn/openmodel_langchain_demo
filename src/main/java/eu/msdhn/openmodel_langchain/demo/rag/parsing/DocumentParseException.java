package eu.msdhn.openmodel_langchain.demo.rag.parsing;

public class DocumentParseException extends RuntimeException {

    public DocumentParseException(String message, Throwable cause) {
        super(message, cause);
    }
}