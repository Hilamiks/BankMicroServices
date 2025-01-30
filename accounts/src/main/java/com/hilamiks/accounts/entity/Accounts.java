package com.hilamiks.accounts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Accounts extends BaseEntity {

    private Long customerId;
    @Id
    private Long accountNumber;
    private String accountType;
    private String branchAddress;
    private Boolean communicationSw;
}
