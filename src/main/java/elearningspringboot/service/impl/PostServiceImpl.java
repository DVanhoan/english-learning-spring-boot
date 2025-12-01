package elearningspringboot.service.impl;

import elearningspringboot.dto.request.PostRequest;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.dto.response.PostResponse;
import elearningspringboot.dto.response.UserSummaryResponse;
import elearningspringboot.entity.CategoryPost;
import elearningspringboot.entity.Post;
import elearningspringboot.entity.User;
import elearningspringboot.enumeration.StatusPost;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.PostMapper;
import elearningspringboot.repository.CategoryPostRepository;
import elearningspringboot.repository.PostRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.PostService;
import elearningspringboot.service.StorageService;
import elearningspringboot.util.AppUtils;
import elearningspringboot.util.HtmlImageProcessor;
import elearningspringboot.util.TagUtils;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository repository;
    private final PostMapper mapper;
    private final CategoryPostRepository categoryRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final MessageSource messageSource;
    private final HtmlImageProcessor htmlImageProcessor;

    @Override
    @Transactional
    public PostResponse create(PostRequest request, MultipartFile thumbnail) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Post entity = mapper.toEntity(request);
        CategoryPost category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("categoryPost.notFound", null, LocaleContextHolder.getLocale())));
        entity.setCategory(category);
        User author;
        if (AppUtils.hasRole(auth, "ADMIN") && request.getAuthorId() != null) {
            author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found.by.id",
                            new Object[] { request.getAuthorId() }, LocaleContextHolder.getLocale())));
        } else {
            Long id = AppUtils.getUserIdFromSecurityContext();
            author = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found.by.id",
                            new Object[] { id }, LocaleContextHolder.getLocale())));
        }
        entity.setAuthor(author);

        String processedContent = htmlImageProcessor.process(request.getContent());
        entity.setContent(processedContent);
        entity.setContentText(Jsoup.parse(processedContent).text());
        String tags = TagUtils.extractTagsAsPipe(processedContent);
        entity.setTags(tags);
        int words = AppUtils.countWords(processedContent);
        entity.setReadingTimeMinutes(Math.max(1, words / 200));
        entity.setSlug(AppUtils.toSlug(entity.getTitle()));
        if (request.getStatus() != null && AppUtils.hasRole(auth, "ADMIN")) {
            entity.setStatus(StatusPost.fromValue(request.getStatus()));
        } else if (!AppUtils.hasRole(auth, "ADMIN")) {
            entity.setStatus(StatusPost.PENDING);
        }
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(thumbnailUrl);
        }
        repository.save(entity);
        PostResponse response = mapper.toDTO(entity);
        response.setAuthor(UserSummaryResponse.builder()
                .id(entity.getAuthor().getId())
                .avatarUrl(entity.getAuthor().getAvatarUrl())
                .fullName(entity.getAuthor().getFullName())
                .role(entity.getAuthor().getRole().getRole().getName())
                .build());
        return response;
    }

    @Override
    @Transactional
    public PostResponse update(Long id, PostRequest request, MultipartFile thumbnail) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Post entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));

        if (!AppUtils.hasRole(auth, "ADMIN")) {
            Long currentUserId = AppUtils.getUserIdFromSecurityContext();
            if (!Objects.equals(currentUserId, entity.getAuthor().getId())) {
                throw new AccessDeniedException(
                        messageSource.getMessage("error.accessDenied", null, LocaleContextHolder.getLocale()));
            }
        }

        mapper.updateEntityFromRequest(request, entity);

        if (request.getCategoryId() != null) {
            CategoryPost category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            messageSource.getMessage("categoryPost.notFound", null, LocaleContextHolder.getLocale())));
            entity.setCategory(category);
        }
        if (request.getAuthorId() != null && AppUtils.hasRole(auth, "ADMIN")) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException(messageSource.getMessage("user.not.found.by.id",
                            new Object[] { request.getAuthorId() }, LocaleContextHolder.getLocale())));
            entity.setAuthor(author);
        }

        if (request.getContent() != null) {
            String processedContent = htmlImageProcessor.process(request.getContent());
            entity.setContent(processedContent);
            entity.setContentText(Jsoup.parse(processedContent).text());
            entity.setTags(TagUtils.extractTagsAsPipe(processedContent));
            int words = AppUtils.countWords(processedContent);
            entity.setReadingTimeMinutes(Math.max(1, words / 200));
        }
        if (request.getTitle() != null) {
            entity.setSlug(AppUtils.toSlug(request.getTitle()));
        }
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(thumbnailUrl);
        }

        if (request.getStatus() != null && AppUtils.hasRole(auth, "ADMIN")) {
            entity.setStatus(StatusPost.fromValue(request.getStatus()));
        }

        repository.save(entity);
        PostResponse response = mapper.toDTO(entity);
        response.setAuthor(UserSummaryResponse.builder()
                .id(entity.getAuthor().getId())
                .avatarUrl(entity.getAuthor().getAvatarUrl())
                .fullName(entity.getAuthor().getFullName())
                .role(entity.getAuthor().getRole().getRole().getName())
                .build());
        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Post entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));
        if (AppUtils.hasRole(authentication, "ADMIN")
                || Objects.equals(AppUtils.getUserIdFromSecurityContext(), entity.getAuthor().getId())) {
            repository.delete(entity);
        } else {
            throw new AccessDeniedException(
                    messageSource.getMessage("error.accessDenied", null, LocaleContextHolder.getLocale()));
        }
    }

    @Override
    @PostAuthorize("hasRole('ADMIN') " +
            "or returnObject.status == T(elearningspringboot.enumeration.StatusPost).PUBLIC " +
            "or (isAuthenticated() and returnObject.author.id == authentication.principal.id)")
    public PostResponse getById(Long id) {
        Post entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));
        PostResponse response = mapper.toDTO(entity);
        response.setAuthor(UserSummaryResponse.builder()
                .id(entity.getAuthor().getId())
                .avatarUrl(entity.getAuthor().getAvatarUrl())
                .fullName(entity.getAuthor().getFullName())
                .role(entity.getAuthor().getRole().getRole().getName())
                .build());
        return response;
    }

    @Override
    public PageResponse<List<PostResponse>> getAll(int pageNumber, int pageSize, List<String> sorts, String keyword,
            Long categoryId, boolean isPublic, Long authorId) {
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        List<String> whiteListFieldSorts = List.of("createdAt", "updatedAt");
        Pageable pageable = AppUtils.generatePageableWithSort(sorts, whiteListFieldSorts, pageNumber, pageSize);
        Page<Post> page;
        if (!isPublic) {
            page = repository.searchPosts(kw, categoryId, authorId, pageable);
        } else {
            page = repository.searchPublicPosts(kw, categoryId, StatusPost.PUBLIC, pageable);
        }
        List<PostResponse> items = page.getContent().stream().map(entity -> {
            PostResponse response = mapper.toDTO(entity);
            response.setAuthor(UserSummaryResponse.builder()
                    .id(entity.getAuthor().getId())
                    .avatarUrl(entity.getAuthor().getAvatarUrl())
                    .fullName(entity.getAuthor().getFullName())
                    .role(entity.getAuthor().getRole().getRole().getName())
                    .build());
            return response;
        }).toList();
        return PageResponse.<List<PostResponse>>builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .numberOfElements(page.getNumberOfElements())
                .items(items)
                .build();
    }

    @Override
    public List<PostResponse> getRelated(Long postId, int limit) {
        Post anchor = repository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())));

        List<Post> related = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();

        String searchText = cleanSearchText(anchor.getTitle() + " " +
                (anchor.getContentText() != null ? anchor.getContentText() : ""));
        if (!searchText.trim().isEmpty()) {
            try {
                var fullTextResults = repository.findRelatedByFullTextSearch(
                        anchor.getId(), searchText, limit);
                addUniqueResults(related, addedIds, fullTextResults);
            } catch (Exception e) {
                System.out.println("Full-text search failed: " + e.getMessage());
            }
        }

        if (related.size() < limit && anchor.getCategory() != null) {
            int remaining = limit - related.size();
            var categoryResults = repository.findRelatedByCategory(
                    StatusPost.PUBLIC,
                    anchor.getCategory().getId(),
                    anchor.getId(),
                    org.springframework.data.domain.PageRequest.of(0, remaining));
            addUniqueResults(related, addedIds, categoryResults);
        }

        if (related.size() < limit) {
            int remaining = limit - related.size();
            var latestResults = repository.findLatestPosts(
                    StatusPost.PUBLIC,
                    anchor.getId(),
                    org.springframework.data.domain.PageRequest.of(0, remaining));
            addUniqueResults(related, addedIds, latestResults);
        }

        return related.stream()
                .limit(limit)
                .map(p -> {
                    PostResponse response = mapper.toDTO(p);
                    response.setAuthor(UserSummaryResponse.builder()
                            .id(p.getAuthor().getId())
                            .avatarUrl(p.getAuthor().getAvatarUrl())
                            .fullName(p.getAuthor().getFullName())
                            .role(p.getAuthor().getRole().getRole().getName())
                            .build());
                    return response;
                })
                .toList();
    }

    private void addUniqueResults(List<Post> target, Set<Long> addedIds, List<Post> source) {
        for (Post post : source) {
            if (!addedIds.contains(post.getId())) {
                target.add(post);
                addedIds.add(post.getId());
            }
        }
    }

    private String cleanSearchText(String text) {
        if (text == null)
            return "";
        return text.replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    @Override
    public List<PostResponse> getLatestPosts(int limit) {
        Pageable pageable = PageRequest.of(0,
                Math.min(limit, 50));
        List<Post> posts = repository.findLatestPublicPosts(StatusPost.PUBLIC, pageable);

        return posts.stream()
                .map(entity -> {
                    PostResponse response = mapper.toDTO(entity);
                    response.setAuthor(UserSummaryResponse.builder()
                            .id(entity.getAuthor().getId())
                            .avatarUrl(entity.getAuthor().getAvatarUrl())
                            .fullName(entity.getAuthor().getFullName())
                            .role(entity.getAuthor().getRole().getRole().getName())
                            .build());
                    return response;
                })
                .toList();
    }
}