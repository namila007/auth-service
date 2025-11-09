package me.namila.service.auth.common.dto;

/**
 * Standard pagination request parameters.
 * Can be used as a base class or embedded in specific request DTOs.
 */
public class PageRequest {
    
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    
    private int page;
    private int size;
    private String sort;
    
    /**
     * Default constructor with default values.
     */
    public PageRequest() {
        this.page = DEFAULT_PAGE;
        this.size = DEFAULT_SIZE;
    }
    
    /**
     * Constructor with page and size.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     */
    public PageRequest(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.min(Math.max(1, size), MAX_SIZE);
    }
    
    /**
     * Constructor with page, size, and sort.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the sort specification (e.g., "createdAt,desc")
     */
    public PageRequest(int page, int size, String sort) {
        this(page, size);
        this.sort = sort;
    }
    
    /**
     * Create a PageRequest with default values.
     * 
     * @return a new PageRequest with defaults
     */
    public static PageRequest ofDefaults() {
        return new PageRequest();
    }
    
    /**
     * Create a PageRequest with specified page and size.
     * 
     * @param page the page number
     * @param size the page size
     * @return a new PageRequest
     */
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }
    
    /**
     * Create a PageRequest with specified page, size, and sort.
     * 
     * @param page the page number
     * @param size the page size
     * @param sort the sort specification
     * @return a new PageRequest
     */
    public static PageRequest of(int page, int size, String sort) {
        return new PageRequest(page, size, sort);
    }
    
    // Getters and Setters
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = Math.max(0, page);
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = Math.min(Math.max(1, size), MAX_SIZE);
    }
    
    public String getSort() {
        return sort;
    }
    
    public void setSort(String sort) {
        this.sort = sort;
    }
    
    /**
     * Check if sorting is specified.
     * 
     * @return true if sort is not null and not empty
     */
    public boolean hasSort() {
        return sort != null && !sort.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sort='" + sort + '\'' +
                '}';
    }
}

