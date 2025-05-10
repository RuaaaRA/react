package lms.step1.Model;

import lms.step1.Enumeration.ResourceType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

@Data

@Entity
public class LessonResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private ResourceType type; // نوع المورد

    private String url; // رابط المورد (الملف أو الرابط)

    @ManyToOne
    private Lesson lesson; // الربط مع الدرس

    // Constructor افتراضي (بدون معطيات)
   
   

    public LessonResource(String title, ResourceType type, String url, Lesson lesson) {
        this.title = title;
        this.type = type;
        this.url = url;
        this.lesson = lesson;
    }
    public LessonResource() {
    }
        
}
