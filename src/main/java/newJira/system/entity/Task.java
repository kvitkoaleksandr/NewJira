package newJira.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

    @Entity
    @Data
    @NoArgsConstructor
    @Table(name = "tasks")
    public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String title;

        @Enumerated(EnumType.STRING)
        private Status status;

        @Enumerated(EnumType.STRING)
        private Priority priority;

        @ManyToOne
        @JoinColumn(name = "author_id")
        private AppUser author;

        @ManyToOne
        @JoinColumn(name = "executor_id")
        private AppUser executor;

        @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Comment> comments;
    }