package io.github.bodzisz.controller;

import io.github.bodzisz.SpringSecurityTestConfig;
import io.github.bodzisz.TestConfig;
import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.enitity.User;
import io.github.bodzisz.repository.PostRepository;
import io.github.bodzisz.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"integration"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {SpringSecurityTestConfig.class})
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
                .andExpect(MockMvcResultMatchers.model().attribute("post", hasProperty("user", is(user))));
    }

    @Test
    @WithUserDetails(value = "user")
    void httpPost_savePost_validPost_returnsView_hasStatusIsCreated() throws Exception {
        // given
        String testTitle = "testTitle";
        String testContent = "testContent";
        // and
        User user = usersRepository.findByUsername("user");

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/savePost")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", testTitle)
                        .param("content", testContent)
                        .sessionAttr("post", new Post())
                )
                .andExpect(status().isCreated())
                .andExpect(view().name("redirect:/posts"))
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    @WithUserDetails
    void httpPost_savePost_invalidPost_blankFields_hasErrors_returnsAddFormView() throws Exception {
        // given
        String testTitle = "";
        String testContent = "";

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/savePost")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", testTitle)
                        .param("content", testContent)
                .sessionAttr("post", new Post())
                )
                .andExpect(status().isCreated())
                .andExpect(view().name("add-post-form"))
                .andExpect(model().attributeHasFieldErrors("post", "title"))
                .andExpect(model().attributeHasFieldErrors("post", "content"))
                .andExpect(model().attribute("post", hasProperty("title", is(testTitle))))
                .andExpect(model().attribute("post", hasProperty("content", is(testContent))));
    }

    @Test
    @WithUserDetails
    void httpPost_savePost_invalidPost_oversizedFields_hasErrors_returnsAddPostFormView() throws Exception {
        // given
        String testTitle = generateString(101);
        String testContent = generateString(5001);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/savePost")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", testTitle)
                        .param("content", testContent)
                        .sessionAttr("post", new Post())
                )
                .andExpect(status().isCreated())
                .andExpect(view().name("add-post-form"))
                .andExpect(model().attributeHasFieldErrors("post", "title"))
                .andExpect(model().attributeHasFieldErrors("post", "content"))
                .andExpect(model().attribute("post", hasProperty("title", is(testTitle))))
                .andExpect(model().attribute("post", hasProperty("content", is(testContent))));
    }

    @Test
    @WithUserDetails(value = "user")
    void httpPost_saveComment_validComment_savesComment_withPrincipalAsUser_redirectsToCommentPost() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        int postId = postRepository.save(post).getId();
        // and
        Comment comment = new Comment();
        String commentContent = "testCommentContent";

        // when + then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/posts/" + postId + "/saveComment")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .param("content", commentContent)
                .sessionAttr("commentToAdd", comment)
                .sessionAttr("id", postId))
                .andExpect(status().isCreated())
                .andExpect(view().name("redirect:/posts/" + postId))
                .andExpect(redirectedUrl("/posts/" + postId))
                .andReturn();

        Comment savedComment = post.getComments().get(post.getComments().size()-1);
        assertThat(savedComment).hasFieldOrPropertyWithValue("content", commentContent);
        assertThat(savedComment).hasFieldOrPropertyWithValue("user", user);
    }

    @Test
    @WithUserDetails
    void httpPost_saveComment_invalidComment_blankContent_hasErrors_returnsPostView() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();
        // and
        Comment comment = new Comment();
        String commentContent = "";

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/" + postId + "/saveComment")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("content", commentContent)
                        .sessionAttr("commentToAdd", comment)
                        .sessionAttr("id", postId))
                .andExpect(status().isCreated())
                .andExpect(view().name("single-post"))
                .andExpect(model().attributeHasFieldErrors("commentToAdd", "content"))
                .andExpect(model().attribute("commentToAdd", hasProperty("content", is(commentContent))));
    }

    @Test
    @WithUserDetails
    void httpPost_saveComment_invalidComment_oversizedContent_hasErrors_returnsPostView() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();
        // and
        Comment comment = new Comment();
        String commentContent = generateString(501);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/" + postId + "/saveComment")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("content", commentContent)
                        .sessionAttr("commentToAdd", comment)
                        .sessionAttr("id", postId))
                .andExpect(status().isCreated())
                .andExpect(view().name("single-post"))
                .andExpect(model().attributeHasFieldErrors("commentToAdd", "content"))
                .andExpect(model().attribute("commentToAdd", hasProperty("content", is(commentContent))));
    }

    public static String generateString(int n) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++) {
            sb.append((char) (random.nextInt(255)));
        }
        return sb.toString();
    }

}
