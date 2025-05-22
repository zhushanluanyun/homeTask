package com.hsbc.transaction.service;

import com.hsbc.transaction.dto.req.TransactionReqDto;
import com.hsbc.transaction.dto.req.TransactionSearchReqDto;
import com.hsbc.transaction.dto.resp.Page;
import com.hsbc.transaction.dto.resp.TransactionRespDto;

/**
 * 交易service
 */
public interface TransactionService {
    void  createTransaction(TransactionReqDto transaction);

    void updateTransaction(String id, TransactionReqDto transaction);

    void deleteTransaction(String id);

    TransactionRespDto getTransaction(String id);

    Page<TransactionRespDto> getTransactionsByPage(TransactionSearchReqDto transaction);
}