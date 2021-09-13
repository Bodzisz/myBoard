package io.github.bodzisz.controller;

import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(final PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String showPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "post-list";
    }

    @GetMapping("/{id}")
    public String showSinglePost(@PathVariable("id") int id, Model model) {
        model.addAttribute("post", postService.findById(id));
        return "single-post";
    }

    @GetMapping("/addPostForm")
    public String addPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "add-post-form";
    }

    @PostMapping("/savePost")
    public String savePost(@ModelAttribute("post") Post post) {
        postService.savePost(post);
        return "redirect:/posts";
    }
}
