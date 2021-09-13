package io.github.bodzisz.service;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.repository.CommentRepository;
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
    public void addComment(int postId, Comment comment) {
        comment.setId(0);
        comment.setPostId(postId);
        commentRepository.save(comment);
    }
}
