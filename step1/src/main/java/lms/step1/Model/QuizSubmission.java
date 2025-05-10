// QuizSubmission.java
package lms.step1.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private User student;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;
    private Integer score;
    
    @Column(name = "is_submitted")
    private Boolean isSubmitted;

    @ElementCollection
    @CollectionTable(name = "submission_answers", 
                   joinColumns = @JoinColumn(name = "submission_id"))
    @Column(name = "answer")
    private List<String> answers;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL)
    @JsonIgnore
    private Grade grade;
}