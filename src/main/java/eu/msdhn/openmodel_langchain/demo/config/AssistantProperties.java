package eu.msdhn.openmodel_langchain.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "assistant")
@Configuration
public class AssistantProperties {

    private final Memory memory = new Memory();
    private final Safety safety = new Safety();
    private final Ollama ollama = new Ollama();

    public Memory getMemory() {
        return memory;
    }

    public Safety getSafety() {
        return safety;
    }

    public Ollama getOllama() {
        return ollama;
    }

    public static class Memory {
        private int maxMessages = 20;

        public int getMaxMessages() {
            return maxMessages;
        }

        public void setMaxMessages(int maxMessages) {
            this.maxMessages = maxMessages;
        }
    }

    public static class Safety {
        private boolean strictMode = true;

        public boolean isStrictMode() {
            return strictMode;
        }

        public void setStrictMode(boolean strictMode) {
            this.strictMode = strictMode;
        }
    }

    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String modelName = "llama3.1:8b";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }
    }
}
