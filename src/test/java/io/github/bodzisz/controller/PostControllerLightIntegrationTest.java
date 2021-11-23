package io.github.bodzisz.controller;

import io.github.bodzisz.SpringSecurityTestConfig;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.enitity.User;
import io.github.bodzisz.service.CommentService;
import io.github.bodzisz.service.PostService;
import io.github.bodzisz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PostController.class)
@ContextConfiguration(classes =
        {PostController.class, PostService.class, CommentService.class, UserService.class, SpringSecurityTestConfig.class})
public class PostControllerLightIntegrationTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;



    @Test
    @WithMockUser(username = "user")
    void httpGet_showSinglePost_returnsTaskWithGivenId() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");
        // and
        User user = new User();
        user.setUsername(userDetails.getUsername());
        // and
        String content = "testContent";
        String title = "testTitle";
        Post post = new Post();
        post.setContent(content);
        post.setTitle(title);
        post.setUser(user);


        when(postService.findById(anyInt())).thenReturn(post);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("single-post"))
                .andExpect(MockMvcResultMatchers.model().attribute("post", post))
                .andExpect(MockMvcResultMatchers.model().attributeExists("commentToAdd"));
    }
}
