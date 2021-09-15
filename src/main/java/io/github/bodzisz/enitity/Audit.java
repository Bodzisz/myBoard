package io.github.bodzisz.enitity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Embeddable
class Audit implements Comparable {
    @Column(updatable = false)
    private LocalDateTime created;
    private LocalDateTime updated;


    @PrePersist
    void prePersist() {
        created = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updated = LocalDateTime.now();
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }


    @Override
    public int compareTo(Object o) {
        if(o instanceof Audit) {
            return created.compareTo(((Audit) o).created);
        }
        return -1;
    }
}
