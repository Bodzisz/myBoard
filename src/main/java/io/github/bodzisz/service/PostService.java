package io.github.bodzisz.service;

import io.github.bodzisz.enitity.Comment;
import io.github.bodzisz.enitity.Post;
import io.github.bodzisz.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;
    @Value("${page.size}")
    private int pageSize;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> findAllSorted() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "Audit.created"));
    }

    public Page<Post> findAllSortedPaged(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "Audit.created"));
        return repository.findAll(pageable);
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

    public List<Post> findAllByTitle(String title) {
        return repository.findAllByTitleContains(title);
    }
}
