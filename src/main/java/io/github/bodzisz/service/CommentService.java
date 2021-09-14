package io.github.bodzisz.service;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.repository.CommentRepository;
import io.github.bodzisz.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    private CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Transactional
    public void addComment(Post post, Comment comment) {
        comment.setId(0);
        post.addComment(comment);
        commentRepository.save(comment);
    }
}
