package spring.charityapp.controller;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    @PostMapping("/create-payment-intent")
    public String createPaymentIntent(@RequestParam("amount") long amount,
                                      @RequestParam("charityProgramId") String charityProgramId) {
        try {
            // Set your secret key
            Stripe.apiKey = "#  remove for securituy reasons";

            // Create payment intent with metadata
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount * 100) // Amount in cents
                    .setCurrency("usd")
                    .putMetadata("charityProgramId", charityProgramId) // Add charityProgramId as metadata
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return paymentIntent.getClientSecret(); // Return client secret to the frontend
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating payment intent";
        }
    }
}