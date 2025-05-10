package lms.step1.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private int maxScore;
    private String filePath;


    @ManyToOne
    
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    @JsonBackReference
    private Course course;
}
