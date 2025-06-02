package com.funtikov.mapper;

import com.funtikov.dto.keyboard.*;
import com.funtikov.dto.keyboard.response.ButtonRequestResponseDto;
import com.funtikov.dto.keyboard.response.ButtonResponseRequestReponseDto;
import com.funtikov.dto.keyboard.response.KeyboardPageRequestResponseDto;
import com.funtikov.dto.keyboard.response.MediaResponseDto;
import com.funtikov.entity.AuditableEntity;
import com.funtikov.entity.keyboard.*;
import org.mapstruct.*;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "cdi",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface KeyboardMapper {

    // ==== KeyboardPage ====

    @BeanMapping( // все неявные поля — игнорировать по умолчанию
            ignoreByDefault = true
    )
    @Mapping(target = "startPage",    source = "startPage")
    @Mapping(target = "pageButtons",  source = "pageButtons")
    KeyboardPage toEntity(KeyboardPageDto dto);

    List<KeyboardPage> toEntity(List<KeyboardPageDto> dtoList);

    default KeyboardPageRequestResponseDto toResponseDto(KeyboardPage entity) {
        return KeyboardPageRequestResponseDto.builder()
                .id(entity.getId())
                .startPage(entity.isStartPage())
                .pageButtonIds(entity.getPageButtons().stream().map(AuditableEntity::getId).collect(Collectors.toList()))
                .build();
    }

    default List<KeyboardPageRequestResponseDto> toResponseDtoList(List<KeyboardPage> entities) {
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }


    // ==== Button ====

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "command",        source = "command")
    @Mapping(target = "buttonResponse", source = "response")
    // parentPage и nextKeyboardPage будем «подставлять» уже в сервисе
    @Mapping(target = "parentPage",     ignore = true)
    @Mapping(target = "nextKeyboardPage", ignore = true)
    Button toEntity(ButtonDto dto);

    List<Button> toEntityButtonList(List<ButtonDto> dtoList);

    default ButtonRequestResponseDto toResponseDto(Button button) {
        return ButtonRequestResponseDto.builder()
                .id(button.getId())
                .command(button.getCommand())
                .buttonResponseId(button.getButtonResponse().getId())
                .parentKeyboardPageId(button.getParentPage().getId())
                .nextKeyboardPageId(button.getNextKeyboardPage() == null ? null : button.getNextKeyboardPage().getId())
                .build();
    }


    // ==== ButtonResponse ====

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "text",   source = "text")
    // поле media заполним руками (через UploadPhotoService), поэтому игнорируем
    @Mapping(target = "media",  ignore = true)
    ButtonResponse toEntity(ButtonResponseDto dto);

    default ButtonResponseRequestReponseDto toButtonResponseDto(ButtonResponse buttonResponse) {
        return ButtonResponseRequestReponseDto.builder()
                .id(buttonResponse.getId())
                .text(buttonResponse.getText())
                .mediaIds(buttonResponse.getMedia().stream().map(Media::getId).collect(Collectors.toList()))
                .build();
    }


    // ==== Media ====

    @BeanMapping(ignoreByDefault = true)
    // единственное поле, которое маппится из dto
    @Mapping(target = "url",    source = "url")
    // всё остальное (id, attachment, связи) — в сервисе
    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "attachment",       ignore = true)
    @Mapping(target = "buttonResponse",   ignore = true)
    Media toEntity(MediaDto dto);

    List<Media> toEntityMediaList(List<MediaDto> dtoList);

    default MediaResponseDto toMediaResponseDto(Media media) {
        return MediaResponseDto.builder()
                .id(media.getId())
                .url(media.getUrl())
                .orderIndex(media.getOrderIndex())
                .currentButtonResponseId(media.getButtonResponse().getId())
                .build();
    }

    default List<MediaResponseDto> toMediaResponseDtoList(List<Media> mediaList) {
        return mediaList.stream()
                .map(this::toMediaResponseDto)
                .toList();
    }
}
