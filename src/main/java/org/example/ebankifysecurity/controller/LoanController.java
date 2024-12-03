package org.example.ebankifysecurity.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.ebankifysecurity.dto.LoanDTO;
import org.example.ebankifysecurity.model.LoanStatusEnum;
import org.example.ebankifysecurity.model.User;
import org.example.ebankifysecurity.model.role_enum;
import org.example.ebankifysecurity.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForLoan(@Valid @RequestBody LoanDTO loanDTO) {
        try {
            LoanDTO appliedLoan = loanService.applyForLoan(loanDTO);
            return ResponseEntity.ok("Loan applied successfully with ID: " + appliedLoan.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{loanId}/emi")
    public ResponseEntity<?> calculateEMI(@PathVariable Long loanId) {
        try {
            Double emi = loanService.calculateEMI(loanId);
            return ResponseEntity.ok(emi);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loan not found for ID: " + loanId);
        }
    }

    @PutMapping("/{loanId}/status")
    public ResponseEntity<?> updateLoanStatus(
            @PathVariable Long loanId,
            @RequestParam LoanStatusEnum status,
            HttpSession session
    )
    {

        session.getAttributeNames().asIterator().forEachRemaining(attr ->
                System.out.println("Session attribute: " + attr + " = " + session.getAttribute(attr))
        );
        // Check if user has the required role
        User authUser = (User) session.getAttribute("user");
        if (authUser.getRole() == role_enum.USER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Unauthorized role");
        }

        try {
            loanService.updateLoanStatus(loanId, status);
            return ResponseEntity.ok("Loan status updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
