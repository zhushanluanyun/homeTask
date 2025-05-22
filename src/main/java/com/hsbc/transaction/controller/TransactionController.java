package com.hsbc.transaction.controller;


import com.hsbc.transaction.dto.req.TransactionReqDto;
import com.hsbc.transaction.dto.req.TransactionSearchReqDto;
import com.hsbc.transaction.dto.resp.Page;
import com.hsbc.transaction.dto.resp.TransactionRespDto;
import com.hsbc.transaction.model.TransactionTypeEnum;
import com.hsbc.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 创建交易
     *
     * @param transactionReqDto 请求dto
     * @return ResponseEntity<Void>
     */
    @PostMapping
    public ResponseEntity<Void> createTransaction(@Valid @RequestBody TransactionReqDto transactionReqDto) {
        try {
            Optional<TransactionTypeEnum> transactionTypeEnum = TransactionTypeEnum.fromCode(transactionReqDto.getTypeCode());
            if (transactionTypeEnum.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            boolean validAmount = transactionTypeEnum.get().isValidAmount(transactionReqDto.getAmount());
            if (!validAmount) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            transactionService.createTransaction(transactionReqDto);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 查询单个交易
     *
     * @param id 主键
     * @return 交易响应
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionRespDto> getTransaction(@PathVariable("id") String id) {
        TransactionRespDto transactionData = transactionService.getTransaction(id);

        if (transactionData != null) {
            return new ResponseEntity<>(transactionData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 分页查询
     *
     * @param reqDto 请求
     * @return 分页列表响应
     */
    @PostMapping("/pageSearch") // 使用POST请求
    public ResponseEntity<Page<TransactionRespDto>> searchTransactions(
            @Valid @RequestBody TransactionSearchReqDto reqDto) {
        Page<TransactionRespDto> transactionPage = transactionService.getTransactionsByPage(reqDto);

        if (transactionPage.getContent().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(transactionPage, HttpStatus.OK);
    }

    /**
     * 更新操作
     *
     * @param id          主键
     * @param transaction 更新结构体
     * @return ResponseEntity<Void>
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTransaction(
            @PathVariable("id") String id, @Valid @RequestBody TransactionReqDto transaction) {
        try {
            transactionService.updateTransaction(id, transaction);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 单个删除
     *
     * @param id 主键
     * @return ResponseEntity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTransaction(@PathVariable("id") String id) {
        try {
            transactionService.deleteTransaction(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}