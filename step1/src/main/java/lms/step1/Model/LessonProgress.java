package lms.step1.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentUsername;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    private boolean completed;
}
