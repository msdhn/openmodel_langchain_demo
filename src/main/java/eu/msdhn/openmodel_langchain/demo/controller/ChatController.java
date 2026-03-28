package eu.msdhn.openmodel_langchain.demo.controller;

import eu.msdhn.openmodel_langchain.demo.dto.ChatRequest;
import eu.msdhn.openmodel_langchain.demo.dto.ChatResponse;
import eu.msdhn.openmodel_langchain.demo.exception.AssistantException;
import eu.msdhn.openmodel_langchain.demo.service.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final AssistantService assistantService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestParam("message") String message) {
        if (message == null || message.isBlank()) {
            throw new AssistantException("message is required.");
        }
        return assistantService.generateSupportResponse(new ChatRequest("web-session", message));
    }

}
