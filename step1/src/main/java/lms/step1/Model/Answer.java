package lms.step1.Model;

import java.util.List;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Quiz quiz;

    @ElementCollection
    @CollectionTable(name = "answer_texts", joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "answer_text")
    private List<String> answerTexts;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
}
