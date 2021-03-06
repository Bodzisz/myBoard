package io.github.bodzisz.repository;

import io.github.bodzisz.enitity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

public interface PostRepository {

    List<Post> findAll();

    List<Post> findAll(Sort sort);

    Optional<Post> findById(int id);

    Post save(Post post);

    void deleteById(int id);

    List<Post> findAllByTitleContains(String title);

    Page<Post> findAll(Pageable pageable);

}
