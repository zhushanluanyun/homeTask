package com.hsbc.transaction.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {
    private List<T> content;        // 当前页数据
    private int page;               // 当前页码
    private int size;               // 每页大小
    private long totalElements;     // 总记录数
    private int totalPages;         // 总页数
    private boolean hasNext;        // 是否有下一页
}