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

    /**
     * Constructor for PointsService.
     *
     * @param pointsRepository Repository to manage transactions and balances.
     */
    public PointsService(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    /**
     * Adds a new transaction to the repository.
     *
     * @param transaction The transaction details including payer, points, and timestamp.
     * @throws InvalidTransactionException If the transaction is invalid.
     */
    public void addPoints(Transaction transaction) {
        if (transaction.getTimestamp() == null || transaction.getPayer() == null || transaction.getPayer().isBlank()) {
            throw new InvalidTransactionException("Transaction must have a valid payer, timestamp, and points.");
        }
        pointsRepository.saveTransaction(transaction);
        pointsRepository.updateBalance(transaction.getPayer(), transaction.getPoints());
    }

    /**
     * Spends points across all transactions.
     *
     * @param points The number of points to spend.
     * @return A list showing the details of each transaction used for point deduction.
     * @throws InvalidTransactionException If points are less than or equal to zero.
     * @throws InsufficientPointsException If there are not enough points to complete the transaction.
     */
    public List<Map<String, Object>> spendPoints(int points) {
        if (points <= 0) {
            throw new InvalidTransactionException("Points to spend must be greater than zero.");
        }
        if (points > pointsRepository.getBalance()) {
            throw new InsufficientPointsException("Not enough points to complete the transaction.");
        }

        List<Transaction> allTransactions = pointsRepository.getAllTransactions();
        allTransactions.sort(Comparator.comparing(Transaction::getTimestamp)); // Sort transactions from oldest to newest

        List<Map<String, Object>> spentPoints = new ArrayList<>();
        int pointsToSpend = points;

        // Iterating over transactions to deduct points
        for (Transaction transaction : allTransactions) {
            if (pointsToSpend <= 0) break;

            int availablePoints = Math.min(transaction.getPoints(), pointsToSpend);

            transaction.setPoints(transaction.getPoints() - availablePoints); // Deduct available points from transaction
            pointsRepository.updateBalance(transaction.getPayer(), -availablePoints); // Update balance in repository
            pointsToSpend -= availablePoints;

            // Adding information about spent points
            spentPoints.add(Map.of("payer", transaction.getPayer(), "points", -availablePoints));
        }

        if (pointsToSpend > 0) {
            throw new InsufficientPointsException("Not enough points to complete the transaction.");
        }
        return spentPoints;
    }

    /**
     * Gets the current balances for each payer.
     *
     * @return A map containing each payer's name and their respective balance.
     */
    public Map<String, Integer> getBalances() {
        List<Transaction> allTransactions = pointsRepository.getAllTransactions();
        Map<String, Integer> balances = new HashMap<>();

        for (Transaction transaction : allTransactions) {
            String payer = transaction.getPayer();
            pointsRepository.initializePayer(payer); // Initialize payer if not present
            balances.put(payer, pointsRepository.getBalanceByPayer(payer));
        }

        return balances;
    }
}
