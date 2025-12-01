package elearningspringboot.service.impl;

import elearningspringboot.dto.request.FlashcardRequest;
import elearningspringboot.dto.request.FlashcardSetRequest;
import elearningspringboot.dto.response.FlashcardSetResponse;
import elearningspringboot.dto.response.PageResponse;
import elearningspringboot.entity.Flashcard;
import elearningspringboot.entity.FlashcardSet;
import elearningspringboot.entity.User;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.FlashcardMapper;
import elearningspringboot.repository.FlashcardSetRepository;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.service.FlashcardService;
import elearningspringboot.service.StorageService;
import elearningspringboot.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardSetRepository repository;
    private final UserRepository userRepository;
    private final FlashcardMapper mapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public FlashcardSetResponse create(FlashcardSetRequest request, MultipartFile thumbnail) {
        Long userId = AppUtils.getUserIdFromSecurityContext();
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FlashcardSet entity = mapper.toEntity(request);
        entity.setAuthor(author);

        if (request.getCards() != null) {
            List<Flashcard> flashcards = new ArrayList<>();
            for (FlashcardRequest cardReq : request.getCards()) {
                Flashcard card = mapper.toCardEntity(cardReq);
                card.setFlashcardSet(entity);
                flashcards.add(card);
            }
            entity.setFlashcards(flashcards);
        }

        if (thumbnail != null && !thumbnail.isEmpty()) {
            String url = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(url);
        }

        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public FlashcardSetResponse update(Long id, FlashcardSetRequest request, MultipartFile thumbnail) {
        FlashcardSet entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard set not found"));

        Long currentUserId = AppUtils.getUserIdFromSecurityContext();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!Objects.equals(entity.getAuthor().getId(), currentUserId) && !AppUtils.hasRole(auth, "ADMIN")) {
            throw new AccessDeniedException("Bạn không có quyền sửa bộ thẻ này");
        }
        // ------------------------------------------

        mapper.updateEntity(entity, request);

        entity.getFlashcards().clear();
        if (request.getCards() != null) {
            for (FlashcardRequest cardReq : request.getCards()) {
                Flashcard card = mapper.toCardEntity(cardReq);
                card.setFlashcardSet(entity);
                entity.getFlashcards().add(card);
            }
        }

        if (thumbnail != null && !thumbnail.isEmpty()) {
            String url = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(url);
        }

        repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FlashcardSet entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard set not found"));

        Long currentUserId = AppUtils.getUserIdFromSecurityContext();
        // --- SỬA LỖI: Lấy Authentication object ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!Objects.equals(entity.getAuthor().getId(), currentUserId) && !AppUtils.hasRole(auth, "ADMIN")) {
            throw new AccessDeniedException("Bạn không có quyền xóa bộ thẻ này");
        }
        // ------------------------------------------

        repository.delete(entity);
    }

    @Override
    public FlashcardSetResponse getById(Long id) {
        FlashcardSet entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard set not found"));
        return mapper.toDTO(entity);
    }

    @Override
    public PageResponse<List<FlashcardSetResponse>> getAll(int page, int size, String keyword, String category, String sort) {
        Sort sortObj = Sort.by("createdAt").descending();
        if ("oldest".equals(sort)) sortObj = Sort.by("createdAt").ascending();
        if ("popular".equals(sort)) sortObj = Sort.by("studyCount").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sortObj);
        Long currentUserId = null;
        try {
            currentUserId = AppUtils.getUserIdFromSecurityContext();
        } catch (Exception e) {
            // User chưa đăng nhập
        }

        Page<FlashcardSet> pageData = repository.searchFlashcards(keyword, category, currentUserId, pageable);

        return PageResponse.<List<FlashcardSetResponse>>builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .numberOfElements(pageData.getNumberOfElements())
                .items(pageData.getContent().stream().map(mapper::toDTO).toList())
                .build();
    }


    @Override
    public PageResponse<List<FlashcardSetResponse>> getAllForManagement(int page, int size, String keyword, String category, String sort, Long authorId) {
        Sort sortObj = Sort.by("createdAt").descending();
        if ("oldest".equals(sort)) sortObj = Sort.by("createdAt").ascending();
        if ("popular".equals(sort)) sortObj = Sort.by("studyCount").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sortObj);

        Page<FlashcardSet> pageData = repository.findAllForManagement(keyword, category, authorId, pageable);

        return PageResponse.<List<FlashcardSetResponse>>builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(pageData.getTotalPages())
                .numberOfElements(pageData.getNumberOfElements())
                .items(pageData.getContent().stream().map(mapper::toDTO).toList())
                .build();
    }
}