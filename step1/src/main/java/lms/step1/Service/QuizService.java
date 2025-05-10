package lms.step1.Service;

import lms.step1.DTO.*;
import lms.step1.Enumeration.Role;
import lms.step1.Exception.*;
import lms.step1.Model.*;
import lms.step1.Repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final QuestionRepository questionRepository;

    // ============================== Main Methods ==============================

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesByCourseId(Long courseId, User user) {
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);
        if (quizzes.isEmpty()) {
            throw new QuizNotFoundException("No quizzes found for course ID: " + courseId);
        }
        return quizzes.stream()
                .map(quiz -> convertToDTO(quiz, user))
                .collect(Collectors.toList());
    }

    @Transactional
    public QuizDTO createQuiz(QuizDTO quizDTO, User instructor) {
        Course course = courseRepository.findById(quizDTO.courseId())
                .orElseThrow(() -> new CourseNotFoundException(quizDTO.courseId()));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.title());
        quiz.setDescription(quizDTO.description());
        quiz.setCourse(course);
        quiz.setIsClosed(false);
        quiz.setOpenTime(quizDTO.startTime());
        quiz.setCloseTime(quizDTO.endTime());

        List<Question> questions = quizDTO.questions().stream()
                .map(this::convertToQuestion)
                .peek(q -> q.setQuiz(quiz))
                .collect(Collectors.toList());

        quiz.setQuestions(questions);
        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToDTO(savedQuiz, instructor);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizDetails(Long quizId, User currentUser) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        return convertToDTO(quiz, currentUser);
    }

    @Transactional
    public SubmissionDTO submitQuiz(Long studentId, Long quizId, List<String> answers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        LocalDateTime now = LocalDateTime.now();
        validateQuizStatus(quiz, now, quizId);

        if (submissionRepository.existsByStudentIdAndQuizId(studentId, quizId)) {
            throw new DuplicateSubmissionException(studentId, quizId);
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));

        validateSubmission(quiz, answers);
        int score = calculateScore(quiz, answers);

        QuizSubmission submission = buildSubmission(student, quiz, answers, score);
        QuizSubmission savedSubmission = submissionRepository.save(submission);

        createGradeRecord(savedSubmission);
        return convertToSubmissionDTO(savedSubmission);
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuizQuestions(Long quizId, User user) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        return quiz.getQuestions().stream()
                .map(q -> new QuestionDTO(
                        q.getId(), // إضافة معرف السؤال

                        q.getText(),
                        user.getRole() == Role.INSTRUCTOR ? q.getCorrectAnswer() : null,
                        q.getOptions(),
                        q.getQuestionType(),
                        q.getPoints() != null ? q.getPoints() : 0,
                        q.getExplanation()))

                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new QuizNotFoundException(quizId);
        }
        quizRepository.deleteById(quizId);
    }

    @Transactional(readOnly = true)
    public List<SubmissionDTO> getQuizResults(Long quizId) {
        return submissionRepository.findByQuizId(quizId).stream()
                .map(this::convertToSubmissionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void closeQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        quiz.setIsClosed(true);
        quizRepository.save(quiz);
    }

    @Transactional(readOnly = true)
    public Boolean getQuizStatus(Long quizId) {
        return quizRepository.findById(quizId)
                .map(quiz -> {
                    LocalDateTime now = LocalDateTime.now();
                    return quiz.getIsClosed()
                            || (quiz.getCloseTime() != null && now.isAfter(quiz.getCloseTime()));
                })
                .orElseThrow(() -> new QuizNotFoundException(quizId));
    }

    @Transactional(readOnly = true)
    public boolean checkSubmissionExists(Long studentId, Long quizId) {
        return submissionRepository.existsByStudentIdAndQuizId(studentId, quizId);
    }

    // ============================= Helper Methods =============================

    private void validateQuizStatus(Quiz quiz, LocalDateTime now, Long quizId) {
        if (quiz.getIsClosed()) {
            throw new QuizClosedException(quizId);
        }
        if (quiz.getOpenTime() != null && now.isBefore(quiz.getOpenTime())) {
            throw new QuizNotStartedException(quizId);
        }
        if (quiz.getCloseTime() != null && now.isAfter(quiz.getCloseTime())) {
            throw new QuizEndedException(quizId);
        }
    }

    private void validateSubmission(Quiz quiz, List<String> answers) {
        if (answers.size() != quiz.getQuestions().size()) {
            throw new InvalidSubmissionException("Number of answers doesn't match questions");
        }

        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            Question question = quiz.getQuestions().get(i);
            String answer = answers.get(i);

            if (!question.getOptions().contains(answer)) {
                throw new InvalidSubmissionException("Invalid answer for question " + (i + 1));
            }
        }
    }

    private int calculateScore(Quiz quiz, List<String> answers) {
        int correctAnswers = 0;
        List<Question> questions = quiz.getQuestions();

        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectAnswer().equalsIgnoreCase(answers.get(i).trim())) {
                correctAnswers++;
            }
        }
        return (correctAnswers * 100) / questions.size();
    }

    private QuizSubmission buildSubmission(User student, Quiz quiz, List<String> answers, int score) {
        return QuizSubmission.builder()
                .student(student)
                .quiz(quiz)
                .submissionDate(LocalDateTime.now())
                .score(score)
                .isSubmitted(true)
                .answers(answers)
                .build();
    }

    private void createGradeRecord(QuizSubmission submission) {
        Grade grade = new Grade();
        grade.setSubmission(submission);
        grade.setQuiz(submission.getQuiz());
        grade.setScore(submission.getScore());
        gradeRepository.save(grade);
    }

    private SubmissionDTO convertToSubmissionDTO(QuizSubmission submission) {
        return new SubmissionDTO(
                submission.getStudent().getId(),
                submission.getStudent().getUsername(),
                submission.getSubmissionDate(),
                submission.getScore(), null);
    }

    private Question convertToQuestion(QuestionDTO dto) {
        Question question = new Question();
        question.setText(dto.text());
        question.setCorrectAnswer(dto.correctAnswer());
        question.setOptions(dto.options());
        question.setQuestionType(dto.questionType());
        question.setPoints(dto.points());
        question.setExplanation(dto.explanation());
        return question;
    }

    private void validateSubmission(Quiz quiz, Map<Long, String> answers) {
        if (answers.size() != quiz.getQuestions().size()) {
            throw new InvalidSubmissionException("Number of answers doesn't match questions");
        }

        for (Question question : quiz.getQuestions()) {
            String answer = answers.get(question.getId());

            if (answer == null || answer.trim().isEmpty()) {
                throw new InvalidSubmissionException("Answer for question " + question.getId() + " is empty");
            }

            if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE &&
                    !question.getOptions().contains(answer)) {
                throw new InvalidSubmissionException("Invalid answer for multiple choice question " + question.getId());
            }

            if (question.getQuestionType() == QuestionType.TRUE_FALSE &&
                    !(answer.equalsIgnoreCase("True") || answer.equalsIgnoreCase("False"))) {
                throw new InvalidSubmissionException("Invalid answer for true/false question " + question.getId());
            }
        }
    }

    @Transactional
    public SubmissionDTO submitQuiz(Long studentId, Long quizId, Map<Long, String> answers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        LocalDateTime now = LocalDateTime.now();
        validateQuizStatus(quiz, now, quizId);

        if (submissionRepository.existsByStudentIdAndQuizId(studentId, quizId)) {
            throw new DuplicateSubmissionException(studentId, quizId);
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));

        validateSubmission(quiz, answers);
        int score = calculateScore(quiz, answers);

        QuizSubmission submission = QuizSubmission.builder()
                .student(student)
                .quiz(quiz)
                .submissionDate(LocalDateTime.now())
                .score(score)
                .isSubmitted(true)
                .answers(answers.values().stream().toList())
                .build();

        QuizSubmission savedSubmission = submissionRepository.save(submission);

        createGradeRecord(savedSubmission);
        return convertToSubmissionDTO(savedSubmission);
    }

    private int calculateScore(Quiz quiz, Map<Long, String> answers) {
        int correctAnswers = 0;

        for (Question question : quiz.getQuestions()) {
            String submittedAnswer = answers.get(question.getId());
            if (submittedAnswer != null && question.getCorrectAnswer().equalsIgnoreCase(submittedAnswer.trim())) {
                correctAnswers++;
            }
        }

        return (correctAnswers * 100) / quiz.getQuestions().size();
    }

    public void updateQuestion(Long quizId, int questionIndex, QuestionDTO questionDTO, User user) {
        // Optionally use 'user' here
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        if (questionIndex < 0 || questionIndex >= quiz.getQuestions().size()) {
            throw new IllegalArgumentException("Invalid question index");
        }

        Question question = quiz.getQuestions().get(questionIndex);
        question.setText(questionDTO.text());
        question.setCorrectAnswer(questionDTO.correctAnswer());
        question.setOptions(questionDTO.options());

        quizRepository.save(quiz);
    }

    private QuizDTO convertToDTO(Quiz quiz, User currentUser) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getCourse().getId(),
                quiz.getQuestions().stream()
                        .map(q -> new QuestionDTO(
                                q.getId(), // إضافة معرف السؤال
                                q.getText(),
                                q.getCorrectAnswer(),
                                q.getOptions(),
                                q.getQuestionType(),
                                q.getPoints() != null ? q.getPoints() : 0,
                                q.getExplanation()))
                        .collect(Collectors.toList()),
                quiz.getIsClosed(),
                currentUser.getRole() == Role.STUDENT
                        ? submissionRepository.existsByStudentIdAndQuizId(currentUser.getId(), quiz.getId())
                        : null,
                currentUser.getRole() == Role.STUDENT
                        ? submissionRepository.findByStudentIdAndQuizId(currentUser.getId(), quiz.getId())
                                .map(QuizSubmission::getScore)
                                .orElse(null)
                        : null,
                currentUser.getRole() != Role.STUDENT
                        ? submissionRepository.findByQuizId(quiz.getId()).stream()
                                .map(this::convertToSubmissionDTO)
                                .collect(Collectors.toList())
                        : null,
                quiz.getOpenTime(),
                quiz.getCloseTime());
    }

    @Transactional
    public void updateCorrectAnswer(Long questionId, String newCorrectAnswer) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(questionId));

        // تحديث الإجابة الصحيحة
        question.setCorrectAnswer(newCorrectAnswer);
        questionRepository.save(question);

        // إعادة حساب العلامات لجميع التقديمات
        List<QuizSubmission> submissions = submissionRepository.findByQuizId(question.getQuiz().getId());

        for (QuizSubmission submission : submissions) {
            int newScore = calculateScore(question.getQuiz(), submission.getAnswers());
            submission.setScore(newScore);
            submissionRepository.save(submission);

            // تحديث الدرجة في سجل الدرجات
            gradeRepository.findBySubmissionId(submission.getId()).ifPresent(grade -> {
                grade.setScore(newScore);
                gradeRepository.save(grade);
            });
        }
    }

}