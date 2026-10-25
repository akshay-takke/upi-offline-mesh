package com.demo.upi_offline_mesh.entity;

import com.demo.upi_offline_mesh.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 30)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false, length = 100)
    private String accountHolderName;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
