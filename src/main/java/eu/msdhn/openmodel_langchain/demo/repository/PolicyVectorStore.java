package eu.msdhn.openmodel_langchain.demo.repository;

import eu.msdhn.openmodel_langchain.demo.service.model.PolicyVectorDocument;

public interface PolicyVectorStore {

    void upsert(PolicyVectorDocument document);
}
