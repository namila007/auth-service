package me.namila.service.auth.common.dto;

import java.util.List;
import java.util.function.Function;

/**
 * Utility class for mapping Spring Data Page objects to PagedResponse.
 * This class is designed to work without direct Spring Data dependencies in the common module.
 */
public final class PageMapper {
    
    private PageMapper() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Create a PagedResponse from page metadata and content.
     * 
     * @param <T> the type of content
     * @param content the list of items in the current page
     * @param pageNumber the current page number (0-based)
     * @param pageSize the size of the page
     * @param totalElements the total number of elements across all pages
     * @return a new PagedResponse instance
     */
    public static <T> PagedResponse<T> toPagedResponse(
            List<T> content, 
            int pageNumber, 
            int pageSize, 
            long totalElements) {
        return PagedResponse.of(content, pageNumber, pageSize, totalElements);
    }
    
    /**
     * Create a PagedResponse by mapping the content of another PagedResponse.
     * Useful for converting domain objects to DTOs while preserving pagination metadata.
     * 
     * @param <S> the source type
     * @param <T> the target type
     * @param source the source PagedResponse
     * @param mapper the function to map each item
     * @return a new PagedResponse with mapped content
     */
    public static <S, T> PagedResponse<T> map(PagedResponse<S> source, Function<S, T> mapper) {
        List<T> mappedContent = source.getContent().stream()
                .map(mapper)
                .toList();
        
        return new PagedResponse<>(
                mappedContent,
                source.getPage(),
                source.getSize(),
                source.getTotalElements(),
                source.getTotalPages(),
                source.isFirst(),
                source.isLast(),
                source.isEmpty()
        );
    }
    
    /**
     * Create a PagedResponse by mapping a list of items with pagination metadata.
     * 
     * @param <S> the source type
     * @param <T> the target type
     * @param sourceContent the source list
     * @param mapper the function to map each item
     * @param pageNumber the current page number
     * @param pageSize the page size
     * @param totalElements the total number of elements
     * @return a new PagedResponse with mapped content
     */
    public static <S, T> PagedResponse<T> mapAndCreate(
            List<S> sourceContent,
            Function<S, T> mapper,
            int pageNumber,
            int pageSize,
            long totalElements) {
        
        List<T> mappedContent = sourceContent.stream()
                .map(mapper)
                .toList();
        
        return PagedResponse.of(mappedContent, pageNumber, pageSize, totalElements);
    }
}

