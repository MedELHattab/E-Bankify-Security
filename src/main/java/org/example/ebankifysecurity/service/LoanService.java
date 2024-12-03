package org.example.ebankifysecurity.service;

import jakarta.validation.Valid;
import org.example.ebankifysecurity.dto.LoanDTO;
import org.example.ebankifysecurity.model.LoanStatusEnum;

public interface LoanService {
    LoanDTO applyForLoan(@Valid LoanDTO loanDTO);
    boolean checkEligibility(LoanDTO loanDTO);
    Double calculateEMI(Long loanId);
    void updateLoanStatus(Long loanId, LoanStatusEnum status);
}
