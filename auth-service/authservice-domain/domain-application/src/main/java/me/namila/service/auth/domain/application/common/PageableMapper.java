package me.namila.service.auth.domain.application.common;

import me.namila.service.auth.common.dto.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Mapper for converting PageRequest to Spring Data Pageable.
 */
public final class PageableMapper {
    
    private PageableMapper() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Convert PageRequest to Spring Data Pageable.
     * 
     * @param pageRequest the page request
     * @return a Spring Data Pageable
     */
    public static Pageable toPageable(PageRequest pageRequest) {
        if (pageRequest == null) {
            return org.springframework.data.domain.PageRequest.of(0, 20);
        }
        
        if (pageRequest.hasSort()) {
            Sort sort = parseSort(pageRequest.getSort());
            return org.springframework.data.domain.PageRequest.of(
                    pageRequest.getPage(),
                    pageRequest.getSize(),
                    sort
            );
        }
        
        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(),
                pageRequest.getSize()
        );
    }
    
    /**
     * Convert PageRequest to Spring Data Pageable with default sort.
     * 
     * @param pageRequest the page request
     * @param defaultSort the default sort to use if none specified
     * @return a Spring Data Pageable
     */
    public static Pageable toPageable(PageRequest pageRequest, Sort defaultSort) {
        if (pageRequest == null) {
            return org.springframework.data.domain.PageRequest.of(0, 20, defaultSort);
        }
        
        Sort sort = pageRequest.hasSort() ? parseSort(pageRequest.getSort()) : defaultSort;
        
        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPage(),
                pageRequest.getSize(),
                sort
        );
    }
    
    /**
     * Parse sort string to Spring Data Sort object.
     * Format: "property,direction" or "property1,direction1;property2,direction2"
     * Example: "createdAt,desc" or "name,asc;createdAt,desc"
     * 
     * @param sortString the sort string
     * @return a Spring Data Sort object
     */
    private static Sort parseSort(String sortString) {
        if (sortString == null || sortString.trim().isEmpty()) {
            return Sort.unsorted();
        }
        
        String[] sortParts = sortString.split(";");
        Sort sort = Sort.unsorted();
        
        for (String sortPart : sortParts) {
            String[] parts = sortPart.trim().split(",");
            if (parts.length >= 1) {
                String property = parts[0].trim();
                Sort.Direction direction = Sort.Direction.ASC;
                
                if (parts.length >= 2) {
                    String directionStr = parts[1].trim().toUpperCase();
                    direction = "DESC".equals(directionStr) ? Sort.Direction.DESC : Sort.Direction.ASC;
                }
                
                Sort.Order order = new Sort.Order(direction, property);
                sort = sort.and(Sort.by(order));
            }
        }
        
        return sort;
    }
    
    /**
     * Create a Pageable with page and size only.
     * 
     * @param page the page number
     * @param size the page size
     * @return a Spring Data Pageable
     */
    public static Pageable of(int page, int size) {
        return org.springframework.data.domain.PageRequest.of(page, size);
    }
    
    /**
     * Create a Pageable with page, size, and sort.
     * 
     * @param page the page number
     * @param size the page size
     * @param sort the sort
     * @return a Spring Data Pageable
     */
    public static Pageable of(int page, int size, Sort sort) {
        return org.springframework.data.domain.PageRequest.of(page, size, sort);
    }
}

