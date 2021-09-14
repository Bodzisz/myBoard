package io.github.bodzisz.service;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> findAll() {
        return repository.findAll();
    }

    public Post findById(int id) {
        return (repository.findById(id)).orElseThrow(() -> new IllegalArgumentException("Post with given id not found " + id));
    }

    public void savePost(Post post) {
        repository.save(post);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
