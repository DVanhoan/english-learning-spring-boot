package elearningspringboot.repository;

import elearningspringboot.entity.DictationLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictationLessonRepository extends JpaRepository<DictationLesson, Long> {
    List<DictationLesson> findByTopicId(Long topicId);
}