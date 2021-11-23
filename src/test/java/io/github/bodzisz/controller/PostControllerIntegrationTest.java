package io.github.bodzisz.controller;

import io.github.bodzisz.SpringSecurityTestConfig;
import io.github.bodzisz.TestConfig;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.enitity.User;
import io.github.bodzisz.repository.PostRepository;
import io.github.bodzisz.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ActiveProfiles({"integration"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringSecurityTestConfig.class)
@AutoConfigureMockMvc
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @WithUserDetails
    void httpGet_showSinglePost_returnViewToPostWithGivenId() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        String content = "testContent";
        String title = "testTitle";
        Post post = new Post();
        post.setContent(content);
        post.setTitle(title);
        post.setUser(user);
        // and
        int id = postRepository.save(post).getId();


        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + id))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("single-post"))
                .andExpect(MockMvcResultMatchers.model().attribute("post", post))
                .andExpect(MockMvcResultMatchers.model().attributeExists("commentToAdd"));
    }

    @Test
    @WithUserDetails(value = "user")
    void httpGet_addPostForm_returnsAddPostFormView_modelHasPrincipalAsPostUser() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/addPostForm"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.view().name("add-post-form"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("post"))
                .andExpect(MockMvcResultMatchers.model().attribute("author", user));
    }

}
