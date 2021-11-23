package io.github.bodzisz;

import io.github.bodzisz.enitity.MyUserDetails;
import io.github.bodzisz.enitity.Role;
import io.github.bodzisz.enitity.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestConfiguration
public class SpringSecurityTestConfig {
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User user = new User("Basic User", "user", "password");
        List<Role> userRoles = new ArrayList<>();
        Role userRole = new Role("USER");
        userRoles.add(userRole);
        user.setRoles(userRoles);
        UserDetails userDetails = new MyUserDetails(user);

        User admin = new User("admin", "admin", "password");
        List<Role> adminRoles = new ArrayList<>();
        adminRoles.add(userRole);
        adminRoles.add(new Role("ADMIN"));
        admin.setRoles(adminRoles);
        UserDetails adminDetails = new MyUserDetails(admin);

        return new InMemoryUserDetailsManager(Arrays.asList(
                userDetails, adminDetails
        ));
    }
}
