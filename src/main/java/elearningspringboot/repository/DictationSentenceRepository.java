package elearningspringboot.repository;

import elearningspringboot.entity.DictationSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictationSentenceRepository extends JpaRepository<DictationSentence, Long> {}