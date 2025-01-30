package com.hilamiks.messages.dto;

public record AccountsMsgDto(
    Long accountNumber,
    String name,
    String email,
    String mobileNumber
) {
}
