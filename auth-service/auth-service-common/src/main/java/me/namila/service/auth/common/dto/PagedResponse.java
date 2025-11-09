package me.namila.service.auth.common.dto;

import java.util.List;

/**
 * Generic wrapper for paginated responses.
 * Provides metadata about pagination along with the content.
 * 
 * @param <T> The type of content in the page
 */
public class PagedResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    
    /**
     * Default constructor.
     */
    public PagedResponse() {
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param content the list of items in the current page
     * @param page the current page number (0-based)
     * @param size the size of the page
     * @param totalElements the total number of elements across all pages
     * @param totalPages the total number of pages
     * @param first whether this is the first page
     * @param last whether this is the last page
     * @param empty whether the page is empty
     */
    public PagedResponse(List<T> content, int page, int size, long totalElements, 
                         int totalPages, boolean first, boolean last, boolean empty) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.empty = empty;
    }
    
    /**
     * Create a PagedResponse from Spring Data Page object.
     * 
     * @param <T> the type of content
     * @param content the list of items
     * @param page the page number
     * @param size the page size
     * @param totalElements the total number of elements
     * @return a new PagedResponse instance
     */
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        boolean empty = content == null || content.isEmpty();
        
        return new PagedResponse<>(content, page, size, totalElements, totalPages, first, last, empty);
    }
    
    /**
     * Create an empty PagedResponse.
     * 
     * @param <T> the type of content
     * @return an empty PagedResponse
     */
    public static <T> PagedResponse<T> empty() {
        return new PagedResponse<>(List.of(), 0, 0, 0, 0, true, true, true);
    }
    
    // Getters and Setters
    
    public List<T> getContent() {
        return content;
    }
    
    public void setContent(List<T> content) {
        this.content = content;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isEmpty() {
        return empty;
    }
    
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
    @Override
    public String toString() {
        return "PagedResponse{" +
                "page=" + page +
                ", size=" + size +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", empty=" + empty +
                ", contentSize=" + (content != null ? content.size() : 0) +
                '}';
    }
}

