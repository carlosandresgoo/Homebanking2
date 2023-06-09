package com.mindhub.homebanking.Controllers;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mindhub.homebanking.dto.ClientDTO;
import com.mindhub.homebanking.dto.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class TransactionController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;


    @Transactional
    @PostMapping("/api/clients/current/transactions")
    public ResponseEntity<Object> newTransaction(
            Authentication authentication, @RequestParam double amount, @RequestParam String description,
            @RequestParam String initialAccount, @RequestParam String destinateAccount) {

        Client client = clientService.findByEmail(authentication.getName());
        Account accountAuthenticated = accountService.findByNumber(initialAccount.toUpperCase());
        Account destinateAccountAuthenticated = accountService.findByNumber(destinateAccount.toUpperCase());

        if (String.valueOf(amount) == null) {
            return new ResponseEntity<>("Please enter an amount.", HttpStatus.FORBIDDEN);
        } else if (amount < 1) {
            return new ResponseEntity<>("Please enter an amount bigger than 0.", HttpStatus.FORBIDDEN);
        } else if (!accountAuthenticated.isAccountActive()) {
            return new ResponseEntity<>("This account is not active.", HttpStatus.FORBIDDEN);
        } else if (accountAuthenticated.getBalance() < amount) {
            return new ResponseEntity<>("You don't have the founds for this transaction", HttpStatus.FORBIDDEN);
        }

        if (description.isBlank()) {
            description = "Transaction to " + destinateAccount;
        }

        if (initialAccount.isBlank()) {
            return new ResponseEntity<>("Please enter one of your accounts", HttpStatus.FORBIDDEN);
        } else if (accountAuthenticated == null) {
            return new ResponseEntity<>("This account " + initialAccount + " doesn't exist", HttpStatus.FORBIDDEN);
        } else if (client.getAccounts().stream().filter(account -> account.getNumber().equalsIgnoreCase(initialAccount)).collect(Collectors.toList()).size() == 0) {
            return new ResponseEntity<>("This account is not yours.", HttpStatus.FORBIDDEN);
        }

        if (destinateAccount.isBlank()) {
            return new ResponseEntity<>("Please enter an account that you want to transfer the money", HttpStatus.FORBIDDEN);
        } else if (destinateAccountAuthenticated == null) {
            return new ResponseEntity<>("This account " + destinateAccount + " doesn't exist", HttpStatus.FORBIDDEN);
        } else if (destinateAccount.equalsIgnoreCase(initialAccount)) {
            return new ResponseEntity<>("You can't send money to the same account number", HttpStatus.FORBIDDEN);
        } else if (!destinateAccountAuthenticated.isAccountActive()) {
            return new ResponseEntity<>("The destination account is not active.", HttpStatus.FORBIDDEN);
        }

        double initialBalance = accountAuthenticated.getBalance();
        double destinateBalance = destinateAccountAuthenticated.getBalance();

        Transaction newTransaction = new Transaction(TransactionType.DEBIT, amount, destinateAccount + " : " + description, LocalDateTime.now(), true, initialBalance);
        accountAuthenticated.addTransaction(newTransaction);
        newTransaction.setBalanceAfterTransaction(initialBalance - amount);
        transactionService.saveTransaction(newTransaction);

        Transaction newTransaction2 = new Transaction(TransactionType.CREDIT, amount, initialAccount + " : " + description, LocalDateTime.now(), true, destinateBalance);
        destinateAccountAuthenticated.addTransaction(newTransaction2);
        newTransaction2.setBalanceAfterTransaction(destinateBalance + amount);
        transactionService.saveTransaction(newTransaction2);

        accountAuthenticated.setBalance(initialBalance - amount);
        destinateAccountAuthenticated.setBalance(destinateBalance + amount);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/accounts/transactions/pdf")
    public ResponseEntity<Object> createPDF (Authentication authentication,@RequestParam Long id, @RequestParam String startDate , @RequestParam String endDate) throws DocumentException, IOException, ParseException {

        Account accountRequest = accountService.findById(id);
        ClientDTO clientAuthenticated = clientService.getClients(authentication);

        if(accountRequest == null){
            return new ResponseEntity<>("The account doesn't exist", HttpStatus.FORBIDDEN);
        }
        if (startDate.isBlank()){
            return new ResponseEntity<>("Start date can't on blank", HttpStatus.FORBIDDEN);
        }
        if(endDate.isBlank()){
            return new ResponseEntity<>("End date can't be on blank", HttpStatus.FORBIDDEN);
        }
        if(clientAuthenticated.getAccounts()
                .stream()
                .noneMatch(account -> account.getNumber().equals(accountRequest.getNumber()))){
            return new ResponseEntity<>("This account doesn't belong to you", HttpStatus.FORBIDDEN);
        }

        Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);

        LocalDateTime startLocalDateTime = Utils.dateToLocalDateTime(start);
        LocalDateTime endLocalDateTime = Utils.dateToLocalDateTime(end);

        List<TransactionDTO> transactionsDTOList = transactionService.getTransactionsByIdAndDateBetween(accountRequest, startLocalDateTime, endLocalDateTime);

        Document document = new Document();

        PdfWriter.getInstance(document, new FileOutputStream("C:\\Tareas mind hub\\Tercera parte del mind hub\\homebanking2\\Transactions-download from" + startDate + " to " + endDate + " .pdf"));
        document.open();

        Image logo = Image.getInstance("C:\\Tareas mind hub\\Tercera parte del mind hub\\homebanking2\\Homebanking2\\src\\main\\resources\\static\\web\\assets\\logo2.png");
        logo.scaleToFit(120, 120);
        document.add(logo);

        Font fontTitle = new Font(Font.FontFamily.HELVETICA, 25, Font.BOLD);
        Paragraph title = new Paragraph("Hello " + clientAuthenticated.getFirstName() +" "+ clientAuthenticated.getLastName(), fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(20);
        title.setSpacingBefore(20);
        document.add(title);

        Font fontSubTitle  = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph subTitle = new Paragraph("Here are your transactions from " + startDate + " to " + endDate + " from your account " + accountRequest.getNumber(), fontSubTitle);
        subTitle.setAlignment(Paragraph.ALIGN_CENTER);
        subTitle.setSpacingAfter(20);
        subTitle.setSpacingBefore(20);
        document.add(subTitle);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        PdfPCell cellDate = new PdfPCell(new Paragraph("Date"));
        cellDate.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellDate);

        PdfPCell cellTime = new PdfPCell(new Paragraph("Time"));
        cellTime.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellTime);

        PdfPCell cellAmount = new PdfPCell(new Paragraph("Amount"));
        cellAmount.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellAmount);

        PdfPCell cellDescription = new PdfPCell(new Paragraph("Description"));
        cellDescription.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellDescription);

        PdfPCell cellType = new PdfPCell(new Paragraph("Type"));
        cellType.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellType);

        PdfPCell cellBalance = new PdfPCell(new Paragraph("Balance"));
        cellType.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cellBalance);




        for (TransactionDTO transactionDTO: transactionsDTOList){
            String date = Utils.getStringDateFromLocalDateTime(transactionDTO.getDate());
            String hour = Utils.getStringHourFromLocalDateTime(transactionDTO.getDate());



            PdfPCell cellTransactionDate = new PdfPCell(new Paragraph(date));
            cellTransactionDate.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTransactionDate.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTransactionDate.setFixedHeight(50);
            table.addCell(cellTransactionDate);

            PdfPCell cellTransactionTime = new PdfPCell(new Paragraph(hour));
            cellTransactionTime.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTransactionTime.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTransactionTime.setFixedHeight(50);
            table.addCell(cellTransactionTime);

            PdfPCell cellTransactionAmount = new PdfPCell(new Paragraph(
                    transactionDTO.getType().name().equals("DEBIT")? "$ - " + (NumberFormat.getNumberInstance(Locale.US).format(transactionDTO.getAmount())) : "$ + "+ NumberFormat.getNumberInstance(Locale.US).format(transactionDTO.getAmount())));
            cellTransactionAmount.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTransactionAmount.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTransactionAmount.setFixedHeight(50);
            table.addCell(cellTransactionAmount);

            PdfPCell cellTransactionDescription = new PdfPCell(new Paragraph(transactionDTO.getDescription()));
            cellTransactionDescription.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTransactionDescription.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTransactionDescription.setFixedHeight(50);
            table.addCell(cellTransactionDescription);

            PdfPCell cellTransactionType = new PdfPCell(new Paragraph(transactionDTO.getType().name()));
            cellTransactionType.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTransactionType.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTransactionType.setFixedHeight(50);
            table.addCell(cellTransactionType);

            PdfPCell cellTransactionBalance = new PdfPCell(new Paragraph("$"+NumberFormat.getNumberInstance(Locale.US).format(transactionDTO.getBalanceAfterTransaction())));
            cellTransactionBalance.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTransactionBalance.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTransactionBalance.setFixedHeight(50);
            table.addCell(cellTransactionBalance);




        }
        document.add(table);

        document.close();

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}