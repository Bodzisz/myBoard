package io.github.bodzisz.controller;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.service.CommentService;
import io.github.bodzisz.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    public PostController(final PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String showPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "post-list";
    }

    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable("id") int id, Model model) {
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("commentToAdd", new Comment());
        return "single-post";
    }

    @GetMapping("/addPostForm")
    public String addPostForm(Model model) {
        model.addAttribute("post", new Post());
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
                                  Model model) {
        model.addAttribute("post", postService.findById(id));
        if(result.hasErrors()) {
            return "single-post";
        }
        commentService.addComment(id, comment);
        return "redirect:/posts/" + id;
    }
}
