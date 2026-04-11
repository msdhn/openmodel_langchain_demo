package eu.msdhn.openmodel_langchain.demo.rag.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TikaDocumentParser implements DocumentParser {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            ".pdf", ".docx", ".doc", ".html", ".htm", ".md",
            ".txt", ".rtf", ".odt", ".pptx", ".ppt", ".xlsx", ".xls", ".csv"
    );

    private final Tika tika = new Tika();

    @Override
    public String parse(Path filePath) {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            String content = tika.parseToString(inputStream);
            log.info("Parsed file={} chars={}", filePath.getFileName(), content.length());
            return content;
        } catch (IOException | TikaException exception) {
            throw new DocumentParseException(
                    "Failed to parse document: " + filePath.getFileName(), exception
            );
        }
    }

    @Override
    public boolean supports(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();
        return SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }
}