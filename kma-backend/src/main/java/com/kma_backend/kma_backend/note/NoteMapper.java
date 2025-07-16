package com.kma_backend.kma_backend.note;

import com.kma_backend.kma_backend.user.User;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    NoteDTO toDTO(Note note);
    Note toEntity(NoteDTO noteDTO);
    List<NoteDTO> toDTOList(List<Note> notes);
}
