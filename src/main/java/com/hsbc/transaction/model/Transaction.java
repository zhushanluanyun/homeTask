package com.hsbc.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 交易实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    // 主键
    private String id;
    ;
    // 操作账户
    private String accountId;
    // 金额
    private BigDecimal amount;
    // 交易类型
    private Integer typeCode;
    // 更新时间
    @Builder.Default
    private Instant updateTime = Instant.now();
    // 创建时间
    private Instant createTime;
    // 更新人
    private String updateAccount;
    // 备注信息
    private String description;
    // 是否删除标志
    private boolean deleted;

    // 更新时自动更新时间
    public void setUpdateTime() {
        this.updateTime = Instant.now();
    }

    // 逻辑删除方法
    public void markAsDeleted() {
        this.deleted = true;
        setUpdateTime();
    }
}
