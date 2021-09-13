package io.github.bodzisz.enitity;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Embeddable
class Audit {
    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    void prePersits() {
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
}
