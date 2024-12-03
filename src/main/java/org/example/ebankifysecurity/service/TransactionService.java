package org.example.ebankifysecurity.service;

import jakarta.servlet.http.HttpSession;
import org.example.ebankifysecurity.dto.TransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {

    // Method to initiate a transaction
    TransactionDTO initiateTransaction(TransactionDTO transactionDTO);

    // Method to approve a transaction (for EMPLOYEE)
    ResponseEntity<String> approveTransaction(Long transactionId, HttpSession session);

    // Method to get transaction history by user
    List<TransactionDTO> getTransactionHistory(Long userId);

    // Method to validate transaction (checking balance, etc.)
    boolean validateTransaction(TransactionDTO transactionDTO);

    // Method to get transaction details by ID
    TransactionDTO getTransactionById(Long transactionId);
}
