package lms.step1.Model;

import lombok.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
// في حزمة lms.step1.Model

import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions")
@Data
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // أو أي نموذج تمثل الطلاب لديك

    @Column(columnDefinition = "TEXT")
    private String solutionText;

    private String solutionFilePath;
    
    private LocalDateTime submissionDate;
    
    private Integer grade;
    
    // Constructors, getters, setters سيتم توليدها تلقائياً بواسطة @Data
}