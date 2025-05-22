package com.hsbc.transaction.dto.resp;

import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.model.TransactionTypeEnum;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRespDtoTest {

    @Test
    void testConvertToRespDto() {
        // 创建一个 Transaction 对象
        Transaction transaction = new Transaction();
        transaction.setId("123");
        transaction.setAccountId("456");
        transaction.setAmount(BigDecimal.TEN);
        transaction.setTypeCode(TransactionTypeEnum.DEPOSIT.getTransactionTypeCode());
        transaction.setUpdateTime(Instant.now());
        transaction.setCreateTime(Instant.now());
        transaction.setUpdateAccount("user1");
        transaction.setDescription("Test transaction");

        // 调用 convertToRespDto 方法进行转换
        TransactionRespDto respDto = TransactionRespDto.convertToRespDto(transaction);

        // 验证转换后的属性值
        assertEquals(transaction.getId(), respDto.getId());
        assertEquals(transaction.getAccountId(), respDto.getAccountId());
        assertEquals(transaction.getAmount(), respDto.getAmount());
        assertEquals(TransactionTypeEnum.DEPOSIT.getTransactionTypeDesc(), respDto.getTransactionType());
        assertEquals(transaction.getUpdateTime(), respDto.getUpdateTime());
        assertEquals(transaction.getCreateTime(), respDto.getCreateTime());
        assertEquals(transaction.getUpdateAccount(), respDto.getUpdateAccount());
        assertEquals(transaction.getDescription(), respDto.getDescription());
    }

    @Test
    void testConvertToRespDtoWithNullTypeCode() {
        // 创建一个 Transaction 对象，typeCode 为 null
        Transaction transaction = new Transaction();
        transaction.setId("123");
        transaction.setAccountId("456");
        transaction.setAmount(BigDecimal.TEN);
        transaction.setTypeCode(null);
        transaction.setUpdateTime(Instant.now());
        transaction.setCreateTime(Instant.now());
        transaction.setUpdateAccount("user1");
        transaction.setDescription("Test transaction");

        // 调用 convertToRespDto 方法进行转换
        TransactionRespDto respDto = TransactionRespDto.convertToRespDto(transaction);

        // 验证 transactionType 为 null
        assertNull(respDto.getTransactionType());
    }
}