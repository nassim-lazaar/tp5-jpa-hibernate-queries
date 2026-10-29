package com.example.util;

import java.util.List;

public class PaginationResult<T> {
    private List<T> items;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalItems;

    public PaginationResult(List<T> elements, int pageActuelle, int taillePage, long nombreTotal) {
        this.items = elements;
        this.currentPage = pageActuelle;
        this.pageSize = taillePage;
        this.totalItems = nombreTotal;
        this.totalPages = taillePage > 0 ? (int) Math.ceil((double) nombreTotal / taillePage) : 0;
    }

    public List<T> getItems() {
        return items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    @Override
    public String toString() {
        return "PaginationResult{" +
                "currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", totalItems=" + totalItems +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}
