import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import {
  ArrowLeftIcon,
  ClockIcon,
  CheckCircleIcon,
  XCircleIcon
} from '@heroicons/react/24/outline';
import { motion } from 'framer-motion';

const TakeQuiz = () => {
  const { quizId } = useParams();
  const navigate = useNavigate();
  const [quiz, setQuiz] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const [timeLeft, setTimeLeft] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState(null);

  useEffect(() => {
    const fetchQuizDetails = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const [quizRes, questionsRes] = await Promise.all([
          axios.get(`http://localhost:8080/quizzes/${quizId}`, {
            headers: { Authorization: `Bearer ${token}` }
          }),
          axios.get(`http://localhost:8080/quizzes/${quizId}/questions`, {
            headers: { Authorization: `Bearer ${token}` }
          })
        ]);

        setQuiz(quizRes.data);
        setQuestions(questionsRes.data);
        
        const timeLimit = quizRes.data.timeLimit || 30;
        setTimeLeft(timeLimit * 60);
        
        const initialAnswers = {};
        questionsRes.data.forEach(question => {
          initialAnswers[question.id] = null;
        });
        setSelectedAnswers(initialAnswers);
      } catch (err) {
        console.error('Error fetching quiz:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchQuizDetails();
  }, [quizId]);

  useEffect(() => {
    if (timeLeft <= 0) return;

    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          handleSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [timeLeft]);

  const handleAnswerSelect = (questionId, answerIndex) => {
    setSelectedAnswers(prev => ({
      ...prev,
      [questionId]: answerIndex
    }));
  };

  const handleSubmit = async () => {
    if (isSubmitting) return;
    
    setIsSubmitting(true);
    try {
      const token = localStorage.getItem('accessToken');
      
      // Prepare answers in correct format
      const answers = {};
      questions.forEach(question => {
        const selectedIndex = selectedAnswers[question.id];
        answers[question.id] = selectedIndex !== null ? 
          question.options[selectedIndex] : '';
      });

      console.log('Submitting answers:', answers); // Debug log

      const response = await axios.post(
        `http://localhost:8080/grading/grade/${quizId}`,
        answers,
        {
          headers: { 
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      console.log('Response:', response.data); // Debug log

      // Prepare detailed result
      const detailedResult = {
        ...response.data,
        questions: questions.map(q => ({
          ...q,
          studentAnswer: answers[q.id] || '',
          isCorrect: response.data.questionResults?.[q.id] || false
        })),
        correctAnswers: Object.values(response.data.questionResults || {})
                          .filter(Boolean).length,
        totalQuestions: questions.length
      };

      setSubmissionResult(detailedResult);
      
    } catch (err) {
      console.error('Error submitting quiz:', err);
      alert(`حدث خطأ أثناء تقديم الاختبار: ${err.response?.data?.message || err.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  const getGradeLetter = (score) => {
    if (score >= 90) return 'ممتاز (A)';
    if (score >= 80) return 'جيد جداً (B)';
    if (score >= 70) return 'جيد (C)';
    if (score >= 60) return 'مقبول (D)';
    return 'راسب (F)';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (submissionResult) {
    return (
      <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white rounded-xl shadow-lg overflow-hidden mb-8"
          >
            <div className="p-6 text-center">
              <div className={`mx-auto flex items-center justify-center h-16 w-16 rounded-full ${
                submissionResult.score >= 50 ? 'bg-green-100' : 'bg-red-100'
              } mb-4`}>
                {submissionResult.score >= 50 ? (
                  <CheckCircleIcon className="h-8 w-8 text-green-600" />
                ) : (
                  <XCircleIcon className="h-8 w-8 text-red-600" />
                )}
              </div>
              <h1 className="text-2xl font-bold text-gray-900 mb-2">نتيجة الاختبار</h1>
              <div className="flex justify-center items-center space-x-4">
                <div className="text-4xl font-bold text-gray-900">
                  {Math.round(submissionResult.score)}%
                </div>
                <div className="text-lg text-gray-600">
                  <p>{submissionResult.correctAnswers} إجابات صحيحة من {submissionResult.totalQuestions}</p>
                  <p>التقدير: {getGradeLetter(submissionResult.score)}</p>
                </div>
              </div>
            </div>
          </motion.div>

          <div className="space-y-6">
            {submissionResult.questions.map((question, index) => (
              <motion.div
                key={question.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.1 }}
                className={`bg-white rounded-lg shadow-sm p-6 border ${
                  question.isCorrect ? 'border-green-200' : 'border-red-200'
                }`}
              >
                <div className="flex items-start">
                  <span className={`px-3 py-1 rounded-full mr-4 ${
                    question.isCorrect ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {index + 1}
                  </span>
                  <div className="flex-1">
                    <div className="flex justify-between items-start mb-4">
                      <h3 className="text-lg font-medium text-gray-900">{question.text}</h3>
                      {question.isCorrect ? (
                        <span className="inline-flex items-center px-3 py-1 rounded-full bg-green-100 text-green-800 text-sm font-medium">
                          صحيح
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-3 py-1 rounded-full bg-red-100 text-red-800 text-sm font-medium">
                          خاطئ
                        </span>
                      )}
                    </div>

                    <div className="space-y-4">
                      <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">إجابتك:</p>
                        <p className="p-3 bg-gray-50 rounded-md">{question.studentAnswer || "لم يتم الإجابة"}</p>
                      </div>
                      {!question.isCorrect && (
                        <div>
                          <p className="text-sm font-medium text-gray-500 mb-1">الإجابة الصحيحة:</p>
                          <p className="p-3 bg-green-50 rounded-md">{question.correctAnswer}</p>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </motion.div>
            ))}
          </div>

          <div className="mt-8 flex justify-center">
            <motion.button
              onClick={() => navigate(`/courses/${quiz.course.id}`)}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="px-6 py-3 bg-blue-600 text-white rounded-md shadow-md hover:shadow-lg"
            >
              العودة إلى المادة
            </motion.button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <div className="flex justify-between items-center mb-6">
          <button
            onClick={() => navigate(-1)}
            className="flex items-center text-gray-600 hover:text-gray-900 transition-colors duration-200"
          >
            <ArrowLeftIcon className="h-5 w-5 mr-2" />
            رجوع
          </button>
          
          <div className="flex items-center bg-red-50 px-4 py-2 rounded-lg">
            <ClockIcon className="h-5 w-5 text-red-600 mr-2" />
            <span className="text-red-800 font-medium">{formatTime(timeLeft)}</span>
          </div>
        </div>

        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl shadow-md overflow-hidden mb-6"
        >
          <div className="p-6">
            <h1 className="text-2xl font-bold text-gray-900 mb-2">{quiz.title}</h1>
            <p className="text-gray-600 mb-4">{quiz.description}</p>
            <div className="flex items-center text-sm text-gray-500">
              <span>عدد الأسئلة: {questions.length}</span>
            </div>
          </div>
        </motion.div>

        <div className="space-y-6">
          {questions.map((question, index) => (
            <motion.div 
              key={question.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.1 }}
              className="bg-white rounded-lg shadow-sm p-6 border border-gray-200"
            >
              <div className="flex items-start">
                <span className="bg-blue-100 text-blue-800 font-medium px-3 py-1 rounded-full mr-4">
                  {index + 1}
                </span>
                <div className="flex-1">
                  <h3 className="text-lg font-medium text-gray-900 mb-4">{question.text}</h3>
                  
                  <div className="space-y-2">
                    {question.options.map((option, optionIndex) => (
                      <div 
                        key={optionIndex}
                        className={`flex items-center p-3 border rounded-lg cursor-pointer transition-colors ${
                          selectedAnswers[question.id] === optionIndex
                            ? 'border-blue-500 bg-blue-50'
                            : 'border-gray-200 hover:bg-gray-50'
                        }`}
                        onClick={() => handleAnswerSelect(question.id, optionIndex)}
                      >
                        <div className={`w-5 h-5 rounded-full border mr-3 flex items-center justify-center ${
                          selectedAnswers[question.id] === optionIndex
                            ? 'border-blue-500 bg-blue-500'
                            : 'border-gray-400'
                        }`}>
                          {selectedAnswers[question.id] === optionIndex && (
                            <div className="w-2 h-2 rounded-full bg-white"></div>
                          )}
                        </div>
                        <span>{option}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </motion.div>
          ))}
        </div>

        <div className="mt-8 flex justify-end">
          <motion.button
            onClick={handleSubmit}
            disabled={isSubmitting}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            className="px-6 py-3 bg-gradient-to-r from-green-600 to-green-500 text-white rounded-md shadow-md hover:shadow-lg disabled:opacity-70 disabled:cursor-not-allowed"
          >
            {isSubmitting ? 'جاري الإرسال...' : 'إرسال الإجابات'}
          </motion.button>
        </div>
      </div>
    </div>
  );
};

export default TakeQuiz;