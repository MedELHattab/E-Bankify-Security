package org.example.ebankifysecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "principal", nullable = false)
    private Double principal;

    @Column(name = "interest_rate", nullable = false)
    private Double interestRate;

    @Column(name = "term_months", nullable = false)
    private int termMonths;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatusEnum status;

    public boolean applyForLoan() {
        // Loan application logic
        return true; // Placeholder
    }

    public Double calculateEMI() {
        // EMI calculation logic
        return principal * (interestRate / 100 / 12); // Simplified formula for demonstration
    }
}
