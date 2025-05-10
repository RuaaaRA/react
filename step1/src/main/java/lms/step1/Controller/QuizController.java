package lms.step1.Controller;

import lms.step1.DTO.*;
import lms.step1.Exception.QuizNotFoundException;
import lms.step1.Model.User;
import lms.step1.Service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    // Endpoint لجلب الـ Quizzes حسب الـ Course
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(quizService.getQuizzesByCourseId(courseId, user));
    }

    // Endpoint للتحقق من وجود تقديم للطالب
    @GetMapping("/{quizId}/submissions/check")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Boolean> checkSubmission(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User user) {
        boolean exists = quizService.checkSubmissionExists(user.getId(), quizId);
        return ResponseEntity.ok(exists);
    }

    // Endpoint لجلب تفاصيل الـ Quiz
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDTO> getQuiz(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(quizService.getQuizDetails(quizId, user));
    }

    // Endpoint لإنشاء Quiz جديد
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<QuizDTO> createQuiz(
            @RequestBody QuizDTO quizDTO,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(quizService.createQuiz(quizDTO, user));
    }

    // Endpoint لجلب أسئلة الـ Quiz
    @GetMapping("/{quizId}/questions")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR' ,'ADMIN')")
    public ResponseEntity<List<QuestionDTO>> getQuizQuestions(
            @PathVariable Long quizId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(quizService.getQuizQuestions(quizId, user));
    }

    // Endpoint لحذف الـ Quiz
    @DeleteMapping("/{quizId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint لجلب النتائج
    @GetMapping("/{quizId}/results")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<List<SubmissionDTO>> getQuizResults(
            @PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizResults(quizId));
    }

    // Endpoint لإغلاق الـ Quiz
    @PatchMapping("/{quizId}/close")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<Void> closeQuiz(@PathVariable Long quizId) {
        quizService.closeQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint لحالة الـ Quiz
    @GetMapping("/{quizId}/status")
    public ResponseEntity<Boolean> getQuizStatus(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizStatus(quizId));
    }

    @PostMapping("/{quizId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubmissionDTO> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody List<String> answers, // يجب أن تكون مباشرة وليست داخل كائن
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(quizService.submitQuiz(user.getId(), quizId, answers));
    }

    @PutMapping("/{quizId}/questions/{questionIndex}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long quizId,
            @PathVariable int questionIndex,
            @RequestBody QuestionDTO questionDTO,
            @AuthenticationPrincipal User user) {
return ResponseEntity.noContent().build();    }







@PatchMapping("/questions/{questionId}/correct-answer")
@PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
public ResponseEntity<Void> updateCorrectAnswer(
        @PathVariable Long questionId,
        @RequestBody String newCorrectAnswer,
        @AuthenticationPrincipal User user) {
    quizService.updateCorrectAnswer(questionId, newCorrectAnswer);
    return ResponseEntity.noContent().build();
}







}