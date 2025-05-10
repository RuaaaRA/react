package lms.step1.Controller;

import lms.step1.Service.AutoGradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/grading")
@RequiredArgsConstructor
@Slf4j
public class AutoGradingController {

    private final AutoGradingService autoGradingService;

  

    @PreAuthorize("hasAnyAuthority('STUDENT','INSTRUCTOR','ADMIN')")
@PostMapping("/grade/{quizId}")
public ResponseEntity<Integer> gradeQuiz(@PathVariable Long quizId, @RequestBody Map<Long, String> userAnswers) {
    log.info("📝 Grading quiz with ID: {} for {} answers submitted", quizId, userAnswers.size());

    int score = autoGradingService.gradeQuiz(quizId, userAnswers);

    log.info("✅ Quiz ID {} graded. Final score: {}", quizId, score);

    return ResponseEntity.ok(score); // Return score to client
}

}