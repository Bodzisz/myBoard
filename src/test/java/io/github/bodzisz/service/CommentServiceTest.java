package io.github.bodzisz.service;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    @Test
    @DisplayName("should save given comment to repository and post comments")
    void addComment_savesCommentToRepo() {
        // given
        Comment commentToSave = new Comment();
        // and
        inMemoryCommentRepository commentRepository = new inMemoryCommentRepository();
        // and
        Post post = new Post();
        // and
        CommentService commentService = new CommentService(commentRepository);
        int countBeforeCall = commentRepository.count();

        // when
        commentService.addComment(post, commentToSave);

        // then
        assertThat(post.getComments().contains(commentToSave)).isTrue();
        assertThat(countBeforeCall + 1).isEqualTo(commentRepository.count());
    }

    private static class inMemoryCommentRepository implements CommentRepository {
        private Map<Integer, Comment> map = new HashMap<>();
        private int index = 0;

        public int count() {
            return map.values().size();
        }

        @Override
        public List<Comment> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Comment findById(int id) {
            return map.get(id);
        }

        @Override
        public Comment save(Comment comment) {
            return map.put(++index, comment);
        }

        @Override
        public void deleteById(int id) {
            map.remove(id);
        }
    }
}
