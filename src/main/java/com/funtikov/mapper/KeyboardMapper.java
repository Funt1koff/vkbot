package com.funtikov.mapper;

import com.funtikov.dto.keyboard.*;
import com.funtikov.entity.keyboard.*;
import org.mapstruct.*;
import java.util.List;

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


    // ==== Button ====

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "command",        source = "command")
    @Mapping(target = "buttonResponse", source = "response")
    // parentPage и nextKeyboardPage будем «подставлять» уже в сервисе
    @Mapping(target = "parentPage",     ignore = true)
    @Mapping(target = "nextKeyboardPage", ignore = true)
    Button toEntity(ButtonDto dto);

    List<Button> toEntityButtonList(List<ButtonDto> dtoList);


    // ==== ButtonResponse ====

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "text",   source = "text")
    // поле media заполним руками (через UploadPhotoService), поэтому игнорируем
    @Mapping(target = "media",  ignore = true)
    ButtonResponse toEntity(ButtonResponseDto dto);


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
}
