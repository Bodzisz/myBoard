package io.github.bodzisz.repository;

import io.github.bodzisz.enitity.Comment;


import java.util.List;

public interface CommentRepository {

    List<Comment> findAll();

    Comment findById(int id);

    Comment save(Comment comment);

    void deleteById(int id);
}
