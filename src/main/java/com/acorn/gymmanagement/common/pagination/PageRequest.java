package com.acorn.gymmanagement.common.pagination;

public record PageRequest(
        Integer page,
        Integer size
) {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    public PageRequest {
        page = page == null || page < 1
                ? DEFAULT_PAGE
                :page;

        size = size == null || size < 1
                ? DEFAULT_SIZE
                : Math.min(size, MAX_SIZE);
    }

    public int offset(){
        return (page - 1) * size;
    }
}
