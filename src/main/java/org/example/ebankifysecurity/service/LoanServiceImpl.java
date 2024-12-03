
package org.example.ebankifysecurity.service;

import org.example.ebankifysecurity.dto.LoanDTO;
import org.example.ebankifysecurity.model.Loan;
import org.example.ebankifysecurity.model.LoanStatusEnum;
import org.example.ebankifysecurity.repository.LoanRepository;
import org.example.ebankifysecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LoanDTO applyForLoan(LoanDTO loanDTO) {
        if (!checkEligibility(loanDTO)) {
            throw new RuntimeException("User is not eligible for the loan.");
        }

        // Map LoanDTO to Loan entity
        Loan loan = new Loan();
        loan.setPrincipal(loanDTO.getPrincipal());
        loan.setInterestRate(loanDTO.getInterestRate());
        loan.setTermMonths(loanDTO.getTermMonths());
        loan.setUser(userRepository.findById(loanDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        loan.setStatus(LoanStatusEnum.PENDING);

        Loan savedLoan = loanRepository.save(loan);
        loanDTO.setId(savedLoan.getId());
        return loanDTO;
    }

    @Override
    public boolean checkEligibility(LoanDTO loanDTO) {
        if (loanDTO.getUserAge() < 18) return false;
        if (loanDTO.getMonthlyIncome() < 1000) return false;
        if (!loanDTO.isCreditHistoryGood()) return false;
        if (loanDTO.getExistingDebt() / loanDTO.getTotalIncome() > 0.4) return false;
        if (loanDTO.getPrincipal() > 5000 && !loanDTO.isHasCollateral()) return false;
        if (loanDTO.getMonthsWithBank() < 6) return false;
        return true;
    }

    @Override
    public Double calculateEMI(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        double monthlyRate = loan.getInterestRate() / 100 / 12;
        double numerator = loan.getPrincipal() * monthlyRate * Math.pow(1 + monthlyRate, loan.getTermMonths());
        double denominator = Math.pow(1 + monthlyRate, loan.getTermMonths()) - 1;

        return numerator / denominator;
    }
    @Override
    public void updateLoanStatus(Long loanId, LoanStatusEnum status) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));
        loan.setStatus(status);
        loanRepository.save(loan);
    }
}
