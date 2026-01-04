package com.merchant.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merchant.app.document.CropDocument;
import com.merchant.app.repository.CropSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebeziumConsumerService {

    private final CropSearchRepository cropSearchRepository;
    private final ObjectMapper objectMapper;

    /**
     * Listens to the Debezium topic for the 'crops' table.
     * Topic naming convention: prefix.database.table
     * Adjust 'dbserver1' to match the Debezium configuration.
     */
    @KafkaListener(topics = "dbserver1.merchant_db.crops", groupId = "merchant-search-sync")
    public void listenToCropsChange(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            JsonNode payload = root.path("payload");

            // Debezium 'after' field contains the new state of the row
            JsonNode after = payload.path("after");
            JsonNode op = payload.path("op");

            // 'c' = create, 'u' = update, 'r' = read (snapshot)
            if (!after.isMissingNode() && !after.isNull()) {
                CropDocument doc = new CropDocument();
                doc.setId(after.get("id").asText());
                doc.setName(after.get("name").asText());
                doc.setPricePerKg(new BigDecimal(after.get("price_per_kg").asText()));
                doc.setQuantityAvailable(after.get("quantity_available").asDouble());
                doc.setFarmerId(after.get("farmer_id").asLong());

                // Note: farmerName is not in the 'crops' table, so it won't be in the CDC
                // event.
                // In a real app, we might need a join or fetch user here.
                // For performance, we'll index what we have or do a DB lookup.
                // Let's populate generic or skip for now.
                doc.setFarmerName("Farmer " + doc.getFarmerId()); // Placeholder

                cropSearchRepository.save(doc);
                log.info("Synced crop to ES: {}", doc);
            } else if (op.asText().equals("d")) {
                // Delete
                JsonNode before = payload.path("before");
                if (!before.isMissingNode()) {
                    String id = before.get("id").asText();
                    cropSearchRepository.deleteById(id);
                    log.info("Removed crop from ES: {}", id);
                }
            }

        } catch (Exception e) {
            log.error("Failed to process CDC event", e);
        }
    }
}
