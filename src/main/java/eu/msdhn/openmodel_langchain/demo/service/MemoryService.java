package eu.msdhn.openmodel_langchain.demo.service;

import eu.msdhn.openmodel_langchain.demo.config.AssistantProperties;
import eu.msdhn.openmodel_langchain.demo.memory.MemorySummarizer;
import eu.msdhn.openmodel_langchain.demo.memory.SessionMemoryStore;
import eu.msdhn.openmodel_langchain.demo.memory.UserProfileMemoryStore;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final SessionMemoryStore sessionMemoryStore;
    private final UserProfileMemoryStore userProfileMemoryStore;
    private final MemorySummarizer memorySummarizer;
    private final AssistantProperties assistantProperties;

    public void rememberUserMessage(String sessionId, String message) {
        int maxMessages = assistantProperties.getMemory().getMaxMessages();
        sessionMemoryStore.appendMessage(sessionId, message, maxMessages);
    }

    public String summarizeSession(String sessionId) {
        List<String> messages = sessionMemoryStore.getMessages(sessionId);
        return memorySummarizer.summarize(messages);
    }

    public String supportTone(String sessionId) {
        return userProfileMemoryStore.getSupportTone(sessionId);
    }
}
