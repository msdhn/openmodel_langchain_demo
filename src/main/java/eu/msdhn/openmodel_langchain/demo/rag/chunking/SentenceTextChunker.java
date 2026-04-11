package eu.msdhn.openmodel_langchain.demo.rag.chunking;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "assistant.rag.chunker-type", havingValue = "sentence")
public class SentenceTextChunker implements TextChunker {

    private final int chunkSize;
    private final int chunkOverlap;

    public SentenceTextChunker(
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

        List<String> sentences = splitSentences(content);
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int overlapStart = 0;

        for (String sentence : sentences) {
            if (current.length() + sentence.length() > chunkSize && !current.isEmpty()) {
                chunks.add(current.toString().trim());
                current = new StringBuilder(buildOverlap(chunks, chunkOverlap));
            }
            current.append(sentence);
        }

        if (!current.isEmpty()) {
            String last = current.toString().trim();
            if (!last.isEmpty()) {
                chunks.add(last);
            }
        }

        return chunks;
    }

    private List<String> splitSentences(String content) {
        List<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        iterator.setText(content);

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(content.substring(start, end));
        }
        return sentences;
    }

    private String buildOverlap(List<String> chunks, int overlapSize) {
        if (chunks.isEmpty() || overlapSize <= 0) {
            return "";
        }
        String lastChunk = chunks.get(chunks.size() - 1);
        int start = Math.max(0, lastChunk.length() - overlapSize);
        return lastChunk.substring(start);
    }
}