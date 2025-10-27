package pl.learnedge.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;

    @Column(nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


}
