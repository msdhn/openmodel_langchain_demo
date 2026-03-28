package eu.msdhn.openmodel_langchain.demo.rag.chunking;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SlidingWindowTextChunker implements TextChunker {

    private final int chunkSize;
    private final int chunkOverlap;

    public SlidingWindowTextChunker(
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
        if (chunkSize <= 0 || chunkOverlap < 0 || chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException("Invalid chunk config. Ensure size > 0 and 0 <= overlap < size.");
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = content.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            String chunk = content.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end >= length) {
                break;
            }
            start = end - chunkOverlap;
        }
        return chunks;
    }
}
