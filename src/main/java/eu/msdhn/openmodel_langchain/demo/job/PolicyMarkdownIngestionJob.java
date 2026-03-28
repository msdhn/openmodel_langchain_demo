package eu.msdhn.openmodel_langchain.demo.job;

import eu.msdhn.openmodel_langchain.demo.rag.chunking.TextChunker;
import eu.msdhn.openmodel_langchain.demo.repository.PolicyVectorStore;
import eu.msdhn.openmodel_langchain.demo.service.model.PolicyVectorDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PolicyMarkdownIngestionJob {

    private final PolicyVectorStore policyVectorStore;
    private final TextChunker textChunker;
    private final String policyFolder;

    public PolicyMarkdownIngestionJob(
            PolicyVectorStore policyVectorStore,
            TextChunker textChunker,
            @Value("${assistant.rag.policy-folder:policies}") String policyFolder
    ) {
        this.policyVectorStore = policyVectorStore;
        this.textChunker = textChunker;
        this.policyFolder = policyFolder;
    }

    public int ingestPolicies() {
        Path folderPath = Paths.get(policyFolder);
        if (!Files.exists(folderPath)) {
            log.warn("Policy folder does not exist: {}", folderPath.toAbsolutePath());
            return 0;
        }

        List<Path> markdownFiles = findMarkdownFiles(folderPath);
        int totalChunks = 0;

        for (Path markdownFile : markdownFiles) {
            totalChunks += ingestSingleFile(markdownFile);
        }

        log.info("Policy ingestion completed. files={}, chunks={}", markdownFiles.size(), totalChunks);
        return totalChunks;
    }

    private int ingestSingleFile(Path markdownFile) {
        String content;
        try {
            content = Files.readString(markdownFile, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            log.error("Failed to read markdown file: {}", markdownFile, exception);
            return 0;
        }

        List<String> chunks = textChunker.chunk(content);
        String sourcePath = markdownFile.toString();

        for (int index = 0; index < chunks.size(); index++) {
            String chunk = chunks.get(index);
            String documentId = sourcePath + "#chunk-" + index;

            PolicyVectorDocument document = new PolicyVectorDocument(
                    documentId,
                    chunk,
                    Map.of(
                            "source", sourcePath,
                            "chunkIndex", String.valueOf(index),
                            "fileName", markdownFile.getFileName().toString()
                    )
            );
            policyVectorStore.upsert(document);
        }

        log.info("Ingested file={} chunks={}", markdownFile.getFileName(), chunks.size());
        return chunks.size();
    }

    private List<Path> findMarkdownFiles(Path folderPath) {
        try (Stream<Path> pathStream = Files.walk(folderPath)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".md"))
                    .sorted()
                    .toList();
        } catch (IOException exception) {
            log.error("Failed to scan markdown files in folder: {}", folderPath, exception);
            return List.of();
        }
    }
}
