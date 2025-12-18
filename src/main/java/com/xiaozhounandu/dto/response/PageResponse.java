package com.xiaozhounandu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer pages;
}
