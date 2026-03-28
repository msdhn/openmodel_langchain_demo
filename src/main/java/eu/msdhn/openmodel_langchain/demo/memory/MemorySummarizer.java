package eu.msdhn.openmodel_langchain.demo.memory;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MemorySummarizer {

    public String summarize(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return "No previous context.";
        }
        if (messages.size() == 1) {
            return messages.get(0);
        }
        return String.join(" | ", messages);
    }
}
