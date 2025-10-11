package project.cucumber.steps;

import io.cucumber.java.Before;
import io.cucumber.java.ko.given;
import io.cucumber.java.ko.when;
import io.cucumber.java.ko.then;
import org.springframework.beans.factory.annotation.Autowired;
import project.cucumber.repository.AccountRepository;
import project.cucumber.service.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountStepDefinitions {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Exception thrownException;

    @Before
    public void setUp() {
        accountRepository.clear();
        thrownException = null;
    }

    @given("계좌번호 {string}와 소유자 {string}으로 초기 잔액 {int}원인 계좌가 있다")
    public void 계좌가_있다(String accountNumber, String owner, Integer initialBalance) {
        accountService.createAccount(accountNumber, owner, initialBalance);
    }

    @when("계좌 {string}에 {int}원을 입금하면")
    public void 계좌에_입금하면(String accountNumber, Integer amount) {
        accountService.deposit(accountNumber, amount);
    }

    @when("계좌 {string}에서 {int}원을 출금하면")
    public void 계좌에서_출금하면(String accountNumber, Integer amount) {
        accountService.withdraw(accountNumber, amount);
    }

    @when("계좌 {string}에서 {int}원을 출금하려고 하면")
    public void 계좌에서_출금하려고_하면(String accountNumber, Integer amount) {
        try {
            accountService.withdraw(accountNumber, amount);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @then("계좌 {string}의 잔액은 {int}원이어야 한다")
    public void 계좌의_잔액은_원이어야_한다(String accountNumber, Integer expectedBalance) {
        int actualBalance = accountService.getBalance(accountNumber);
        assertThat(actualBalance).isEqualTo(expectedBalance);
    }

    @then("{string}라는 예외가 발생해야 한다")
    public void 예외가_발생해야_한다(String expectedMessage) {
        assertThat(thrownException).isNotNull();
        assertThat(thrownException.getMessage()).isEqualTo(expectedMessage);
    }
}
