package com.example.myhealthlife.data.remote.mapper;

import com.example.myhealthlife.domain.common.ErrorMapper;
import com.example.myhealthlife.domain.common.register.DomainError;

import java.io.IOException;

public class RemoteErrorMapper implements ErrorMapper {

    @Override
    public DomainError map(Throwable throwable) {

        if (throwable instanceof IOException) {
            return DomainError.NETWORK_ERROR;
        }

        return DomainError.UNKNOWN;
    }

    @Override
    public DomainError map(int httpCode, String backendCode) {

        if (httpCode == 409) {
            return DomainError.USER_ALREADY_EXISTS;
        }

        if (httpCode == 400) {
            return DomainError.USER_ALREADY_EXISTS;
        }

        if (httpCode >= 500) {
            return DomainError.SERVER_ERROR;
        }

        return DomainError.UNKNOWN_ERROR;
    }
}
