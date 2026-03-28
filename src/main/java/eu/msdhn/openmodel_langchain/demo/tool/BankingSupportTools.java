package eu.msdhn.openmodel_langchain.demo.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.ReturnBehavior;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class BankingSupportTools {

    @Tool("Use for card decline, payment failed, and card rejected scenarios.")
    public String cardDeclineChecklist(@P("The user's issue text") String issue) {
        return """
                Card decline checklist:
                - Confirm available balance and daily limits.
                - Verify the card is active and not expired.
                - Check whether online/international transactions are enabled.
                - If needed, temporarily unlock the card in your banking app and retry.
                - If issue continues, contact support for account-level verification.
                """
                .trim();
    }

    @Tool("Use for dispute, chargeback, and unauthorized transaction guidance.")
    public String disputeProcessGuidance(@P("The user's issue text") String issue) {
        return """
                Dispute guidance:
                - Open transaction details and select dispute/report issue.
                - Choose dispute reason and submit notes/evidence.
                - Save the case reference number for tracking.
                - Review timelines in your app; urgent fraud cases should be reported immediately.
                """
                .trim();
    }

    @Tool(
            value = "Use when the user asks to block, freeze, or lock a credit card."
    )
    public String blockCreditCardGuidance(@P("The user's request text") String request) {
        return """
                Credit card block guidance:
                - Immediately lock/freeze the card in your official banking app.
                - If app access is unavailable, call the bank's 24/7 card support line.
                - Confirm recent transactions and report any unauthorized charges.
                - Request replacement card issuance through official support channels.
                - Keep your case/reference number for follow-up.
                """
                .trim();
    }

    @Tool("Use for branch information, opening hours, and service availability FAQs.")
    public String branchServiceFaq(@P("The user's question text") String question) {
        return """
                Branch support:
                - Most branches operate Monday to Friday, 9:00 AM to 5:00 PM.
                - ATM and digital banking services are available 24/7.
                - For exact hours, check your bank's official branch locator.
                """
                .trim();
    }

    @Tool(value = "Use for Credit card outstanding details", returnBehavior = ReturnBehavior.IMMEDIATE)
    public String creditCardOutstandingDetails(String creditCardNumber) {
        return creditCardNumber + """
                 has outstanding of 100 Euro 
                """;
    }

}
