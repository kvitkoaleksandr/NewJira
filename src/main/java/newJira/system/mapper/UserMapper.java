package newJira.system.mapper;

import newJira.system.dto.UserDto;
import newJira.system.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(AppUser appUser);

    AppUser toUser(UserDto userDto);
}