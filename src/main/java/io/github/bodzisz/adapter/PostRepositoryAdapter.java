package io.github.bodzisz.adapter;

import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.repository.PostRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepositoryAdapter extends JpaRepository<Post, Integer>, PostRepository {
}
