package eu.msdhn.openmodel_langchain.demo.rag.repository;

import eu.msdhn.openmodel_langchain.demo.rag.model.PolicyVectorDocument;

public interface PolicyVectorStore {

    void upsert(PolicyVectorDocument document);
}
