package com.demo.upi_offline_mesh.service;

import com.demo.upi_offline_mesh.dto.request.InitiatePaymentRequest;
import com.demo.upi_offline_mesh.dto.response.PaymentResponse;
import com.demo.upi_offline_mesh.entity.Account;
import com.demo.upi_offline_mesh.entity.PaymentInstruction;
import com.demo.upi_offline_mesh.enums.AccountStatus;
import com.demo.upi_offline_mesh.enums.PaymentStatus;
import com.demo.upi_offline_mesh.repository.AccountRepository;
import com.demo.upi_offline_mesh.repository.PaymentInstructionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final AccountRepository accountRepository;
    private final PaymentInstructionRepository paymentRepository;

    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request)  {

        // Step 1: Prevent self-transfers
        if (request.senderAccountId().equals(request.receiverAccountId())) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sender and Receiver cannot be same");
        }

        // Step 2: Load sender
        Account sender = accountRepository
                .findById(request.senderAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sender account not found"));

        // Step 3: Load receiver
        Account receiver = accountRepository
                .findById(request.receiverAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Receiver account not found"));

        // Step 4: Validate account statuses
        if (sender.getStatus() != AccountStatus.ACTIVE || receiver.getStatus() != AccountStatus.ACTIVE) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Both accounts must be active");
        }

        // Step 5: Create Instruction
        Instant now = Instant.now();

        PaymentInstruction payment = new PaymentInstruction();

        payment.setInstructionId(UUID.randomUUID());
        payment.setSender(sender);
        payment.setReceiver(receiver);
        payment.setAmount(request.amount());
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreatedAt(now);
        payment.setExpiresAt(now.plus(15, ChronoUnit.MINUTES));

        // Step 6: Persist
        PaymentInstruction saved = paymentRepository.save(payment);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID instructionId) {

        PaymentInstruction payment = paymentRepository
                .findByInstructionId(instructionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment instruction not found"
                ));

        return toResponse(payment);
    }

    private PaymentResponse toResponse(PaymentInstruction payment) {

        return new PaymentResponse(
                payment.getInstructionId(),
                payment.getSender().getId(),
                payment.getReceiver().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getExpiresAt()
        );
    }

}
