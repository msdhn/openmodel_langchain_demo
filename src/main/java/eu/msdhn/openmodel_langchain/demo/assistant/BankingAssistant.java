package eu.msdhn.openmodel_langchain.demo.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "ollamaChatModel",
        tools = {"bankingSupportTools"}
)
public interface BankingAssistant {

    @SystemMessage("{{systemPrompt}}")
    @UserMessage("""
            User message:
            {{userMessage}}

            Provide a safe support response.
            """)
    Result<String> generateSupportResponse(
            @V("systemPrompt") String systemPrompt,
            @V("userMessage") String userMessage
    );
}
