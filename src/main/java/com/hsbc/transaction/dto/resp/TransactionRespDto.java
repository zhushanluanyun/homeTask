package com.hsbc.transaction.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hsbc.transaction.model.Transaction;
import com.hsbc.transaction.model.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

/**
 * 交易响应dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRespDto {

    // 唯一值
    private String id;
    // 操作账户
    private String accountId;
    // 金额
    private BigDecimal amount;
    // 交易类型
    private String transactionType;
    // 更新时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updateTime;
    // 创建时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createTime;
    // 更新人
    private String updateAccount;
    // 备注信息
    private String description;

    /**
     * 转换为响应dto
     *
     * @param transaction transaction
     * @return TransactionRespDto
     */
    public static TransactionRespDto convertToRespDto(Transaction transaction) {
        TransactionRespDto resp = TransactionRespDto.builder().build();
        BeanUtils.copyProperties(transaction, resp);
        if (transaction.getTypeCode() != null) {
            Optional<TransactionTypeEnum> transactionTypeOptional = TransactionTypeEnum.fromCode(transaction.getTypeCode());
            transactionTypeOptional.ifPresent(type -> resp.setTransactionType(type.getTransactionTypeDesc()));
        }
        return resp;
    }
}
