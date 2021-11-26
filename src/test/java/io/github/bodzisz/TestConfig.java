package io.github.bodzisz;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.enitity.Role;
import io.github.bodzisz.enitity.User;
import io.github.bodzisz.repository.CommentRepository;
import io.github.bodzisz.repository.PostRepository;
import io.github.bodzisz.repository.RoleRepository;
import io.github.bodzisz.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class TestConfig {

    @Autowired
    UserDetailsService userDetailsService;

    @Value("${page.size}")
    private int pageSize;

    @Bean
    @Primary
    @Profile("integration")
    DataSource e2eTestDataSource() {
        var result = new DriverManagerDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        result.setDriverClassName("org.h2.Driver");
        return result;
    }

    @Bean
    @Primary
    @Profile("integration")
    PostRepository testPostRepo() {
        return new PostRepository() {
            private final Map<Integer, Post> map = new HashMap<>();

            @Override
            public List<Post> findAll() {
                return new ArrayList<>(map.values());
            }

            @Override
            public List<Post> findAll(Sort sort) {
                return map.values().stream()
                        .sorted(Comparator.comparing(Post::getCreationTime))
                        .collect(Collectors.toList());
            }

            @Override
            public Optional<Post> findById(int id) {
                return Optional.of(map.get(id));
            }

            @Override
            public Post save(Post post) {
                int key = map.size() + 1;
                post.setId(key);
                map.put(key, post);
                return map.get(key);
            }

            @Override
            public void deleteById(int id) {
                map.remove(id);
            }

            @Override
            public List<Post> findAllByTitleContains(String title) {
                return map.values().stream()
                        .filter(post -> post.getTitle().contains(title))
                        .collect(Collectors.toList());
            }

            @Override
            public Page<Post> findAll(Pageable pageable) {
                List<Post> list = new ArrayList<>(map.values());
                list = list.stream()
                        .sorted((o1, o2) -> {
                            if(o1.getAudit().getCreated() == null || o2.getAudit().getCreated() == null) {
                                return 1;
                            }
                            else {
                                return o1.getAudit().getCreated().compareTo(o2.getAudit().getCreated());
                            }
                        })
                        .collect(Collectors.toCollection(ArrayList::new));
                Collections.reverse(list);
                int from = pageSize * (pageable.getPageNumber());
                int to = from + pageSize;
                if(from >= list.size()) {
                    from = 0;
                    to = list.size() - 1;
                }
                else if(to >= list.size()) {
                    to = list.size();
                }
                list = list.subList(from, to);
                Page<Post> page = new PageImpl<Post>(list,
                        pageable,
                        map.size());
                return page;
            }
        };
    }

    @Bean
    @Primary
    @Profile("integration")
    CommentRepository testCommentRepo() {
        return new CommentRepository() {
            private final Map<Integer, Comment> map = new HashMap<>();

            @Override
            public List<Comment> findAll() {
                return new ArrayList<>(map.values());
            }

            @Override
            public Comment findById(int id) {
                return map.get(id);
            }

            @Override
            public Comment save(Comment comment) {
                int key = map.size() + 1;
                comment.setId(key);
                map.put(key, comment);
                return map.get(key);
            }

            @Override
            public void deleteById(int id) {
                map.remove(id);
            }
        };
    }

    @Bean
    @Primary
    @Profile("integration")
    UsersRepository testUsersRepo() {
        UsersRepository myUsersRepo = new UsersRepository() {
            private final Map<String, User> map = new HashMap<>();

            @Override
            public User findByUsername(String username) {
                return map.get(username);
            }

            @Override
            public Optional<User> findAllByUsername(String username) {
                return Optional.of(map.get(username));
            }

            @Override
            public User save(User user) {
                map.put(user.getUsername(), user);
                return map.get(user.getUsername());
            }

            @Override
            public boolean existsByUsername(String username) {
                return map.containsKey(username);
            }
        };
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");
        UserDetails adminDetails = userDetailsService.loadUserByUsername("admin");

        myUsersRepo.save(new User("Test Name", userDetails.getUsername(), userDetails.getPassword()));
        myUsersRepo.save(new User("Test Name", adminDetails.getUsername(), adminDetails.getPassword()));
        return myUsersRepo;
    }

    @Bean
    @Primary
    @Profile("integration")
    RoleRepository testRoleRepo() {
        RoleRepository myRoleRepo = new RoleRepository() {
            private final Map<String, Role> map = new HashMap<>();

            @Override
            public Role findRoleByRole(String role) {
                return map.get(role);
            }

            @Override
            public Role save(Role role) {
                map.put(role.getRole(), role);
                return map.get(role.getRole());
            }
        };
        userDetailsService.loadUserByUsername("admin").getAuthorities()
                .forEach(role -> myRoleRepo.save(new Role(role.getAuthority())));
        return myRoleRepo;
    }
}

