package spring.charityapp.controller;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import spring.charityapp.common.PaymentMethod;
import spring.charityapp.dto.CharityActionDTO;
import spring.charityapp.model.CharityAction;
import spring.charityapp.model.Transaction;
import spring.charityapp.service.CharityActionService;
import spring.charityapp.service.TransactionService;
import spring.charityapp.util.Mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {

    private final TransactionService transactionService;
    private final CharityActionService charityActionService;
    private final Mapper mapper = new Mapper();

    @Value("${stripe.secret.key}")
    private String endpointSecret;

    public StripeWebhookController(TransactionService transactionService,
                                   CharityActionService charityActionService) {
        this.transactionService = transactionService;
        this.charityActionService = charityActionService;
    }
    @PostMapping
    public String handleStripeWebhook(@RequestBody String payload,
                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verify the webhook signature
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            // Handle the event
            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();

                // Extract metadata and payment details
                int charityProgramId = Integer.parseInt(paymentIntent.getMetadata().get("charityProgramId"));
                float amount = paymentIntent.getAmount();

                // Save transaction to the database
                saveTransaction(charityProgramId, amount);
            }

            return "Webhook handled successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error handling webhook";
        }
    }

    private void saveTransaction(int charityProgramId, float amount) {
        System.out.println("Saving transaction: CharityProgramId=" + charityProgramId + ", Amount=" + amount);

        CharityActionDTO charityActionDTO = charityActionService.getCharityActionById(charityProgramId).orElse(null);
        CharityAction charityAction = mapper.mapCharityActionDTOToCharityAction(charityActionDTO);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDate(new Date());
        transaction.setPaymentMethod(PaymentMethod.STRIPE);
        // pass local time
        transaction.setTime(LocalTime.now());
        transaction.setCharityAction(charityAction);

        transactionService.createTransaction(mapper.mapTransactionToTransactionDTO(transaction));
    }
}