package eu.msdhn.openmodel_langchain.demo.rag.model;

import java.util.Map;

public record PolicyVectorDocument(
        String id,
        String content,
        Map<String, String> metadata
) {
}
