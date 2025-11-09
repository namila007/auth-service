package me.namila.service.auth.domain.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic wrapper class for paginated responses.
 * Provides pagination metadata along with the content.
 * 
 * @param <T> The type of content in the page
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    /**
     * The list of items in the current page.
     */
    private List<T> content;
    
    /**
     * The current page number (0-based).
     */
    private int page;
    
    /**
     * The number of items per page.
     */
    private int size;
    
    /**
     * The total number of items across all pages.
     */
    private long totalElements;
    
    /**
     * The total number of pages.
     */
    private int totalPages;
    
    /**
     * Whether this is the first page.
     */
    private boolean first;
    
    /**
     * Whether this is the last page.
     */
    private boolean last;
    
    /**
     * Whether there is a next page.
     */
    private boolean hasNext;
    
    /**
     * Whether there is a previous page.
     */
    private boolean hasPrevious;
    
    /**
     * Whether the page is empty.
     */
    private boolean empty;
    
    /**
     * Constructor that creates a PagedResponse from a Spring Data Page object.
     * 
     * @param page the Spring Data Page object
     */
    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
        this.empty = page.isEmpty();
    }
    
    /**
     * Static factory method to create a PagedResponse from a Spring Data Page.
     * 
     * @param page the Spring Data Page object
     * @param <T> the type of content
     * @return a new PagedResponse instance
     */
    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(page);
    }
    
    /**
     * Static factory method to create an empty PagedResponse.
     * 
     * @param <T> the type of content
     * @return an empty PagedResponse instance
     */
    public static <T> PagedResponse<T> empty() {
        PagedResponse<T> response = new PagedResponse<>();
        response.setContent(List.of());
        response.setPage(0);
        response.setSize(0);
        response.setTotalElements(0);
        response.setTotalPages(0);
        response.setFirst(true);
        response.setLast(true);
        response.setHasNext(false);
        response.setHasPrevious(false);
        response.setEmpty(true);
        return response;
    }
}

