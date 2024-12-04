package com.fetch.points_tracker.controller;

import com.fetch.points_tracker.model.Transaction;
import com.fetch.points_tracker.service.PointsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class PointsController {

    private final PointsService pointsService;

    /**
     * Constructor for PointsController.
     *
     * @param pointsService Service layer for handling business logic.
     */
    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    /**
     * Adds points for a specific payer.
     *
     * @param request The transaction details including payer, points, and timestamp.
     * @return Response entity indicating success.
     */
    @PostMapping("/add")
    public ResponseEntity<Void> addPoints(@RequestBody Map<String, Object> request) {
        String payer = (String) request.get("payer");
        int points = (int) request.get("points");
        ZonedDateTime timestamp = ZonedDateTime.parse((String) request.get("timestamp")); // Parsing timestamp from request

        pointsService.addPoints(new Transaction(payer, points, timestamp));
        return ResponseEntity.ok().build();
    }

    /**
     * Spends points across multiple payers based on a FIFO approach.
     *
     * @param request A map containing the points to be spent.
     * @return A list of maps showing how points were deducted from each payer.
     */
    @PostMapping("/spend")
    public ResponseEntity<List<Map<String, Object>>> spendPoints(@RequestBody Map<String, Integer> request) {
        int points = request.get("points");
        List<Map<String, Object>> spent = pointsService.spendPoints(points);
        return ResponseEntity.ok(spent);
    }

    /**
     * Gets the current balances for each payer.
     *
     * @return A map of payer names and their respective points balance.
     */
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Integer>> getBalances() {
        return ResponseEntity.ok(pointsService.getBalances());
    }
}
