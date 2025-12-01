package elearningspringboot.repository;

import elearningspringboot.entity.DictationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictationTopicRepository extends JpaRepository<DictationTopic, Long> {
    List<DictationTopic> findByAuthorId(Long authorId);

}