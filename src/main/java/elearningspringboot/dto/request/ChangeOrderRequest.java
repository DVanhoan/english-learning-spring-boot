package elearningspringboot.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeOrderRequest {
    private Long id;
    private Integer orderIndex;
}
