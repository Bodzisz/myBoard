package io.github.bodzisz.service;

import io.github.bodzisz.enitity.User;
import io.github.bodzisz.enitity.dto.UserWriteModel;
import io.github.bodzisz.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

public class MyUserDetailServiceTest {

    @Test
    @DisplayName("should throw UsernameNotFoundException when given invalid username")
    void loadByUsername_invalidUsername_throwsUsernameNotFoundException() {
        // given
        UsersRepository repository = mock(UsersRepository.class);
        when(repository.findAllByUsername(anyString())).thenReturn(Optional.empty());
        // and
        MyUsersDetailService service = new MyUsersDetailService(repository);

        // when
        Throwable exception = catchThrowable(() -> service.loadUserByUsername("invalidUsername"));
        // then
        assertThat(exception)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Username not found");
    }

    @Test
    @DisplayName("should return mapped UserDetails object")
    void loadByUsername_validUsername_returnsMappedMyUserDetailsObject() {
        // given
        UsersRepository repository = mock(UsersRepository.class);
        when(repository.findAllByUsername("bar")).thenReturn(Optional.of(new User(
                new UserWriteModel("foo", "bar", "password", "password"))));
        // and
        MyUsersDetailService service = new MyUsersDetailService(repository);

        // when
        UserDetails returnObject = service.loadUserByUsername("bar");

        // then
        assertThat(returnObject)
                .hasFieldOrPropertyWithValue("name", "foo")
                .hasFieldOrPropertyWithValue("username", "bar")
                .hasFieldOrProperty("password");
    }
}
