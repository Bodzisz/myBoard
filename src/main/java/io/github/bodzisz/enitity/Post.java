package io.github.bodzisz.enitity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Post title can not be empty")
    private String title;
    @NotBlank(message = "Post content can not be empty")
    private String content;
    @Embedded
    private Audit audit = new Audit();
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "post_id")
    private List<Comment> comments;

    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Audit getAudit() {
        return audit;
    }

    public LocalDateTime getCreationTime() {
        return audit == null? null : audit.getCreated();
    }

    public LocalDateTime getUpdateTime() {
        return audit == null? null : audit.getUpdated();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
