package net.malevy.hyperdemo.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

public class Task {

    public static class Properties {
        public static final String id = "id";
        public static final String title = "title";
        public static final String description = "description";
        public static final String importance = "importance";
        public static final String due = "due";
        public static final String completedOn = "completedOn";
    }

    public enum Importance {
        LOW, NORMAL, HIGH;

        public static Optional<Importance> lookup(String s) {

            if (!StringUtils.hasText(s)) return Optional.empty();

            for (Importance entry : Importance.values()) {
                if (entry.name().equalsIgnoreCase(s)) return Optional.of(entry);
            }

            return Optional.empty();
        }

    }

    private final @Getter Integer id;
    private @Getter @Setter String title;
    private @Getter @Setter String description;
    private @Getter @Setter Importance importance;
    private @Getter @Setter LocalDate due;
    private @Getter LocalDate completedOn;

    public Task(Integer id, String title) {
        Assert.notNull(title, "must provide a title");

        this.id = id;
        this.title = title;
        this.setImportance(Importance.NORMAL);
    }

    public boolean isComplete() {
        return null != completedOn;
    }

    public void markComplete() {
        this.markComplete(LocalDate.now());
    }

    public void markComplete(LocalDate completedOn) {
        if (isComplete()) return;
        this.completedOn = completedOn;
    }

    Task(Integer id, String title, String description, Importance importance, LocalDate due, LocalDate completedOn) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.importance = importance;
        this.due = due;
        this.completedOn = completedOn;
    }
}
