package eu.msdhn.openmodel_langchain.demo.rag.chunking;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "assistant.rag.chunker-type", havingValue = "paragraph")
public class ParagraphTextChunker implements TextChunker {

    private static final String PARAGRAPH_SEPARATOR = "\n\n";

    private final int chunkSize;

    public ParagraphTextChunker(
            @Value("${assistant.rag.chunk-size:1000}") int chunkSize
    ) {
        this.chunkSize = chunkSize;
    }

    @Override
    public List<String> chunk(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }

        String[] paragraphs = content.split(PARAGRAPH_SEPARATOR);
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            if (current.length() + trimmed.length() + PARAGRAPH_SEPARATOR.length() > chunkSize
                    && !current.isEmpty()) {
                chunks.add(current.toString().trim());
                current = new StringBuilder();
            }

            if (!current.isEmpty()) {
                current.append(PARAGRAPH_SEPARATOR);
            }
            current.append(trimmed);
        }

        if (!current.isEmpty()) {
            String last = current.toString().trim();
            if (!last.isEmpty()) {
                chunks.add(last);
            }
        }

        return chunks;
    }
}