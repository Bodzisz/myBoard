package io.github.bodzisz.controller;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.service.CommentService;
import io.github.bodzisz.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
    Logger logger = LoggerFactory.getLogger(PostController.class);

    public PostController(final PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String showPosts(Model model) {
        setPostListModelAttributes(model);
        return this.showPagedPosts(model, 0);
    }

    // Validation of targetPage was handled by AOP - check aop.PageRequestValidator class
    @GetMapping("/page/{targetPage}")
    public String showPagedPosts(Model model, @PathVariable("targetPage") int targetPage) {
//        // requested page < 0
//        if(targetPage < 0) {
//            model.addAttribute("invalidPageMessage", "Page does not exist!");
//            return showPagedPosts(model, 0);
//        }

        Page<Post> page = postService.findAllSortedPaged(targetPage);
        int totalPages = page.getTotalPages();

//        // requested page number is bigger than number of pages
//        if(targetPage >= totalPages) {
//            model.addAttribute("invalidPageMessage", "Page does not exist!");
//            return showPagedPosts(model, totalPages - 1);
//        }

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
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        if(result.hasErrors()) {
            return "single-post";
        }
        commentService.addComment(post, comment);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable ("id") int id , Model model) {
        model.addAttribute("post", postService.findById(id));
        return "add-post-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable ("id") int id, Model model) {
        postService.deleteById(id);
        model.addAttribute("deleteMessage", "Post deleted!");
        setPostListModelAttributes(model);
        return "post-list";
    }

    @GetMapping("/{postId}/deleteComment/{id}")
    public String deleteComment(@PathVariable ("id") int id, @PathVariable ("postId") int postId, Model model) {
        commentService.deleteComment(id);
        model.addAttribute("deleteCommentMessage", "Comment deleted!");
        model.addAttribute("post", postService.findById(postId));
        model.addAttribute("commentToAdd", new Comment());
        return "single-post";
    }

    @GetMapping("/search")
    public String searchByTitle(@RequestParam("title") String title, Model model) {
        logger.info("Search for post with title: " + title);
        model.addAttribute("posts", postService.findAllByTitle(title));
        Post searchModel = new Post();
        searchModel.setTitle(title);
        model.addAttribute("titleSearchPost", searchModel);
        return "post-list";
    }

    private void setPostListModelAttributes(Model model) {
        model.addAttribute("posts", postService.findAllSortedPaged(0));
        model.addAttribute("titleSearchPost", new Post());
        model.addAttribute("currentPage", 0);
    }
}
