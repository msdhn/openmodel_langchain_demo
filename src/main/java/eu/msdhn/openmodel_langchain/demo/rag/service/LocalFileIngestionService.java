package eu.msdhn.openmodel_langchain.demo.rag.service;

import eu.msdhn.openmodel_langchain.demo.rag.chunking.TextChunker;
import eu.msdhn.openmodel_langchain.demo.rag.model.PolicyVectorDocument;
import eu.msdhn.openmodel_langchain.demo.rag.parsing.DocumentParser;
import eu.msdhn.openmodel_langchain.demo.rag.parsing.DocumentParseException;
import eu.msdhn.openmodel_langchain.demo.rag.repository.PolicyVectorStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileIngestionService implements IngestionService {

    private final DocumentParser documentParser;
    private final TextChunker textChunker;
    private final PolicyVectorStore policyVectorStore;

    @Override
    public int ingestFromFolder(Path folderPath) {
        if (!Files.exists(folderPath)) {
            log.warn("Policy folder does not exist: {}", folderPath.toAbsolutePath());
            return 0;
        }

        List<Path> supportedFiles = findSupportedFiles(folderPath);
        int totalChunks = 0;

        for (Path file : supportedFiles) {
            totalChunks += ingestFile(file).size();
        }

        log.info("Policy ingestion completed. files={}, chunks={}", supportedFiles.size(), totalChunks);
        return totalChunks;
    }

    @Override
    public List<PolicyVectorDocument> ingestFile(Path file) {
        String content;
        try {
            content = documentParser.parse(file);
        } catch (DocumentParseException exception) {
            log.error("Failed to parse document: {}", file, exception);
            return List.of();
        }

        if (content == null || content.isBlank()) {
            log.warn("Empty content after parsing: {}", file.getFileName());
            return List.of();
        }

        List<String> chunks = textChunker.chunk(content);
        String sourcePath = file.toString();
        List<PolicyVectorDocument> documents = new ArrayList<>();

        for (int index = 0; index < chunks.size(); index++) {
            String chunk = chunks.get(index);
            String documentId = sourcePath + "#chunk-" + index;

            PolicyVectorDocument document = new PolicyVectorDocument(
                    documentId,
                    chunk,
                    Map.of(
                            "source", sourcePath,
                            "chunkIndex", String.valueOf(index),
                            "fileName", file.getFileName().toString()
                    )
            );
            policyVectorStore.upsert(document);
            documents.add(document);
        }

        log.info("Ingested file={} chunks={}", file.getFileName(), chunks.size());
        return documents;
    }

    private List<Path> findSupportedFiles(Path folderPath) {
        try (Stream<Path> pathStream = Files.walk(folderPath)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .filter(documentParser::supports)
                    .sorted()
                    .toList();
        } catch (IOException exception) {
            log.error("Failed to scan files in folder: {}", folderPath, exception);
            return List.of();
        }
    }
}