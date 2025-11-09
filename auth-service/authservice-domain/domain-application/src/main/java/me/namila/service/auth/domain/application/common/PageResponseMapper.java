package me.namila.service.auth.domain.application.common;

import me.namila.service.auth.common.dto.PagedResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Mapper for converting Spring Data Page objects to PagedResponse.
 * This class bridges Spring Data pagination with our common PagedResponse DTO.
 */
public final class PageResponseMapper {
    
    private PageResponseMapper() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Convert a Spring Data Page to PagedResponse without mapping content.
     * 
     * @param <T> the type of content
     * @param page the Spring Data Page
     * @return a PagedResponse with the same content
     */
    public static <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
    
    /**
     * Convert a Spring Data Page to PagedResponse while mapping the content.
     * Useful for converting domain objects to DTOs.
     * 
     * @param <S> the source type (domain)
     * @param <T> the target type (DTO)
     * @param page the Spring Data Page
     * @param mapper the function to map each item
     * @return a PagedResponse with mapped content
     */
    public static <S, T> PagedResponse<T> toPagedResponse(Page<S> page, Function<S, T> mapper) {
        List<T> mappedContent = page.getContent().stream()
                .map(mapper)
                .toList();
        
        return new PagedResponse<>(
                mappedContent,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
    
    /**
     * Convert a Spring Data Page to PagedResponse using Page.map().
     * This is more efficient as it uses Spring's built-in mapping.
     * 
     * @param <S> the source type
     * @param <T> the target type
     * @param page the Spring Data Page
     * @param mapper the function to map each item
     * @return a PagedResponse with mapped content
     */
    public static <S, T> PagedResponse<T> map(Page<S> page, Function<S, T> mapper) {
        Page<T> mappedPage = page.map(mapper);
        return toPagedResponse(mappedPage);
    }
}

