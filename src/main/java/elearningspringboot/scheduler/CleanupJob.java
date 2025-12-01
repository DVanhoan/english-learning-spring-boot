package elearningspringboot.scheduler;

import elearningspringboot.enumeration.Status;
import elearningspringboot.repository.UserRepository;
import elearningspringboot.repository.WhitelistTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CleanupJob {

    private final WhitelistTokenRepository whitelistTokenRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void cleanupToken() {
        whitelistTokenRepository.deleteByExpiredToken(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void cleanupExpiredPendingUser() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.minusDays(1);
        userRepository.deleteExpiredPendingUsers(Status.PENDING, expiredAt);
    }
}
