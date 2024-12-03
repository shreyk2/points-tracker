package com.fetch.points_tracker.repository;

import com.fetch.points_tracker.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryPointsRepository implements PointsRepository {

    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Integer> balances = new HashMap<>();
    private int balance = 0;

    @Override
    public void saveTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions); // Return a copy to avoid external modifications
    }

    @Override
    public void deleteTransactionsByZeroPoints() {
        transactions.removeIf(transaction -> transaction.getPoints() == 0);
    }

    @Override
    public int getBalanceByPayer(String payer) {
        return balances.getOrDefault(payer, 0);
    }

    @Override
    public void updateBalance(String payer, int points) {
        balance+=points;
        balances.put(payer, balances.getOrDefault(payer, 0) + points);
    }
    @Override
    public int getBalance() {
        return balance;
    }
    @Override
    public void initializePayer(String payer) {
        balances.putIfAbsent(payer, 0);
    }
}
