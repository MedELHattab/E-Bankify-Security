package org.example.ebankifysecurity.repository;

import org.example.ebankifysecurity.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Custom queries for transaction search
    List<Transaction> findBySourceAccountId(Long sourceAccountId);
    List<Transaction> findByDestinationAccountId(Long destinationAccountId);
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByAmountGreaterThanEqual(Double amount);

}
