package com.hsbc.transaction.controller;

import com.hsbc.transaction.dto.req.TransactionReqDto;
import com.hsbc.transaction.dto.req.TransactionSearchReqDto;
import com.hsbc.transaction.dto.resp.Page;
import com.hsbc.transaction.dto.resp.TransactionRespDto;
import com.hsbc.transaction.model.TransactionTypeEnum;
import com.hsbc.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTransaction_Success() {
        TransactionReqDto transactionReqDto = new TransactionReqDto();
        transactionReqDto.setAmount(BigDecimal.valueOf(10));
        transactionReqDto.setTypeCode(1);
        doNothing().when(transactionService).createTransaction(transactionReqDto);

        ResponseEntity<Void> response = transactionController.createTransaction(transactionReqDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(transactionService, times(1)).createTransaction(transactionReqDto);
    }

    @Test
    void testCreateTransaction_InvalidTypeCode() {
        TransactionReqDto transactionReqDto = new TransactionReqDto();
        transactionReqDto.setTypeCode(99);
        transactionReqDto.setAmount(BigDecimal.valueOf(100.0));
        ResponseEntity<Void> response = transactionController.createTransaction(transactionReqDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(transactionService, never()).createTransaction(transactionReqDto);
    }

    @Test
    void testCreateTransaction_Exception() {
        TransactionReqDto transactionReqDto = new TransactionReqDto();
        transactionReqDto.setTypeCode(1);
        transactionReqDto.setAmount(BigDecimal.valueOf(-100.0));

        ResponseEntity<Void> response = transactionController.createTransaction(transactionReqDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(transactionService, never()).createTransaction(transactionReqDto);
    }

    @Test
    void testGetTransaction_Found() {
        String id = "1";
        TransactionRespDto transactionRespDto = new TransactionRespDto();
        when(transactionService.getTransaction(id)).thenReturn(transactionRespDto);

        ResponseEntity<TransactionRespDto> response = transactionController.getTransaction(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactionRespDto, response.getBody());
        verify(transactionService, times(1)).getTransaction(id);
    }

    @Test
    void testGetTransaction_NotFound() {
        String id = "1";
        when(transactionService.getTransaction(id)).thenReturn(null);

        ResponseEntity<TransactionRespDto> response = transactionController.getTransaction(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transactionService, times(1)).getTransaction(id);
    }

    @Test
    void testSearchTransactions_ContentFound() {
        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        Page<TransactionRespDto> transactionPage = new Page<>();
        transactionPage.setContent(Collections.singletonList(new TransactionRespDto()));
        when(transactionService.getTransactionsByPage(reqDto)).thenReturn(transactionPage);

        ResponseEntity<Page<TransactionRespDto>> response = transactionController.searchTransactions(reqDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactionPage, response.getBody());
        verify(transactionService, times(1)).getTransactionsByPage(reqDto);
    }

    @Test
    void testSearchTransactions_NoContent() {
        TransactionSearchReqDto reqDto = new TransactionSearchReqDto();
        Page<TransactionRespDto> transactionPage = new Page<>();
        transactionPage.setContent(Collections.emptyList());
        when(transactionService.getTransactionsByPage(reqDto)).thenReturn(transactionPage);

        ResponseEntity<Page<TransactionRespDto>> response = transactionController.searchTransactions(reqDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService, times(1)).getTransactionsByPage(reqDto);
    }

    @Test
    void testUpdateTransaction_Success() {
        String id = "1";
        TransactionReqDto transaction = new TransactionReqDto();
        doNothing().when(transactionService).updateTransaction(id, transaction);

        ResponseEntity<Void> response = transactionController.updateTransaction(id, transaction);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(id, transaction);
    }

    @Test
    void testUpdateTransaction_IllegalArgumentException() {
        String id = "1";
        TransactionReqDto transaction = new TransactionReqDto();
        doThrow(IllegalArgumentException.class).when(transactionService).updateTransaction(id, transaction);

        ResponseEntity<Void> response = transactionController.updateTransaction(id, transaction);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(id, transaction);
    }

    @Test
    void testUpdateTransaction_Exception() {
        String id = "1";
        TransactionReqDto transaction = new TransactionReqDto();
        doThrow(RuntimeException.class).when(transactionService).updateTransaction(id, transaction);

        ResponseEntity<Void> response = transactionController.updateTransaction(id, transaction);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(transactionService, times(1)).updateTransaction(id, transaction);
    }

    @Test
    void testDeleteTransaction_Success() {
        String id = "1";
        doNothing().when(transactionService).deleteTransaction(id);

        ResponseEntity<HttpStatus> response = transactionController.deleteTransaction(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(transactionService, times(1)).deleteTransaction(id);
    }

    @Test
    void testDeleteTransaction_IllegalArgumentException() {
        String id = "1";
        doThrow(IllegalArgumentException.class).when(transactionService).deleteTransaction(id);

        ResponseEntity<HttpStatus> response = transactionController.deleteTransaction(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(transactionService, times(1)).deleteTransaction(id);
    }

    @Test
    void testDeleteTransaction_Exception() {
        String id = "1";
        doThrow(RuntimeException.class).when(transactionService).deleteTransaction(id);

        ResponseEntity<HttpStatus> response = transactionController.deleteTransaction(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(transactionService, times(1)).deleteTransaction(id);
    }
}