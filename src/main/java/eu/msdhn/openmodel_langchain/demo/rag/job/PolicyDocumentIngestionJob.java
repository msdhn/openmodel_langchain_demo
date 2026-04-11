package eu.msdhn.openmodel_langchain.demo.rag.job;

import eu.msdhn.openmodel_langchain.demo.rag.service.IngestionService;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PolicyDocumentIngestionJob {

    private final IngestionService ingestionService;
    private final String policyFolder;

    public PolicyDocumentIngestionJob(
            IngestionService ingestionService,
            @Value("${assistant.rag.policy-folder:policies}") String policyFolder
    ) {
        this.ingestionService = ingestionService;
        this.policyFolder = policyFolder;
    }

    public int ingestPolicies() {
        return ingestionService.ingestFromFolder(Paths.get(policyFolder));
    }
}