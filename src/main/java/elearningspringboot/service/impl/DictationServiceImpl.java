package elearningspringboot.service.impl;

import elearningspringboot.dto.request.*;
import elearningspringboot.dto.response.*;
import elearningspringboot.entity.*;
import elearningspringboot.util.AppUtils;
import elearningspringboot.exception.ResourceNotFoundException;
import elearningspringboot.mapper.DictationMapper;
import elearningspringboot.repository.*;
import elearningspringboot.service.DictationService;
import elearningspringboot.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DictationServiceImpl implements DictationService {

    private final DictationTopicRepository topicRepository;
    private final DictationLessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final DictationMapper mapper;
    private final StorageService storageService;

    @Override
    public List<DictationTopicResponse> getAllTopics() {
        return topicRepository.findAll().stream().map(mapper::toTopicDTO).toList();
    }

    @Override
    public List<DictationTopicResponse> getTopicsByAuthor(Long authorId) {
        return topicRepository.findByAuthorId(authorId).stream()
                .map(mapper::toTopicDTO).toList();
    }

    @Override
    public DictationTopicResponse getTopicById(Long id) {
        DictationTopic entity = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        return mapper.toTopicDTO(entity);
    }

    @Override
    @Transactional
    public DictationTopicResponse createTopic(DictationTopicRequest request, MultipartFile thumbnail) {
        Long userId = AppUtils.getUserIdFromSecurityContext();
        User author = userRepository.findById(userId).orElseThrow();

        DictationTopic entity = mapper.toTopicEntity(request);
        entity.setAuthor(author);
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String url = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(url);
        }
        topicRepository.save(entity);
        return mapper.toTopicDTO(entity);
    }

    @Override
    @Transactional
    public DictationTopicResponse updateTopic(Long id, DictationTopicRequest request, MultipartFile thumbnail) {
        DictationTopic entity = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        mapper.updateTopic(entity, request);
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String url = storageService.uploadFile(thumbnail);
            entity.setThumbnailUrl(url);
        }
        topicRepository.save(entity);
        return mapper.toTopicDTO(entity);
    }

    @Override
    @Transactional
    public void deleteTopic(Long id) {
        topicRepository.deleteById(id);
    }

    // --- LESSON ---
    @Override
    public List<DictationLessonResponse> getLessonsByTopicId(Long topicId) {
        return lessonRepository.findByTopicId(topicId).stream()
                .map(mapper::toLessonDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DictationLessonResponse getLessonById(Long id) {
        DictationLesson entity = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        return mapper.toLessonDTO(entity);
    }

    @Override
    @Transactional
    public DictationLessonResponse createLesson(DictationLessonRequest request, MultipartFile audioFile) {
        DictationTopic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        DictationLesson lesson = mapper.toLessonEntity(request);
        lesson.setTopic(topic);

        // Upload Audio (Dùng hàm uploadVideo vì audio cũng là media stream)
        if (audioFile != null && !audioFile.isEmpty()) {
            String url = storageService.uploadVideo(audioFile);
            lesson.setMediaUrl(url);
        }

        // Xử lý Sentences (Quan trọng)
        if (request.getSentences() != null) {
            List<DictationSentence> sentences = new ArrayList<>();
            int index = 1;
            for (DictationSentenceRequest sentReq : request.getSentences()) {
                DictationSentence s = mapper.toSentenceEntity(sentReq);
                s.setLesson(lesson); // Link ngược lại
                s.setOrderIndex(index++);
                sentences.add(s);
            }
            lesson.setSentences(sentences);
        }

        lessonRepository.save(lesson);
        return mapper.toLessonDTO(lesson);
    }

    @Override
    @Transactional
    public DictationLessonResponse updateLesson(Long id, DictationLessonRequest request, MultipartFile audioFile) {
        // 1. Tìm bài học
        DictationLesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        // 2. Cập nhật thông tin cơ bản (title, desc, vocabLevel...)
        mapper.updateLesson(lesson, request);

        // 3. Cập nhật Topic nếu có thay đổi
        if (!lesson.getTopic().getId().equals(request.getTopicId())) {
            DictationTopic newTopic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
            lesson.setTopic(newTopic);
        }

        // 4. Cập nhật File Audio/Video nếu có gửi lên
        if (audioFile != null && !audioFile.isEmpty()) {
            String url = storageService.uploadVideo(audioFile);
            lesson.setMediaUrl(url);
        }

        // 5. Cập nhật danh sách câu (Sentences) - SỬA LỖI HIBERNATE TẠI ĐÂY
        if (request.getSentences() != null) {
            // Lấy danh sách hiện tại do Hibernate quản lý
            List<DictationSentence> currentSentences = lesson.getSentences();

            // Nếu danh sách chưa khởi tạo thì tạo mới
            if (currentSentences == null) {
                currentSentences = new ArrayList<>();
                lesson.setSentences(currentSentences);
            }

            // BƯỚC QUAN TRỌNG: Xóa sạch phần tử cũ trong list hiện tại
            // Hibernate sẽ tự động xóa các record tương ứng trong DB (nhờ orphanRemoval = true)
            currentSentences.clear();

            // Thêm các phần tử mới vào list hiện tại
            int index = 1;
            for (DictationSentenceRequest sentReq : request.getSentences()) {
                DictationSentence s = mapper.toSentenceEntity(sentReq);

                // Thiết lập quan hệ 2 chiều: Câu thuộc về Bài học này
                s.setLesson(lesson);
                s.setOrderIndex(index++);

                // Add vào list cũ
                currentSentences.add(s);
            }
        }

        // 6. Lưu lại
        lessonRepository.save(lesson);
        return mapper.toLessonDTO(lesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}