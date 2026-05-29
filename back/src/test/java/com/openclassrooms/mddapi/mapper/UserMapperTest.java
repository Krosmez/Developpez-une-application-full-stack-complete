package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.response.UserProfileResponse;
import com.openclassrooms.mddapi.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper")
class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("toProfileDto : mappe les champs du profil")
    void toProfileDto() {
        User user = User.builder()
                .id(1L).email("jean@example.com").username("jean").bio("dev").password("secret")
                .build();

        UserProfileResponse dto = mapper.toProfileDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("jean@example.com");
        assertThat(dto.getUsername()).isEqualTo("jean");
        assertThat(dto.getBio()).isEqualTo("dev");
    }

    @Test
    @DisplayName("updateUserFromRequest : applique les champs non nuls")
    void updateUserFromRequest_appliesNonNull() {
        User user = User.builder().id(1L).email("old@example.com").username("old").bio("old bio").build();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@example.com");
        request.setBio("new bio");

        mapper.updateUserFromRequest(request, user);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.getBio()).isEqualTo("new bio");
        // username non fourni -> conservé
        assertThat(user.getUsername()).isEqualTo("old");
    }

    @Test
    @DisplayName("updateUserFromRequest : ignore les champs nuls")
    void updateUserFromRequest_ignoresNull() {
        User user = User.builder().id(1L).email("keep@example.com").username("keep").build();
        UpdateUserRequest request = new UpdateUserRequest();

        mapper.updateUserFromRequest(request, user);

        assertThat(user.getEmail()).isEqualTo("keep@example.com");
        assertThat(user.getUsername()).isEqualTo("keep");
        assertThat(user.getId()).isEqualTo(1L);
    }
}
