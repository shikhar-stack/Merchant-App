package com.merchant.app.controller;

import com.merchant.app.document.CropDocument;
import com.merchant.app.dto.CropDto;
import com.merchant.app.repository.CropSearchRepository;
import com.merchant.app.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CropController {

    private final CropService cropService;
    private final CropSearchRepository cropSearchRepository;

    @PostMapping
    public ResponseEntity<?> addCrop(@RequestBody CropDto cropDto, Authentication authentication) {
        try {
            // In a real app, use authentication.getName() (email)
            // For v1 without full JWT setup on client side, passing email might be needed
            // or basic auth
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
    public ResponseEntity<List<CropDocument>> searchCrops(@RequestParam String query) {
        if (query == null || query.isEmpty()) {
            // Return all from ES
            return ResponseEntity.ok(StreamSupport.stream(cropSearchRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.ok(cropSearchRepository.findByNameContaining(query));
    }
}
