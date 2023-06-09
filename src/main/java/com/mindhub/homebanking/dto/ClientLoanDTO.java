package com.mindhub.homebanking.dto;

import com.mindhub.homebanking.models.ClientLoan;


public class ClientLoanDTO {

    private long id;
    private long loanId;
    private String name;
    private double amount;
    private int payments;

    private double finalAmount;
    private double interest;



    public ClientLoanDTO(ClientLoan clientLoan) {

        this.id = clientLoan.getId();

        this.loanId = clientLoan.getLoan().getId();

        this.name = clientLoan.getLoan().getName();

        this.amount = clientLoan.getAmount();

        this.payments = clientLoan.getPayments();

        this.finalAmount = clientLoan.getFinalAmount();

        this.interest = clientLoan.getLoan().getInterest();

    }

    public long getId() {
        return id;
    }

    public long getLoanId() {
        return loanId;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public int getPayments() {
        return payments;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public double getInterest() {
        return interest;
    }
}
