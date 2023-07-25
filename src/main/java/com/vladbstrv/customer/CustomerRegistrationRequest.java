package com.vladbstrv.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
