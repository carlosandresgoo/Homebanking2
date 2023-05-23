//package com.mindhub.homebanking;
//
//import com.mindhub.homebanking.models.*;
//import com.mindhub.homebanking.repositories.*;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
//
////@DataJpaTest
//@SpringBootTest
//@AutoConfigureTestDatabase(replace = NONE)
//public class RepositoriesTest {

//    @Autowired
//    ClientRepository clientRespository;
//    @Autowired
//    AccountRepository accountRepository;
//    @Autowired
//    TransactionRepository transactionRepository;
//    @Autowired
//    CardRepository cardRepository;
//    @Autowired
//    LoanRepository loanRepository;
//
//    //  Repositorio de client
//    @Test
//    public void existClient(){
//        List<Client> clients = clientRespository.findAll();
//        assertThat(clients, hasItem(hasProperty("password",is(not(empty())))));}
//
//    @Test
//    public void existClientPassword(){
//        List<Client> clients = clientRespository.findAll();
//        assertThat(clients, hasItem(hasProperty("password",isA(String.class))));}
//
//    //  Repositorio de cuentas
//    @Test
//    public void existAccountBalance(){
//        List<Account> accounts = accountRepository.findAll();
//        assertThat(accounts,hasItem(hasProperty("balance",is(greaterThanOrEqualTo(0.0)))));}
//
//    @Test
//    public void existAccountBalanceDouble(){
//        List<Account> accounts = accountRepository.findAll();
//        assertThat(accounts,hasItem(hasProperty("balance",isA(double.class))));}
//
//    @Test
//    public void existAccountWithoutClient(){
//        List<Account> accounts = accountRepository.findAll();
//        assertThat(accounts,hasItem(hasProperty("client",is(not(empty())))));}
//
//    //  Repositorio de transaccions
//    @Test
//    public void DebitTransaction(){
//        List<Transaction> transactionsDebit = transactionRepository.findAll().stream().filter( transaction -> transaction.getType() ==  TransactionType.DEBIT).collect(Collectors.toList());
//        assertThat(transactionsDebit,hasItem(hasProperty("amount",is(greaterThanOrEqualTo(0.0)))));}
//
//    @Test
//    public void existTransactionWithoutAccount(){
//        List<Transaction> transactions = transactionRepository.findAll();
//        assertThat(transactions,hasItem(hasProperty("account",is(not(empty())))));}
//
//    //  Repositorio de cards
//    @Test
//    public void existCardWithoutClient(){
//        List<Card> cards = cardRepository.findAll();
//        assertThat(cards,hasItem(hasProperty("client",is(not(empty())))));}
//
//    @Test
//    public void existCardWithIncorrectDate(){
//        List<Card> cards = cardRepository.findAll();
//        assertThat(cards,hasItem(hasProperty("fromDate",is(not(equalTo("thruDate"))))));}
//
//
//
////    Loan test
//
//    @Test
//    public void existLoans(){
//
//        List<Loan> loans = loanRepository.findAll();
//
//        assertThat(loans,is(not(empty())));
//
//    }
//
//    @Test
//
//    public void existPersonalLoan(){
//
//        List<Loan> loans = loanRepository.findAll();
//
//        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));
//
//    }
//}
