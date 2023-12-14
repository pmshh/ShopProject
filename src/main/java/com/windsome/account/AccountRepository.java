package com.windsome.account;

import com.windsome.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);

    Account findByUserId(String userId);

    Account findByEmail(String email);
}