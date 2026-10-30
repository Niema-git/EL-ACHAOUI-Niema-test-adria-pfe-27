package org.sid.transactionservice.model;

import lombok.*;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    private UUID id;
    private String customerReference;
    private String status;
    private String currency;
    private double balance;
}