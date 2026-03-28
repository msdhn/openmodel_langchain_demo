package eu.msdhn.openmodel_langchain.demo.service;

import eu.msdhn.openmodel_langchain.demo.guardrail.ContentFilter;
import eu.msdhn.openmodel_langchain.demo.guardrail.InjectionDetector;
import eu.msdhn.openmodel_langchain.demo.guardrail.PolicyRuleEngine;
import org.springframework.stereotype.Service;

@Service
public class SafetyService {

    private final InjectionDetector injectionDetector;
    private final ContentFilter contentFilter;
    private final PolicyRuleEngine policyRuleEngine;

    public SafetyService(
            InjectionDetector injectionDetector,
            ContentFilter contentFilter,
            PolicyRuleEngine policyRuleEngine
    ) {
        this.injectionDetector = injectionDetector;
        this.contentFilter = contentFilter;
        this.policyRuleEngine = policyRuleEngine;
    }

    public String classifyInput(String message) {
        if (contentFilter.isBlocked(message) || injectionDetector.isInjectionAttempt(message)) {
            return "BLOCK";
        }
        return policyRuleEngine.classify(message);
    }
}
