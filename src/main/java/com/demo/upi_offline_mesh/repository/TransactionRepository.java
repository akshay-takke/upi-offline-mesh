package com.demo.upi_offline_mesh.repository;

import com.demo.upi_offline_mesh.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(UUID transactionId);

    boolean existsByPaymentInstructionId(Long paymentInstructionId);
}
