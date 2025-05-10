package lms.step1.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "submission_id")
    private QuizSubmission submission;

    @ManyToOne
    @JoinColumn(name = "quiz_id")  
    private Quiz quiz;

    private Integer score;
}