package com.example.myhealthlife.domain.common;

import com.example.myhealthlife.domain.common.register.DomainError;

public interface ErrorMapper {
    DomainError map(Throwable throwable);
    DomainError map(int httpCode, String backendCode);
}
