package org.example.ebankifysecurity.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ebankifysecurity.model.TransactionStatusEnum;
import org.example.ebankifysecurity.model.TransactionTypeEnum;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private Long id;

    @NotNull(message = "Source account ID is required.")
    @Positive(message = "Source account ID must be positive.")
    private Long sourceAccountId;

    @NotNull(message = "Destination account ID is required.")
    @Positive(message = "Destination account ID must be positive.")
    private Long destinationAccountId;

    @NotNull(message = "Amount is required.")
    @Min(value = 1, message = "Amount must be greater than 0.")
    @Digits(integer = 10, fraction = 2, message = "Amount must be a valid number with up to two decimal places.")
    private Double amount;

    @NotNull(message = "Transaction type is required.")
    private TransactionTypeEnum type; // CLASSIC, INSTANT, PERMANENT

    @NotNull(message = "Transaction date is required.")
    private LocalDateTime transactionDate;

    private TransactionStatusEnum status; // PENDING, APPROVED, REJECTED

    @NotNull(message = "Description is required.")
    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;  // Optional: Reason for the transaction

    private Boolean isInterBank;
}
