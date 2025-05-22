package com.hsbc.transaction.repository;

import com.hsbc.transaction.dto.req.TransactionSearchReqDto;
import com.hsbc.transaction.dto.resp.Page;
import com.hsbc.transaction.dto.resp.TransactionRespDto;
import com.hsbc.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class TransactionRepository {
    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public void save(Transaction transaction) {
        if (transaction.getId() == null || transaction.getId().isEmpty()) {
            transaction.setId(String.valueOf(sequence.getAndIncrement()));
        }
        transactionMap.put(transaction.getId(), transaction);
    }

    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(transactionMap.get(id));
    }


    public void deleteById(String id) {
        Transaction transaction = transactionMap.get(id);
        transaction.markAsDeleted();
        transactionMap.put(id, transaction);
    }

    /**
     * 分页查询交易记录数
     *
     * @param reqDto reqDto
     * @return Page<Transaction>
     */
    public Page<TransactionRespDto> listTransactions(TransactionSearchReqDto reqDto) {
        List<TransactionRespDto> filteredTransactions = transactionMap.values().stream()
                .filter(t -> reqDto.getAccountId() == null || t.getAccountId().equals(reqDto.getAccountId()))
                .filter(t -> reqDto.getMinAmount() == null || t.getAmount().compareTo(reqDto.getMinAmount()) >= 0)
                .filter(t -> reqDto.getMaxAmount() == null || t.getAmount().compareTo(reqDto.getMaxAmount()) <= 0)
                .filter(t -> reqDto.getTypeCode() == null || Objects.equals(t.getTypeCode(), reqDto.getTypeCode()))
                .filter(t -> reqDto.getStartCreateTime() == null || t.getCreateTime().isAfter(reqDto.getStartCreateTime()))
                .filter(t -> reqDto.getEndCreateTime() == null || t.getCreateTime().isBefore(reqDto.getEndCreateTime()))
                .filter(t -> !t.isDeleted())
                .map(TransactionRespDto::convertToRespDto)
                .collect(Collectors.toList());

        // 计算分页信息
        int page = reqDto.getPage() - 1;
        int size = reqDto.getSize();
        int fromIndex = Math.min(page * size, filteredTransactions.size());
        int toIndex = Math.min(fromIndex + size, filteredTransactions.size());

        List<TransactionRespDto> pageContent = filteredTransactions.subList(fromIndex, toIndex);
        long totalElements = filteredTransactions.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return Page.<TransactionRespDto>builder()
                .content(pageContent)
                .page(reqDto.getPage())
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .hasNext(page < totalPages - 1)
                .build();
    }


}