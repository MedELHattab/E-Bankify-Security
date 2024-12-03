
package org.example.ebankifysecurity.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ebankifysecurity.model.LoanStatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;

    @NotNull(message = "Principal amount is required.")
    @Min(value = 1000, message = "Principal must be at least 1000.")
    private Double principal;

    @NotNull(message = "Interest rate is required.")
    private Double interestRate;

    @Min(value = 1, message = "Loan term must be at least 1 month.")
    private int termMonths;

    @NotNull(message = "User ID is required.")
    private Long userId;

    private LoanStatusEnum status;

    // Eligibility checks
    @Min(value = 18, message = "User must be at least 18 years old.")
    private int userAge;

    @Min(value = 1000, message = "User must have a minimum monthly income of 1000.")
    private Double monthlyIncome;

    private Double existingDebt;
    private Double totalIncome;
    private boolean creditHistoryGood;
    private boolean hasCollateral;
    private int monthsWithBank;
}
