package lms.step1.Model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private String correctAnswer;
    
    @ElementCollection
    private List<String> options;
    
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    
    @Column(nullable = false)
    private Integer points = 0;

    
        private String explanation;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}