package com.hsbc.transaction.dto.req;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class TransactionSearchReqDto {

    // 操作账户
    private String accountId;
    // 金额
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    // 金额
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal minAmount;
    // 金额
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal MaxAmount;
    // 更新时间
    private Instant startCreateTime;
    // 创建时间
    private Instant endCreateTime;
    // 交易类型
    private Integer typeCode;
    // 备注信息
    private String description;
    // 分页参数
    @Builder.Default
    @Min(value = 1, message = "Page number must be greater than or equal to 1")
    private Integer page = 1;
    @Builder.Default
    @Min(value = 1, message = "Page number must be greater than or equal to 1")
    private Integer size = 10;
}
