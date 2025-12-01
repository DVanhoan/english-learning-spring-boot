package elearningspringboot.repository;

import elearningspringboot.entity.SpeakingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakingRoomRepository extends JpaRepository<SpeakingRoom, Long> {
    List<SpeakingRoom> findByIsActiveTrue();
}
