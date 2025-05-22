package com.hsbc.transaction.dto.req;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 交易请求dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReqDto {

    @NotNull
    // 操作账户
    private String accountId;
    // 金额
    @NotNull
    private BigDecimal amount;
    // 交易类型
    @NotNull
    private Integer typeCode;
    // 备注信息
    private String description;

}
