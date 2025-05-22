package com.hsbc.transaction.service.impl;

import com.hsbc.transaction.dto.req.TransactionReqDto;
import com.hsbc.transaction.dto.req.TransactionSearchReqDto;
import com.hsbc.transaction.dto.resp.Page;
import com.hsbc.transaction.dto.resp.TransactionRespDto;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static com.hsbc.transaction.config.CacheConfig.CACHE_TRANSACTIONS_NAME;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CacheManager cacheManager;

    private static final String CACHE_KEY = "OnePageSize10";

    @Override
    public void createTransaction(TransactionReqDto transactionReqDto) {
        if (isDuplicateTransaction(transactionReqDto)) {
            log.error("is Duplicate Transaction...");
            throw new RuntimeException("Duplicate Transaction");
        }
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionReqDto, transaction);
        transaction.setCreateTime(Instant.now());
        dealTransactionUpdateParam(transactionReqDto, transaction);
        transactionRepository.save(transaction);
        clearCache();
    }

    private void clearCache() {
        Cache cache = cacheManager.getCache(CACHE_TRANSACTIONS_NAME);
        if (cache != null) {
            cache.evict(CACHE_KEY);
        }
    }

    private static void dealTransactionUpdateParam(TransactionReqDto transactionReqDto, Transaction transaction) {
        transaction.setUpdateTime(Instant.now());
        transaction.setUpdateAccount(transactionReqDto.getAccountId());
    }


    /**
     * 防抖设计 1s内是否有重复提交操作
     *
     * @param transactionReqDto
     * @return
     */
    private boolean isDuplicateTransaction(TransactionReqDto transactionReqDto) {
        Instant now = Instant.now();
        Instant oneSecondAgo = now.minusSeconds(1);
        TransactionSearchReqDto transactionSearchReqDto = TransactionSearchReqDto.builder().build();
        BeanUtils.copyProperties(transactionReqDto, transactionSearchReqDto);
        transactionSearchReqDto.setStartCreateTime(oneSecondAgo);
        Page<TransactionRespDto> listTransactions = transactionRepository.listTransactions(transactionSearchReqDto);
        return !listTransactions.getContent().isEmpty();
    }


    @Override
    public void updateTransaction(String id, TransactionReqDto transaction) {
        Optional<Transaction> existingTransaction = transactionRepository.findById(id);

        if (existingTransaction.isEmpty()) {
            throw new IllegalArgumentException("找不到ID为 " + id + " 的交易记录");
        }

        Transaction updatedTransaction = existingTransaction.get();
        if (updatedTransaction.isDeleted()) {
            throw new IllegalArgumentException("找不到ID为 " + id + " 的交易记录 或者 此记录已被删除");
        }

        // 更新允许修改的字段
        if (transaction.getAmount() != null) {
            updatedTransaction.setAmount(transaction.getAmount());
        }
        if (transaction.getTypeCode() != null) {
            updatedTransaction.setTypeCode(transaction.getTypeCode());
        }
        if (transaction.getDescription() != null) {
            updatedTransaction.setDescription(transaction.getDescription());
        }
        dealTransactionUpdateParam(transaction, updatedTransaction);
        transactionRepository.save(updatedTransaction);
        clearCache();
    }

    @Override
    public void deleteTransaction(String id) {
        if (transactionRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("找不到ID为 " + id + " 的交易记录");
        }
        transactionRepository.deleteById(id);
        clearCache();
    }

    @Override
    public TransactionRespDto getTransaction(String id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            Transaction transactionEntity = transaction.get();
            if (transactionEntity.isDeleted()) {
                return null;
            }
            return TransactionRespDto.convertToRespDto(transactionEntity);
        }
        return null;
    }

    /**
     * 缓存首页 默认查询条件下的记录数 快速加载列表页面
     *
     * @param transactionReqDto 请求体
     * @return 响应列表
     */
    @Override
    public Page<TransactionRespDto> getTransactionsByPage(TransactionSearchReqDto transactionReqDto) {
        if (isRequestWithoutCriteria(transactionReqDto)) {
            Cache cache = cacheManager.getCache(CACHE_TRANSACTIONS_NAME);
            // 尝试从缓存获取
            Page<TransactionRespDto> cachedPage = null;

            cachedPage = cache.get(CACHE_KEY, Page.class);

            if (cachedPage != null) {
                return cachedPage;
            }
            // 缓存未命中，查询数据库
            Page<TransactionRespDto> page = transactionRepository.listTransactions(transactionReqDto);

            // 存入缓存
            cache.put(CACHE_KEY, page);
            return page;
        }
        return transactionRepository.listTransactions(transactionReqDto);
    }

    private boolean isRequestWithoutCriteria(TransactionSearchReqDto reqDto) {
        return reqDto.getAccountId() == null &&
                reqDto.getTypeCode() == null &&
                reqDto.getMinAmount() == null &&
                reqDto.getMaxAmount() == null &&
                reqDto.getStartCreateTime() == null &&
                reqDto.getEndCreateTime() == null;
    }
}    