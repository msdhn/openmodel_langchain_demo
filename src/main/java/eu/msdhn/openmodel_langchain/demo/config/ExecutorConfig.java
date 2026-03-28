package eu.msdhn.openmodel_langchain.demo.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

    @Bean(name = "ingestionExecutorService", destroyMethod = "shutdown")
    public ExecutorService ingestionExecutorService() {
        return Executors.newSingleThreadExecutor();
    }
}
