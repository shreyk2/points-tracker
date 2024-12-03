package com.fetch.points_tracker.repository;

import com.fetch.points_tracker.model.Transaction;

import java.util.List;

public interface PointsRepository {
    void saveTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    void deleteTransactionsByZeroPoints();

    int getBalanceByPayer(String payer);

    void updateBalance(String payer, int points);

    int getBalance();

    void initializePayer(String payer);
}
