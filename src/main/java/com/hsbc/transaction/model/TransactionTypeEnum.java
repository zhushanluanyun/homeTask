package com.hsbc.transaction.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

/**
 * 交易种类枚举
 */
@Getter
public enum TransactionTypeEnum {
    DEPOSIT(1, "存款") {
        @Override
        public boolean isValidAmount(BigDecimal amount) {
            return amount != null && amount.signum() > 0;
        }
    },
    WITHDRAWAL(2, "提款") {
        @Override
        public boolean isValidAmount(BigDecimal amount) {
            return amount != null && amount.signum() < 0;
        }
    },
    TRANSFER(3, "转帐") {
        @Override
        public boolean isValidAmount(BigDecimal amount) {
            return amount != null && amount.signum() < 0;
        }
    },
    PAYMENT(4, "付款") {
        @Override
        public boolean isValidAmount(BigDecimal amount) {
            return amount != null && amount.signum() < 0;
        }
    };

    private final int transactionTypeCode;
    private final String transactionTypeDesc;

    TransactionTypeEnum(int code, String desc) {
        this.transactionTypeCode = code;
        this.transactionTypeDesc = desc;
    }

    public abstract boolean isValidAmount(BigDecimal amount);

    public static Optional<TransactionTypeEnum> fromCode(int code) {
        return Arrays.stream(values())
                .filter(type -> type.transactionTypeCode == code)
                .findFirst();
    }
}
