package com.merchant.app.controller;

import com.merchant.app.dto.CropDto;
import com.merchant.app.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CropController {

    private final CropService cropService;

    @PostMapping
    public ResponseEntity<?> addCrop(@RequestBody CropDto cropDto, Authentication authentication) {
        try {
            // In a real app, use authentication.getName() (email)
            // For v1 without full JWT setup on client side, passing email might be needed
            // Assuming Basic Auth or Header populates Principal with email
            String email = (authentication != null) ? authentication.getName() : "farmer@example.com";
            if (authentication == null) {
                // Fallback for testing/dev if no auth header passed
                return ResponseEntity.status(401).body("Not authenticated");
            }
            return ResponseEntity.ok(cropService.addCrop(cropDto, authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CropDto>> searchCrops(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(cropService.searchCrops(query));
    }
}
