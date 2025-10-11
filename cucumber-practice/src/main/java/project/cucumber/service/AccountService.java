package project.cucumber.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.cucumber.domain.Account;
import project.cucumber.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account createAccount(String accountNumber, String owner, int initialBalance) {
        Account account = new Account(accountNumber, owner, initialBalance);
        accountRepository.save(account);
        return account;
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));
    }

    public void deposit(String accountNumber, int amount) {
        Account account = getAccount(accountNumber);
        account.deposit(amount);
    }

    public void withdraw(String accountNumber, int amount) {
        Account account = getAccount(accountNumber);
        account.withdraw(amount);
    }

    public int getBalance(String accountNumber) {
        return getAccount(accountNumber).getBalance();
    }
}
