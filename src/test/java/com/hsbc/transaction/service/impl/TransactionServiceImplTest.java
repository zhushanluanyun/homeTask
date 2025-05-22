package com.hsbc.transaction.service.impl;

import com.hsbc.transaction.dto.req.TransactionReqDto;
import com.hsbc.transaction.dto.req.TransactionSearchReqDto;
import com.hsbc.transaction.dto.resp.Page;
import com.hsbc.transaction.dto.resp.TransactionRespDto;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static com.hsbc.transaction.config.CacheConfig.CACHE_TRANSACTIONS_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTransaction_Duplicate() {
        TransactionReqDto transactionReqDto = new TransactionReqDto();
        Page<TransactionRespDto> nonEmptyPage = new Page<>();
        nonEmptyPage.setContent(Collections.singletonList(new TransactionRespDto()));

        when(transactionRepository.listTransactions(any(TransactionSearchReqDto.class))).thenReturn(nonEmptyPage);

        assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(transactionReqDto);
        });

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testUpdateTransaction_NonExistingTransaction() {
        String id = "123";
        TransactionReqDto reqDto = new TransactionReqDto();
        reqDto.setAmount(BigDecimal.TEN);

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.updateTransaction(id, reqDto));
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testDeleteTransaction_ExistingTransaction() {
        String id = "123";
        when(transactionRepository.findById(id)).thenReturn(Optional.of(new Transaction()));
        doNothing().when(transactionRepository).deleteById(id);

        assertDoesNotThrow(() -> transactionService.deleteTransaction(id));
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteTransaction_NonExistingTransaction() {
        String id = "123";
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transactionService.deleteTransaction(id));
        verify(transactionRepository, times(1)).findById(id);
        verify(transactionRepository, never()).deleteById(id);
    }

    @Test
    void testGetTransaction_ExistingTransaction() {
        String id = "123";
        Transaction transaction = new Transaction();
        transaction.setId(id);
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        TransactionRespDto respDto = transactionService.getTransaction(id);
        assertNotNull(respDto);
        verify(transactionRepository, times(1)).findById(id);
    }

    @Test
    void testGetTransaction_NonExistingTransaction() {
        String id = "123";
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        TransactionRespDto respDto = transactionService.getTransaction(id);
        assertNull(respDto);
        verify(transactionRepository, times(1)).findById(id);
    }

    @Test
    void testGetTransactionsByPage_Cached() {
        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        Page<TransactionRespDto> cachedPage = new Page<>();
        when(cacheManager.getCache(CACHE_TRANSACTIONS_NAME)).thenReturn(cache);
        when(cache.get(CACHE_KEY, Page.class)).thenReturn(cachedPage);

        Page<TransactionRespDto> result = transactionService.getTransactionsByPage(reqDto);
        assertEquals(cachedPage, result);
        verify(cacheManager, times(1)).getCache(CACHE_TRANSACTIONS_NAME);
        verify(cache, times(1)).get(CACHE_KEY, Page.class);
        verify(transactionRepository, never()).listTransactions(reqDto);
    }
    private static final String CACHE_KEY = "OnePageSize10";
    @Test
    void testGetTransactionsByPage_NotCached() {
        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        Page<TransactionRespDto> page = new Page<>();
        when(cacheManager.getCache(CACHE_TRANSACTIONS_NAME)).thenReturn(cache);
        when(cache.get(CACHE_KEY, Page.class)).thenReturn(null);
        when(transactionRepository.listTransactions(reqDto)).thenReturn(page);

        Page<TransactionRespDto> result = transactionService.getTransactionsByPage(reqDto);
        assertEquals(page, result);
        verify(cacheManager, times(1)).getCache(CACHE_TRANSACTIONS_NAME);
        verify(cache, times(1)).get(CACHE_KEY, Page.class);
        verify(cache, times(1)).put(CACHE_KEY, page);
        verify(transactionRepository, times(1)).listTransactions(reqDto);
    }

    @Test
    void testGetTransactionsByPage_WithCriteria() {
        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        reqDto.setAccountId("ACCOUNT_ID");
        Page<TransactionRespDto> page = new Page<>();
        when(transactionRepository.listTransactions(reqDto)).thenReturn(page);

        Page<TransactionRespDto> result = transactionService.getTransactionsByPage(reqDto);
        assertEquals(page, result);
        verify(cacheManager, never()).getCache(CACHE_TRANSACTIONS_NAME);
        verify(cache, never()).get(CACHE_KEY, Page.class);
        verify(transactionRepository, times(1)).listTransactions(reqDto);
    }
}