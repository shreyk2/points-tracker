package com.fetch.points_tracker.service;

import com.fetch.points_tracker.exceptions.InsufficientPointsException;
import com.fetch.points_tracker.exceptions.InvalidTransactionException;
import com.fetch.points_tracker.model.Transaction;
import com.fetch.points_tracker.repository.PointsRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PointsService {

    private final PointsRepository pointsRepository;

    public PointsService(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    public void addPoints(Transaction transaction) {
        if (transaction.getTimestamp() == null || transaction.getPayer() == null || transaction.getPayer().isBlank()) {
            throw new InvalidTransactionException("Transaction must have a valid payer, timestamp, and points.");
        }
        pointsRepository.saveTransaction(transaction);
        pointsRepository.updateBalance(transaction.getPayer(), transaction.getPoints());
    }

    public List<Map<String, Object>> spendPoints(int points) {
        if (points <= 0) {
            throw new InvalidTransactionException("Points to spend must be greater than zero.");
        }
        if(points > pointsRepository.getBalance()){
            throw new InsufficientPointsException("Not enough points to complete the transaction.");
        }
        List<Transaction> allTransactions = pointsRepository.getAllTransactions();
        allTransactions.sort(Comparator.comparing(Transaction::getTimestamp)); // Sort oldest first
        List<Map<String, Object>> spentPoints = new ArrayList<>();
        int pointsToSpend = points;

        for (Transaction transaction : allTransactions) {
            if (pointsToSpend <= 0) break;

            int availablePoints = Math.min(transaction.getPoints(), pointsToSpend);

            transaction.setPoints(transaction.getPoints() - availablePoints);
            pointsRepository.updateBalance(transaction.getPayer(), -availablePoints);
            pointsToSpend -= availablePoints;
            spentPoints.add(Map.of("payer", transaction.getPayer(), "points", -availablePoints));
        }

        if (pointsToSpend > 0) {
            throw new InsufficientPointsException("Not enough points to complete the transaction.");
        }
        return spentPoints;
    }

    public Map<String, Integer> getBalances() {
        List<Transaction> allTransactions = pointsRepository.getAllTransactions();
        Map<String, Integer> balances = new HashMap<>();

        for (Transaction transaction : allTransactions) {
            String payer = transaction.getPayer();
            pointsRepository.initializePayer(payer);
            balances.put(payer, pointsRepository.getBalanceByPayer(payer));
        }

        return balances;
    }
}
