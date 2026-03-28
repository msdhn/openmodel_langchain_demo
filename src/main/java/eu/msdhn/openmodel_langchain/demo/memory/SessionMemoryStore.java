package eu.msdhn.openmodel_langchain.demo.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class SessionMemoryStore {

    private final Map<String, List<String>> sessions = new ConcurrentHashMap<>();

    public List<String> getMessages(String sessionId) {
        return new ArrayList<>(sessions.getOrDefault(sessionId, List.of()));
    }

    public void appendMessage(String sessionId, String message, int maxMessages) {
        sessions.compute(sessionId, (key, current) -> {
            List<String> values = new ArrayList<>(current == null ? List.of() : current);
            values.add(message);
            int overflow = values.size() - maxMessages;
            if (overflow > 0) {
                values = new ArrayList<>(values.subList(overflow, values.size()));
            }
            return values;
        });
    }
}
