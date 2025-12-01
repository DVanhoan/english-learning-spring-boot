package elearningspringboot.service.impl;

import elearningspringboot.dto.request.CategoryCourseRequest;
import elearningspringboot.dto.response.CategoryCourseResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.entity.CategoryCourse;
import elearningspringboot.exception.ResourceConflictException;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.CategoryCourseMapper;
import elearningspringboot.util.AppUtils;
import elearningspringboot.repository.CategoryCourseRepository;
import elearningspringboot.service.CategoryCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryCourseServiceImpl implements CategoryCourseService {

    private final CategoryCourseMapper mapper;
    private final CategoryCourseRepository repository;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public CategoryCourseResponse create(CategoryCourseRequest request) {
        if (repository.existsByTitle(request.getTitle())) {
            String message = messageSource.getMessage("categoryCourse.exists", new Object[] { request.getTitle() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }
        CategoryCourse entity = mapper.toEntity(request);
        if (entity.getTitle() != null) {
            entity.setSlug(AppUtils.toSlug(entity.getTitle()));
        }
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public CategoryCourseResponse update(Long id, CategoryCourseRequest request) {
        CategoryCourse entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("categoryCourse.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });

        if (request.getTitle() != null && repository.existsByTitleAndIdNot(request.getTitle(), id)) {
            String message = messageSource.getMessage("categoryCourse.exists", new Object[] { request.getTitle() },
                    LocaleContextHolder.getLocale());
            throw new ResourceConflictException(message);
        }

        mapper.updateEntityFromRequest(request, entity);
        if (request.getTitle() != null) {
            entity.setSlug(AppUtils.toSlug(request.getTitle()));
        }
        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CategoryCourse entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("categoryCourse.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        repository.delete(entity);
    }

    @Override
    public CategoryCourseResponse getById(Long id) {
        CategoryCourse entity = repository.findById(id)
                .orElseThrow(() -> {
                    String message = messageSource.getMessage("categoryCourse.notFound", null,
                            LocaleContextHolder.getLocale());
                    return new ResourceNotFoundException(message);
                });
        ;
        return mapper.toDTO(entity);
    }

    @Override
    public List<CategoryCourseResponse> getAll() {
        List<CategoryCourse> list = repository.findAll();
        return mapper.toListDTOs(list);
    }

    @Override
    public PageResponse<List<CategoryCourseResponse>> getCategoriesWithPaginationAndKeyword(int pageNumber,
            int pageSize, java.util.List<String> sorts, String keyword) {
        List<String> whiteListFieldSorts = java.util.List.of("title", "createdAt", "updatedAt");
        Page<CategoryCourse> page = repository.searchCategories(keyword == null ? "" : keyword.toLowerCase(),
                AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize));

        List<CategoryCourseResponse> items = page.getContent().stream().map(mapper::toDTO).toList();

        return PageResponse.<List<CategoryCourseResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(items)
                .build();
    }
}
