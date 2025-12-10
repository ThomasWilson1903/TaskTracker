package ru.twilson.tasktracker.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.model.RegisterRequest;
import ru.twilson.tasktracker.model.UserResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConsumerMapper {

    Consumer toConsumer(RegisterRequest registerRequest);

    @Mapping(target = "id", source = "globalId")
    UserResponse toUserResponse(Consumer consumer);
}
