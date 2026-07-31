package com.acorn.gymmanagement.common.pagination;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        long totalElements,
        int page,
        int size,
        int totalPages,
        boolean hasPrevious,
        boolean hasNext
) {
    public static <T> PageResult<T> of(
            List<T> content,
            long totalElements,
            PageRequest request
    ){
        int totalPages = totalElements == 0
                ? 0
                : (int) Math.ceil((double) totalElements / request.size());

        return new PageResult<>(
                content,
                totalElements,
                request.page(),
                request.size(),
                totalPages,
                request.page() > 1,
                request.page() < totalPages
        );
    }
}
