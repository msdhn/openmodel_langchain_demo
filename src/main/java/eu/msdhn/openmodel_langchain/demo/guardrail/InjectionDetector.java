package eu.msdhn.openmodel_langchain.demo.guardrail;

import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class InjectionDetector {

    public boolean isInjectionAttempt(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        String normalized = message.toLowerCase(Locale.ROOT);
        return normalized.contains("ignore previous instructions")
                || normalized.contains("reveal system prompt")
                || normalized.contains("developer message")
                || normalized.contains("bypass");
    }
}
