package com.gcash.dao;

import com.gcash.model.Transaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionDAO {
    // This is a stub. Replace with actual DB logic as needed.
    public List<Transaction> getTransactions(int userId) {
        // Example: return dummy transactions for demonstration
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("CASH_IN", 1000.0, null, new Date()));
        transactions.add(new Transaction("TRANSFER_OUT", 500.0, "bob", new Date()));
        return transactions;
    }
}