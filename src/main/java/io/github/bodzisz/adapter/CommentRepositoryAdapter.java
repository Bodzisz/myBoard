package io.github.bodzisz.adapter;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.repository.CommentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepositoryAdapter extends JpaRepository<Comment, Integer>, CommentRepository {
}
