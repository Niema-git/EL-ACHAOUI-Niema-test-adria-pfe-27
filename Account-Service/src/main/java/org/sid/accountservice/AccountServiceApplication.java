package org.sid.accountservice;

import org.sid.accountservice.domain.Account;
import org.sid.accountservice.domain.AccountStatus;
import org.sid.accountservice.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }



    @Bean
    CommandLineRunner seed(AccountRepository repository) {
        return args -> {
            repository.save(Account.builder().customerReference("CUST-001").currency("MAD").balance(15000).status(AccountStatus.ACTIVE).build());
            repository.save(Account.builder().customerReference("CUST-001").currency("EUR").balance(2500).status(AccountStatus.ACTIVE).build());
            repository.save(Account.builder().customerReference("CUST-002").currency("MAD").balance(8000).status(AccountStatus.ACTIVE).build());
            repository.save(Account.builder().customerReference("CUST-003").currency("MAD").balance(500).status(AccountStatus.BLOCKED).build());
            repository.findAll().forEach(a -> System.out.println(a.getId() + " | " + a.getCustomerReference()
                    + " | " + a.getStatus() + " | " + a.getBalance() + " " + a.getCurrency()));
        };
    }

}
