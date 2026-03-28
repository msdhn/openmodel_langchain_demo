package eu.msdhn.openmodel_langchain.demo.service.model;

import java.util.Map;

public record PolicyVectorDocument(
        String id,
        String content,
        Map<String, String> metadata
) {
}
