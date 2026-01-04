package com.merchant.app.controller;

import com.merchant.app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestParam Long cropId, @RequestParam Double quantity,
            Authentication authentication) {
        try {
            String email = (authentication != null) ? authentication.getName() : "merchant@example.com";
            if (authentication == null) {
                return ResponseEntity.status(401).body("Not authenticated");
            }
            return ResponseEntity.ok(orderService.placeOrder(cropId, quantity, email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/merchant")
    public ResponseEntity<?> getMerchantOrders(Authentication authentication) {
        try {
            String email = (authentication != null) ? authentication.getName() : "merchant@example.com";
            return ResponseEntity.ok(orderService.getOrdersForMerchant(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/farmer")
    public ResponseEntity<?> getFarmerOrders(Authentication authentication) {
        try {
            String email = (authentication != null) ? authentication.getName() : "farmer@example.com";
            return ResponseEntity.ok(orderService.getOrdersForFarmer(email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
