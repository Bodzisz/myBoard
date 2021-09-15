package io.github.bodzisz.enitity;

import org.springframework.stereotype.Controller;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Comment's content can not be empty")
    @Size(max=500, message ="Post content can contain maximum of 500 characters")
    private String content;
    @Embedded
    private Audit audit = new Audit();

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public LocalDateTime getCreationTime() {
        return audit == null? null : audit.getCreated();
    }
}
