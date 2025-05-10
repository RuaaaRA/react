package lms.step1.Request;

import lms.step1.Enumeration.ResourceType;
import lombok.Data;

@Data
public class LessonResourceRequest {
    private String title;
    private String url;
    private ResourceType type;
    private Long lessonId;
}
