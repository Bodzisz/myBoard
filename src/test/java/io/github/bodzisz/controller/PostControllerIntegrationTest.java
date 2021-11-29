package io.github.bodzisz.controller;

import io.github.bodzisz.SpringSecurityTestConfig;
import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.enitity.User;
import io.github.bodzisz.repository.CommentRepository;
import io.github.bodzisz.repository.PostRepository;
import io.github.bodzisz.repository.UsersRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles({"integration", "test"})
@SpringBootTest(classes = {SpringSecurityTestConfig.class})
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository  commentRepository;

    @Value("${page.size}")
    private int pageSize;



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
    @Order(100)
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
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"))
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    @WithUserDetails
    @Order(100)
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
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("add-post-form"))
                .andExpect(model().attributeHasFieldErrors("post", "title"))
                .andExpect(model().attributeHasFieldErrors("post", "content"))
                .andExpect(model().attribute("post", hasProperty("title", is(testTitle))))
                .andExpect(model().attribute("post", hasProperty("content", is(testContent))));
    }

    @Test
    @WithUserDetails
    @Order(100)
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
                .andExpect(status().is2xxSuccessful())
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
        post.setUser(user);
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
                .andExpect(status().is3xxRedirection())
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
                .andExpect(status().is2xxSuccessful())
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
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("single-post"))
                .andExpect(model().attributeHasFieldErrors("commentToAdd", "content"))
                .andExpect(model().attribute("commentToAdd", hasProperty("content", is(commentContent))));
    }

    @Test
    @WithUserDetails(value = "user")
    void delete_notPostAuthor_userWithNoAdminRole_noAccess() throws Exception {
        // given
        User user = new User("author", "author", "password");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/delete/" + postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accessDenied"));
    }

    @Test
    @WithUserDetails(value = "user")
    void delete_postAuthorLoggedIn_deletesPost() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();
        int numberOfPostsBefore = postRepository.findAll().size();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/delete/" + postId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("deleteMessage", containsString("Post deleted")));

        assertThat(postRepository.findAll().size()).isEqualTo(numberOfPostsBefore - 1);
    }

    @Test
    @WithUserDetails(value = "admin")
    void delete_adminLoggedIn_deletesPost() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();
        int numberOfPostsBefore = postRepository.findAll().size();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/delete/" + postId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("deleteMessage", containsString("Post deleted")));

        assertThat(postRepository.findAll().size()).isEqualTo(numberOfPostsBefore - 1);
    }

    @Test
    @WithUserDetails
    void httpGet_update_noPostAuthor_noAdmin_noAccess() throws Exception {
        // given
        User user = new User("author", "author", "password");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/update/" + postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accessDenied"));
    }

    @Test
    @WithUserDetails(value = "user")
    void httpGet_update_PostAuthorAsPrincipal_returnsUpdatePostForm() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/update/" + postId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("add-post-form"))
                .andExpect(model().attribute("post", is(post)));
    }

    @Test
    @WithUserDetails(value = "admin")
    void httpGet_update_adminAsPrincipal_returnsUpdatePostForm() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post = new Post();
        post.setTitle("testPostTitle");
        post.setContent("testPostContent");
        post.setUser(user);
        int postId = postRepository.save(post).getId();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/update/" + postId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("add-post-form"))
                .andExpect(model().attribute("post", is(post)));
    }

    @Test
    @WithUserDetails(value = "user")
    void httpGet_deleteComment_NotCommentAuthor_NotAdmin_accessDenied() throws Exception {
        // given
        User user = new User("author", "author", "password");
        // and
        Post post = new Post("testPostTitle", "testPostContent", user);
        int postId = postRepository.save(post).getId();
        // and
        Comment comment = new Comment();
        comment.setContent("testCommentContent");
        comment.setUser(user);
        int commentId = commentRepository.save(comment).getId();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + postId + "/deleteComment/" + commentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accessDenied"));
    }

    @Test
    @WithUserDetails(value = "user")
    void httpGet_deleteComment_commentAuthorLoggedIn_deletesComment() throws Exception {
        // given
        User user = new User("author", "author", "password");
        // and
        Post post = new Post("testPostTitle", "testPostContent", user);
        int postId = postRepository.save(post).getId();
        // and
        Comment comment = new Comment();
        comment.setContent("testCommentContent");
        comment.setUser(usersRepository.findByUsername("user"));
        int commentId = commentRepository.save(comment).getId();
        // and
        int initialSize = commentRepository.findAll().size();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + postId + "/deleteComment/" + commentId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("single-post"))
                .andExpect(model().attribute("deleteCommentMessage", containsString("Comment deleted")))
                .andExpect(model().attribute("post", is(post)))
                .andExpect(model().attributeExists("commentToAdd"));

        assertThat(commentRepository.findAll().size()).isEqualTo(initialSize - 1);
    }

    @Test
    @WithUserDetails(value = "admin")
    void httpGet_deleteComment_adminLoggedIn_deletesComment() throws Exception {
        // given
        User user = new User("author", "author", "password");
        // and
        Post post = new Post("testPostTitle", "testPostContent", user);
        int postId = postRepository.save(post).getId();
        // and
        Comment comment = new Comment();
        comment.setContent("testCommentContent");
        comment.setUser(user);
        int commentId = commentRepository.save(comment).getId();
        // and
        int initialSize = commentRepository.findAll().size();

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + postId + "/deleteComment/" + commentId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("single-post"))
                .andExpect(model().attribute("deleteCommentMessage", containsString("Comment deleted")))
                .andExpect(model().attribute("post", is(post)))
                .andExpect(model().attributeExists("commentToAdd"));

        assertThat(commentRepository.findAll().size()).isEqualTo(initialSize - 1);
    }

    @Test
    @WithUserDetails
    void httpGet_searchByTitle_givenValidTitle_returnsAllPostContainingGivenTitle() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post1 = new Post("123", "test1", user);
        // and
        Post post2 = new Post("test123test", "test2", user);
        // and
        Post post3 = new Post("random", "test3", user);
        // and
        String givenTitle = "123";

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/search")
                .param("title", givenTitle))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("posts", hasSize(2)))
                .andExpect(model().attribute("posts", contains(post1, post2)))
                .andExpect(model().attribute("titleSearchPost", hasProperty("title", equalTo(givenTitle))))
                .andExpect(view().name("post-list"));
    }

    @Test
    @WithUserDetails
    void httpGet_searchByTitle_givenTitleThatMatchesNoPostsTitles_returnsEmptyList() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post1 = new Post("456", "test1", user);
        // and
        Post post2 = new Post("test456test", "test2", user);
        // and
        Post post3 = new Post("foo", "test3", user);
        // and
        String givenTitle = "NotMatchingTitle";

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/search")
                        .param("title", givenTitle))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("posts", hasSize(0)))
                .andExpect(model().attribute("titleSearchPost", hasProperty("title", equalTo(givenTitle))))
                .andExpect(view().name("post-list"));
    }

    @Test
    @WithUserDetails
    @Order(2)
    void httpGet_searchByTitle_givenEmptyTitle_returnsAllPosts() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post1 = new Post("789", "test1", user);
        // and
        Post post2 = new Post("test789test", "test2", user);
        // and
        Post post3 = new Post("333", "test3", user);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        // and
        String givenTitle = "";
        // and
        int numberOfAllPosts = postRepository.findAll().size();
        numberOfAllPosts = numberOfAllPosts > pageSize ? pageSize : numberOfAllPosts;

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/search")
                        .param("title", givenTitle))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("posts", hasSize(numberOfAllPosts)))
                .andExpect(model().attribute("titleSearchPost", hasProperty("title", nullValue())))
                .andExpect(view().name("post-list"));
    }

    @Test
    @WithUserDetails
    @Order(1)
    void httpGet_showPagedPosts_returnsAllPosts_sortedByAuditDesc() throws Exception {
        // given
        User user = usersRepository.findByUsername("user");
        // and
        Post post1 = new Post("post1", "test", user);
        post1.getAudit().setCreated(LocalDateTime.now().minusDays(1));
        // and
        Post post2 = new Post("post2", "test", user);
        post2.getAudit().setCreated(LocalDateTime.now());
        // and
        Post post3 = new Post("post3", "test", user);
        post3.getAudit().setCreated(LocalDateTime.now().plusDays(1));
        // and
        Post post4 = new Post("post4", "test", user);
        post4.getAudit().setCreated(LocalDateTime.now().minusDays(2));

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);

        // when + then
        mockMvc.perform(MockMvcRequestBuilders.get("/posts"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("posts", hasSize(3)))
                .andExpect(model().attribute("posts", containsInRelativeOrder(post3, post2, post1)))
                .andExpect(model().attributeExists("titleSearchPost"))
                .andExpect(model().attribute("currentPage", is(0)))
                .andExpect(model().attribute("pageNum", is(2)))
                .andExpect(view().name("post-list"));
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/page/1"))
                .andExpect(model().attribute("posts", hasSize(1)))
                .andExpect(model().attribute("posts", contains(post4)))
                .andExpect(model().attributeExists("titleSearchPost"))
                .andExpect(model().attribute("currentPage", is(1)))
                .andExpect(model().attribute("pageNum", is(2)))
                .andExpect(view().name("post-list"));

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
