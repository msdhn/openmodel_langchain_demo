package eu.msdhn.openmodel_langchain.demo.rag.repository;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import eu.msdhn.openmodel_langchain.demo.rag.model.PolicyVectorDocument;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "assistant.rag.vector-store", havingValue = "qdrant")
public class QdrantPolicyVectorStore implements PolicyVectorStore {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final int embeddingDimension;

    public QdrantPolicyVectorStore(
            EmbeddingModel embeddingModel,
            @Value("${assistant.rag.qdrant.host:localhost}") String host,
            @Value("${assistant.rag.qdrant.port:6334}") int port,
            @Value("${assistant.rag.qdrant.collection:bank-policies}") String collectionName,
            @Value("${assistant.rag.qdrant.use-tls:false}") boolean useTls,
            @Value("${assistant.rag.qdrant.api-key:}") String apiKey,
            @Value("${assistant.rag.qdrant.payload-text-key:text_segment}") String payloadTextKey,
            @Value("${assistant.rag.embedding-dimension:512}") int embeddingDimension
    ) {
        this.embeddingModel = embeddingModel;
        this.embeddingDimension = embeddingDimension;

        QdrantGrpcClient.Builder grpcBuilder = QdrantGrpcClient.newBuilder(host, port, useTls);
        if (apiKey != null && !apiKey.isBlank()) {
            grpcBuilder.withApiKey(apiKey);
        }
        QdrantClient client = new QdrantClient(grpcBuilder.build());
        ensureCollection(client, collectionName, embeddingDimension);

        QdrantEmbeddingStore.Builder builder = QdrantEmbeddingStore.builder()
                .client(client)
                .collectionName(collectionName)
                .useTls(useTls)
                .payloadTextKey(payloadTextKey);

        if (apiKey != null && !apiKey.isBlank()) {
            builder.apiKey(apiKey);
        }

        this.embeddingStore = builder.build();
        log.info(
                "Initialized QdrantEmbeddingStore host={} port={} collection={} dimension={}",
                host,
                port,
                collectionName,
                embeddingDimension
        );
    }

    @Override
    public void upsert(PolicyVectorDocument document) {
        String qdrantPointId = toQdrantPointId(document.id());
        Metadata metadata = toMetadata(document);
        TextSegment textSegment = TextSegment.from(document.content(), metadata);
        Embedding embedding = embeddingModel.embed(textSegment).content();
        Embedding normalizedEmbedding = normalizeDimension(embedding, embeddingDimension);

        embeddingStore.addAll(
                List.of(qdrantPointId),
                List.of(normalizedEmbedding),
                List.of(textSegment)
        );
    }

    private String toQdrantPointId(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        try {
            return UUID.fromString(rawId).toString();
        } catch (IllegalArgumentException ignored) {
            return UUID.nameUUIDFromBytes(rawId.getBytes(StandardCharsets.UTF_8)).toString();
        }
    }

    private Metadata toMetadata(PolicyVectorDocument document) {
        Metadata metadata = new Metadata();
        metadata.put("documentId", document.id());

        if (document.metadata() != null) {
            document.metadata().forEach((key, value) -> {
                if (value != null && !value.isBlank()) {
                    metadata.put(key, value);
                }
            });
        }
        return metadata;
    }

    private Embedding normalizeDimension(Embedding embedding, int targetDimension) {
        float[] source = embedding.vector();
        if (source.length == targetDimension) {
            return embedding;
        }

        float[] normalized = new float[targetDimension];
        int copyLength = Math.min(source.length, targetDimension);
        System.arraycopy(source, 0, normalized, 0, copyLength);

        log.warn(
                "Normalized embedding dimension from {} to {} (truncate/pad).",
                source.length,
                targetDimension
        );
        return Embedding.from(normalized);
    }

    private void ensureCollection(QdrantClient client, String collectionName, int embeddingDimension) {
        try {
            boolean exists = client.collectionExistsAsync(collectionName).get();
            if (exists) {
                return;
            }

            Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                    .setSize(embeddingDimension)
                    .setDistance(Collections.Distance.Cosine)
                    .build();

            client.createCollectionAsync(collectionName, vectorParams).get();
            log.info("Created Qdrant collection={} dimension={}", collectionName, embeddingDimension);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while ensuring Qdrant collection.", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("Failed to ensure Qdrant collection.", exception);
        }
    }
}
