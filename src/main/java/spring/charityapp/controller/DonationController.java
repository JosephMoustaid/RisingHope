package spring.charityapp.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import jakarta.websocket.Session;
import org.apache.tomcat.websocket.WsSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DonationController {



    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(
            @RequestParam String organizationName,
            @RequestParam Long amountInCents,
            @RequestParam String successUrl,
            @RequestParam String cancelUrl) throws StripeException {

        Stripe.apiKey = "sk_test_..."; // your secret key

        Map<String, Object> params = new HashMap<>();
        params.put("payment_method_types", List.of("card"));
        params.put("mode", "payment");
        params.put("success_url", successUrl);
        params.put("cancel_url", cancelUrl);

        Map<String, Object> lineItem = new HashMap<>();
        lineItem.put("quantity", 1);

        Map<String, Object> priceData = new HashMap<>();
        priceData.put("currency", "usd");
        priceData.put("unit_amount", amountInCents); // from user input

        priceData.put("product_data", Map.of("name", "Donation to " + organizationName));
        lineItem.put("price_data", priceData);

        params.put("line_items", List.of(lineItem));

        // Create a new Checkout Session
        Map<String, Object> sessionParams = new HashMap<>();
        sessionParams.put("line_items", List.of(lineItem));
        sessionParams.put("mode", "payment");
        sessionParams.put("success_url", successUrl);
        sessionParams.put("cancel_url", cancelUrl);
        sessionParams.put("payment_method_types", List.of("card"));


        com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.create(sessionParams);
        String sessionId = session.getId(); // Use getId() to retrieve the session ID
        Map<String, Object> response = new HashMap<>();
        response.put("id", sessionId);
        return ResponseEntity.ok(response);

    }

}
