package eu.msdhn.openmodel_langchain.demo.rag.parsing;

import java.nio.file.Path;

public interface DocumentParser {

    String parse(Path filePath);

    boolean supports(Path filePath);
}