package elearningspringboot.mapper;

import elearningspringboot.dto.request.FlashcardRequest;
import elearningspringboot.dto.request.FlashcardSetRequest;
import elearningspringboot.dto.response.FlashcardResponse;
import elearningspringboot.dto.response.FlashcardSetResponse;
import elearningspringboot.entity.Flashcard;
import elearningspringboot.entity.FlashcardSet;
import elearningspringboot.entity.Role; // <-- Import Role
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface FlashcardMapper {

    // ... các phương thức mapping khác ...
    @Mapping(target = "cards", source = "flashcards")
    @Mapping(target = "cardCount", expression = "java(entity.getFlashcards().size())")
    FlashcardSetResponse toDTO(FlashcardSet entity);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "flashcards", ignore = true)
    FlashcardSet toEntity(FlashcardSetRequest request);

    void updateEntity(@MappingTarget FlashcardSet entity, FlashcardSetRequest request);

    FlashcardResponse toCardDTO(Flashcard entity);

    Flashcard toCardEntity(FlashcardRequest request);

    default String map(Role role) {
        if (role == null || role.getRole() == null) {
            return null;
        }
        return role.getRole().name();
    }
}