package com.hsbc.transaction.repository;

import com.hsbc.transaction.dto.req.TransactionReqDto;
import com.hsbc.transaction.dto.req.TransactionSearchReqDto;
import com.hsbc.transaction.dto.resp.Page;
import com.hsbc.transaction.dto.resp.TransactionRespDto;
import com.hsbc.transaction.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest {

    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = new TransactionRepository();
    }

    @Test
    void testSaveTransaction() {
        Transaction transaction = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.TEN)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction")
                .build();

        transactionRepository.save(transaction);

        assertNotNull(transaction.getId());
        Optional<Transaction> savedTransaction = transactionRepository.findById(transaction.getId());
        assertTrue(savedTransaction.isPresent());
        assertEquals(transaction.getId(), savedTransaction.get().getId());
    }

    @Test
    void testFindTransactionById() {
        Transaction transaction = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.TEN)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction")
                .build();
        transactionRepository.save(transaction);

        Optional<Transaction> foundTransaction = transactionRepository.findById(transaction.getId());
        assertTrue(foundTransaction.isPresent());
        assertEquals(transaction.getId(), foundTransaction.get().getId());
    }

    @Test
    void testDeleteTransactionById() {
        Transaction transaction = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.TEN)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction")
                .build();
        transactionRepository.save(transaction);

        transactionRepository.deleteById(transaction.getId());
        Optional<Transaction> deletedTransaction = transactionRepository.findById(transaction.getId());
        assertTrue(deletedTransaction.get().isDeleted());
    }

    @Test
    void testListTransactions() {
        Transaction transaction1 = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.TEN)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction 1")
                .build();
        Transaction transaction2 = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.ONE)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction 2")
                .build();
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        reqDto.setAccountId("123");
        reqDto.setPage(1);
        reqDto.setSize(10);

        Page<TransactionRespDto> page = transactionRepository.listTransactions(reqDto);
        assertNotNull(page);
        assertFalse(page.getContent().isEmpty());
    }

    @Test
    void testListTransactionsNotExsist() {
        Transaction transaction1 = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.TEN)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction 1")
                .build();
        Transaction transaction2 = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.ONE)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction 2")
                .build();
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        reqDto.setAccountId("456");
        Page<TransactionRespDto> page = transactionRepository.listTransactions(reqDto);
        assertTrue(page.getContent().isEmpty());

    }

    @Test
    void testListTransactionsPage() {
        Transaction transaction1 = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.TEN)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction 1")
                .build();
        Transaction transaction2 = Transaction.builder()
                .accountId("123")
                .amount(BigDecimal.ONE)
                .typeCode(1)
                .createTime(Instant.now())
                .description("Test transaction 2")
                .build();
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);

        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        reqDto.setPage(1);
        reqDto.setSize(1);
        Page<TransactionRespDto> page = transactionRepository.listTransactions(reqDto);
        assertEquals(1, page.getContent().size());
    }
}