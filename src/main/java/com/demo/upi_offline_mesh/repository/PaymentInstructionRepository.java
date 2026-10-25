package com.demo.upi_offline_mesh.repository;

import com.demo.upi_offline_mesh.entity.PaymentInstruction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentInstructionRepository extends JpaRepository<PaymentInstruction, Long> {

    Optional<PaymentInstruction> findByInstructionId(UUID instructionId);

}
