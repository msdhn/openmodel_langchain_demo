package eu.msdhn.openmodel_langchain.demo.service;

import eu.msdhn.openmodel_langchain.demo.assistant.BankingAssistant;
import eu.msdhn.openmodel_langchain.demo.dto.ChatRequest;
import eu.msdhn.openmodel_langchain.demo.dto.ChatResponse;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecution;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private static final String DISCLAIMER = "I can provide support guidance but cannot perform account actions.";
    private final BankingAssistant bankingAssistant;
    private final PromptService promptService;

    public ChatResponse generateSupportResponse(ChatRequest chatRequest) {
        String systemPrompt = promptService.systemPrompt();
        Result<String> result = bankingAssistant.generateSupportResponse(systemPrompt, chatRequest.message());
        String answer = extractAnswer(result);
        String safeStep = "If needed, connect with a human support agent for account-specific action.";
        return new ChatResponse(answer, "SAFE", safeStep, DISCLAIMER);
    }

    private String extractAnswer(Result<String> result) {
        if (result.content() != null && !result.content().isBlank()) {
            return result.content();
        }

        List<ToolExecution> toolExecutions = result.toolExecutions();
        if (toolExecutions != null && !toolExecutions.isEmpty()) {
            ToolExecution lastToolExecution = toolExecutions.get(toolExecutions.size() - 1);
            String toolResult = lastToolExecution.result();
            if (toolResult != null && !toolResult.isBlank()) {
                return toolResult;
            }
        }

        return "I can help with banking support requests. Please provide a bit more detail.";
    }
}
