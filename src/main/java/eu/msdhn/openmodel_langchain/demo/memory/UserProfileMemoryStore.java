package eu.msdhn.openmodel_langchain.demo.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMemoryStore {

    private final Map<String, String> supportToneBySession = new ConcurrentHashMap<>();

    public void setSupportTone(String sessionId, String tone) {
        supportToneBySession.put(sessionId, tone);
    }

    public String getSupportTone(String sessionId) {
        return supportToneBySession.getOrDefault(sessionId, "professional");
    }
}
