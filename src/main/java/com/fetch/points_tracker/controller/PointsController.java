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

    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addPoints(@RequestBody Map<String, Object> request) {
        String payer = (String) request.get("payer");
        int points = (int) request.get("points");
        ZonedDateTime timestamp = ZonedDateTime.parse((String) request.get("timestamp")); // ZonedDateTime

        pointsService.addPoints(new Transaction(payer, points, timestamp));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/spend")
    public ResponseEntity<List<Map<String, Object>>> spendPoints(@RequestBody Map<String, Integer> request) {
        int points = request.get("points");
        List<Map<String, Object>> spent = pointsService.spendPoints(points);
        return ResponseEntity.ok(spent);
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Integer>> getBalances() {
        return ResponseEntity.ok(pointsService.getBalances());
    }
}
