package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    Optional<PaymentTransaction> findByTxnRef(String txnRef);

    Optional<PaymentTransaction> findByOrderId(String orderId);
}
