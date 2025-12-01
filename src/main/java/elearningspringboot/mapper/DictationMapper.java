package elearningspringboot.mapper;

import elearningspringboot.dto.request.*;
import elearningspringboot.dto.response.*;
import elearningspringboot.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DictationMapper {

    // --- TOPIC ---
    @Mapping(target = "lessonCount", expression = "java(entity.getLessons() != null ? entity.getLessons().size() : 0)")
    DictationTopicResponse toTopicDTO(DictationTopic entity);

    @Mapping(target = "lessons", ignore = true) // Không map lessons khi tạo topic
    DictationTopic toTopicEntity(DictationTopicRequest request);

    @Mapping(target = "lessons", ignore = true)
    void updateTopic(@MappingTarget DictationTopic entity, DictationTopicRequest request);

    // --- LESSON ---
    @Mapping(source = "topic.id", target = "topicId")
    @Mapping(source = "sentences", target = "sentences") // Map danh sách câu
    DictationLessonResponse toLessonDTO(DictationLesson entity);

    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "sentences", ignore = true) // Sentences sẽ được xử lý thủ công trong Service
    DictationLesson toLessonEntity(DictationLessonRequest request);

    @Mapping(target = "topic", ignore = true)
    @Mapping(target = "sentences", ignore = true)
    void updateLesson(@MappingTarget DictationLesson entity, DictationLessonRequest request);

    // --- SENTENCE ---
    DictationSentenceResponse toSentenceDTO(DictationSentence entity);

    DictationSentence toSentenceEntity(DictationSentenceRequest request);
}