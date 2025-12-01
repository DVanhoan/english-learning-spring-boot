package elearningspringboot.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class FlashcardSetResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Boolean isPublic;
    private Integer studyCount;
    private Double rating;
    private Integer ratingCount;
    private String createdAt; // String để dễ format

    private UserSummaryResponse author;
    private List<FlashcardResponse> cards; // Danh sách thẻ con
    private Integer cardCount; // Tổng số thẻ
}