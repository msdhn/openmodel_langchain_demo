package eu.msdhn.openmodel_langchain.demo.dto;

public record ChatResponse(
        String answer,
        String riskFlag,
        String nextSafeStep,
        String disclaimer
) {
}
