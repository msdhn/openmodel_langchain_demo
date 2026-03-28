package eu.msdhn.openmodel_langchain.demo.guardrail;

import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ContentFilter {

    private static final Set<String> BLOCKED_PATTERNS = Set.of(
            "another customer's transaction",
            "show me customer data",
            "steal",
            "fraud",
            "violent"
    );

    public boolean isBlocked(String message) {
        if (message == null || message.isBlank()) {
            return true;
        }
        String normalized = message.toLowerCase(Locale.ROOT);
        return BLOCKED_PATTERNS.stream().anyMatch(normalized::contains);
    }
}
