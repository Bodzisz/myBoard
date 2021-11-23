package io.github.bodzisz.controller;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.enitity.User;
import io.github.bodzisz.repository.UsersRepository;
import io.github.bodzisz.service.CommentService;
import io.github.bodzisz.service.PostService;
import io.github.bodzisz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final UserService usersService;
    Logger logger = LoggerFactory.getLogger(PostController.class);

    public PostController(final PostService postService, CommentService commentService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.usersService = userService;
    }

    @GetMapping
    public String showPosts(Model model) {
        return this.showPagedPosts(model, 0);
    }

    // Validation of targetPage was handled by AOP - check aop.PaginationValidator class
    @GetMapping("/page/{targetPage}")
    public String showPagedPosts(Model model, @PathVariable("targetPage") int targetPage) {
        Page<Post> page = postService.findAllSortedPaged(targetPage);
        int totalPages = page.getTotalPages();

        model.addAttribute("posts", page.getContent());
        model.addAttribute("titleSearchPost", new Post());
        model.addAttribute("currentPage", targetPage);
        model.addAttribute("pageNum", totalPages);
        return "post-list";
    }


    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable("id") int id, Model model) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("commentToAdd", new Comment());
        return "single-post";
    }

    @GetMapping("/addPostForm")
    public String addPostForm(Model model, Principal principal) {
        Post newPost = new Post();
        User user = usersService.findByUsername(principal.getName());
        newPost.setUser(user);
        model.addAttribute("post", newPost);
        model.addAttribute("author", user);
        return "add-post-form";
    }

    @PostMapping("/savePost")
    public String savePost(@ModelAttribute("post") @Valid Post post,
                           BindingResult result) {
        if(result.hasErrors()) {
            return "add-post-form";
        }
        postService.savePost(post);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/saveComment")
    public String savePostComment(@ModelAttribute("commentToAdd") @Valid Comment comment,
                                  BindingResult result,
                                  @PathVariable("id") int id,
                                  Model model, Principal principal) {
        comment.setUser(usersService.findByUsername(principal.getName()));
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        if(result.hasErrors()) {
            return "single-post";
        }
        commentService.addComment(post, comment);
        return "redirect:/posts/" + id;
    }

    @PreAuthorize("hasRole('ADMIN') or principal.username.equals(@postService.findById(#id).user.username)")
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable ("id") int id , Model model) {
        model.addAttribute("post", postService.findById(id));
        return "add-post-form";
    }

    @PreAuthorize("hasRole('ADMIN') or principal.username.equals(@postService.findById(#id).user.username)")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable ("id") int id, Model model) {
        postService.deleteById(id);
        model.addAttribute("deleteMessage", "Post deleted!");
        return this.showPagedPosts(model, 0);
    }

    @PreAuthorize("hasRole('ADMIN') or principal.username.equals(@commentService.findById(#id).user.username)")
    @GetMapping("/{postId}/deleteComment/{id}")
    public String deleteComment(@PathVariable ("id") int id, @PathVariable ("postId") int postId, Model model) {
        commentService.deleteComment(id);
        model.addAttribute("deleteCommentMessage", "Comment deleted!");
        model.addAttribute("post", postService.findById(postId));
        model.addAttribute("commentToAdd", new Comment());
        return "single-post";
    }

    @GetMapping("/search")
    public String searchByTitle(@RequestParam("title") String title, Model model)
    {
        if(title.replace(" ", "").equals("")) {
            return showPosts(model);
        }
        logger.info("Search for post with title: " + title);
        model.addAttribute("posts", postService.findAllByTitle(title));
        Post searchModel = new Post();
        searchModel.setTitle(title);
        model.addAttribute("titleSearchPost", searchModel);
        return "post-list";
    }

}

