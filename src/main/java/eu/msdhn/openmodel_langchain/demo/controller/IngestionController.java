package eu.msdhn.openmodel_langchain.demo.controller;

import eu.msdhn.openmodel_langchain.demo.rag.job.PolicyDocumentIngestionJob;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class IngestionController {

    private final PolicyDocumentIngestionJob policyDocumentIngestionJob;

    @Qualifier("ingestionExecutorService")
    private final ExecutorService ingestionExecutorService;

    private final Map<String, Future<Integer>> ingestionTasks = new ConcurrentHashMap<>();

    @PostMapping("/initiate")
    public ResponseEntity<Map<String, String>> initiateIngestion() {
        String jobId = UUID.randomUUID().toString();
        Future<Integer> task = ingestionExecutorService.submit(() -> {
            log.info("Ingestion job started. jobId={}", jobId);
            int chunks = policyDocumentIngestionJob.ingestPolicies();
            log.info("Ingestion job finished. jobId={} chunks={}", jobId, chunks);
            return chunks;
        });

        ingestionTasks.put(jobId, task);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "jobId", jobId,
                        "status", "SUBMITTED"
                ));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<Map<String, String>> jobStatus(@PathVariable String jobId) {
        Future<Integer> future = ingestionTasks.get(jobId);
        if (future == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "jobId", jobId,
                            "status", "NOT_FOUND"
                    ));
        }
        if (!future.isDone()) {
            return ResponseEntity.ok(Map.of(
                    "jobId", jobId,
                    "status", "RUNNING"
            ));
        }
        try {
            Integer chunkCount = future.get();
            return ResponseEntity.ok(Map.of(
                    "jobId", jobId,
                    "status", "COMPLETED",
                    "chunks", String.valueOf(chunkCount)
            ));
        } catch (Exception exception) {
            log.error("Ingestion job failed. jobId={}", jobId, exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "jobId", jobId,
                            "status", "FAILED"
                    ));
        }
    }
}
