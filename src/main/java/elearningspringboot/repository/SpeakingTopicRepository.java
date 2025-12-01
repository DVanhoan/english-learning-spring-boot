package elearningspringboot.repository;

import elearningspringboot.entity.SpeakingTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeakingTopicRepository extends JpaRepository<SpeakingTopic, Long> {
}
