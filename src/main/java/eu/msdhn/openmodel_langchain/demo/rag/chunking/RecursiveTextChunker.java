package eu.msdhn.openmodel_langchain.demo.rag.chunking;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "assistant.rag.chunker-type", havingValue = "recursive")
public class RecursiveTextChunker implements TextChunker {

    private static final List<String> SEPARATORS = List.of(
            "\n## ",    // markdown h2
            "\n### ",   // markdown h3
            "\n\n",     // paragraph
            "\n",       // line break
            ". ",       // sentence
            " "         // word
    );

    private final int chunkSize;
    private final int chunkOverlap;

    public RecursiveTextChunker(
            @Value("${assistant.rag.chunk-size:1000}") int chunkSize,
            @Value("${assistant.rag.chunk-overlap:150}") int chunkOverlap
    ) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    @Override
    public List<String> chunk(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        return recursiveChunk(content, 0);
    }

    private List<String> recursiveChunk(String text, int separatorIndex) {
        if (text.length() <= chunkSize) {
            String trimmed = text.trim();
            return trimmed.isEmpty() ? List.of() : List.of(trimmed);
        }

        if (separatorIndex >= SEPARATORS.size()) {
            // Fallback: hard split at chunkSize
            return hardSplit(text);
        }

        String separator = SEPARATORS.get(separatorIndex);
        String[] parts = text.split(escapeRegex(separator), -1);

        if (parts.length <= 1) {
            return recursiveChunk(text, separatorIndex + 1);
        }

        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            String withSeparator = (i > 0 ? separator : "") + part;

            if (current.length() + withSeparator.length() > chunkSize && !current.isEmpty()) {
                chunks.addAll(recursiveChunk(current.toString(), separatorIndex + 1));
                current = new StringBuilder(buildOverlap(current.toString()));
            }

            current.append(withSeparator);
        }

        if (!current.isEmpty()) {
            chunks.addAll(recursiveChunk(current.toString(), separatorIndex + 1));
        }

        return chunks;
    }

    private List<String> hardSplit(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end >= text.length()) {
                break;
            }
            start = end - chunkOverlap;
        }
        return chunks;
    }

    private String buildOverlap(String text) {
        if (chunkOverlap <= 0) {
            return "";
        }
        int start = Math.max(0, text.length() - chunkOverlap);
        return text.substring(start);
    }

    private String escapeRegex(String separator) {
        return separator.replace(".", "\\.").replace("#", "\\#");
    }
}