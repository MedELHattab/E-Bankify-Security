package org.example.ebankifysecurity.service;

import jakarta.servlet.http.HttpSession;
import org.example.ebankifysecurity.dto.TransactionDTO;
import org.example.ebankifysecurity.model.*;
import org.example.ebankifysecurity.model.role_enum;
import org.example.ebankifysecurity.repository.AccountRepository;
import org.example.ebankifysecurity.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;



    private static final double CLASSIC_TRANSFER_FEE_RATE = 0.02;  // 2% fee for CLASSIC transfers
    private static final double INSTANT_TRANSFER_FEE_RATE = 0.05;  // 5% fee for INSTANT transfers

    @Override
    public TransactionDTO initiateTransaction(TransactionDTO transactionDTO) {
        // Validate the transaction
        if (!validateTransaction(transactionDTO)) {
            throw new RuntimeException("Transaction validation failed.");
        }

        // Retrieve source and destination accounts
        Account sourceAccount = accountRepository.findById(transactionDTO.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));
        Account destinationAccount = accountRepository.findById(transactionDTO.getDestinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Calculate the fee if it's an inter-bank transfer
        Boolean isInterBank = transactionDTO.getIsInterBank();
        double fee = calculateTransactionFee(transactionDTO.getType(), transactionDTO.getIsInterBank());
        double totalAmount = transactionDTO.getAmount() + fee;

        // Check if source account has enough balance
        if (sourceAccount.getBalance() < totalAmount) {
            throw new RuntimeException("Insufficient funds for this transaction.");
        }

        // Create a new transaction
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(totalAmount); // Including fee
        transaction.setType(transactionDTO.getType());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setIsInterBank(isInterBank);

        // Set the transaction status based on the type
        if (transactionDTO.getType() == TransactionTypeEnum.PERMANENT) {
            transaction.setStatus(TransactionStatusEnum.PENDING);
        } else {
            transaction.setStatus(TransactionStatusEnum.COMPLETED);
        }

        // Save the transaction to the repository
        Transaction savedTransaction = transactionRepository.save(transaction);

        double amountToDectate = totalAmount;

        sourceAccount.setBalance(sourceAccount.getBalance() - amountToDectate);

        destinationAccount.setBalance(destinationAccount.getBalance() + transactionDTO.getAmount());

        // Save the updated accounts
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);



        // Save to Elasticsearch for search capability
//        elasticsearchTransactionRepository.save(savedTransaction);

        // Update DTO with the saved transaction ID
        transactionDTO.setId(savedTransaction.getId());



        return transactionDTO;
    }

    // Calculate the fee for inter-bank transactions based on type and isInterBank flag
    private double calculateTransactionFee(TransactionTypeEnum type, boolean isInterBank) {
        if (isInterBank) {
            if (type == TransactionTypeEnum.CLASSIC) {
                return CLASSIC_TRANSFER_FEE_RATE;
            } else if (type == TransactionTypeEnum.INSTANT) {
                return INSTANT_TRANSFER_FEE_RATE;
            }
        }
        return 0.0; // No fee for intra-bank transfers
    }

    @Override
    public ResponseEntity<String> approveTransaction(Long transactionId, HttpSession session) {
        // Retrieve authenticated user from session
        User authUser = (User) session.getAttribute("user");

        if (authUser == null || authUser.getRole() != role_enum.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Unauthorized role");
        }

        // Proceed with transaction approval if user has EMPLOYEE role
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(TransactionStatusEnum.COMPLETED);
        transactionRepository.save(transaction);

        return ResponseEntity.ok("Transaction approved successfully");
    }

    @Override
    public List<TransactionDTO> getTransactionHistory(Long userId) {
        // Fetch transactions for both source and destination accounts
        List<Transaction> transactions = transactionRepository.findBySourceAccountId(userId);
        transactions.addAll(transactionRepository.findByDestinationAccountId(userId));

        // Convert the transactions to DTOs
        return transactions.stream()
                .map(transaction -> new TransactionDTO(
                        transaction.getId(),
                        transaction.getSourceAccount().getId(),
                        transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getId() : null,
                        transaction.getAmount(),
                        transaction.getType(),
                        transaction.getTransactionDate(),
                        transaction.getStatus(),
                        transaction.getDescription(),
                        transaction.getIsInterBank()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateTransaction(TransactionDTO transactionDTO) {
        Account sourceAccount = accountRepository.findById(transactionDTO.getSourceAccountId())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        // Check if the source account has enough funds for the transaction amount
        double fee = calculateTransactionFee(transactionDTO.getType(), transactionDTO.getIsInterBank());
        double totalAmount = transactionDTO.getAmount() + fee;

        return sourceAccount.getBalance() >= totalAmount;
    }


    @Override
    public TransactionDTO getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return new TransactionDTO(
                transaction.getId(),
                transaction.getSourceAccount().getId(),
                transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getId() : null,
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTransactionDate(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getIsInterBank()
        );
    }
}
