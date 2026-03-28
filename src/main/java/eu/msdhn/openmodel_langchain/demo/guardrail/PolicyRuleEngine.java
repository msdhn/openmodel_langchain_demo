package eu.msdhn.openmodel_langchain.demo.guardrail;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class PolicyRuleEngine {

    public String classify(String message) {
        if (message == null || message.isBlank()) {
            return "BLOCK";
        }

        String normalized = message.toLowerCase(Locale.ROOT);
        if (normalized.contains("investment advice")
                || normalized.contains("legal advice")
                || normalized.contains("tax advice")) {
            return "SENSITIVE";
        }
        return "SAFE";
    }
}
