package eu.msdhn.openmodel_langchain.demo.rag.service;

import eu.msdhn.openmodel_langchain.demo.rag.model.PolicyVectorDocument;

import java.nio.file.Path;
import java.util.List;

public interface IngestionService {

    int ingestFromFolder(Path folderPath);

    List<PolicyVectorDocument> ingestFile(Path file);
}