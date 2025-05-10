import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import {
  CheckCircleIcon,
  XCircleIcon,
  ArrowLeftIcon,
  ClockIcon,
  AcademicCapIcon
} from '@heroicons/react/24/outline';
import { motion } from 'framer-motion';

const QuizPage = () => {
  const { quizId } = useParams();
  const navigate = useNavigate();
  const [quiz, setQuiz] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [score, setScore] = useState(null);
  const [timeLeft, setTimeLeft] = useState(null);

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const response = await axios.get(
          `http://localhost:8080/api/quizzes/${quizId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        );

        setQuiz(response.data);
        
        // Calculate initial time left if endTime exists
        if (response.data.endTime) {
          const endTime = new Date(response.data.endTime).getTime();
          const now = new Date().getTime();
          setTimeLeft(Math.max(0, Math.floor((endTime - now) / 1000)));
        }
        
        setLoading(false);
      } catch (err) {
        setError('Failed to load quiz');
        console.error(err);
        setLoading(false);
      }
    };

    fetchQuiz();
  }, [quizId]);

  // Timer effect
  useEffect(() => {
    if (!timeLeft) return;

    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [timeLeft]);

  const handleSelectAnswer = (questionIndex, answer) => {
    setSelectedAnswers(prev => ({
      ...prev,
      [questionIndex]: answer
    }));
  };

  const handleSubmit = async () => {
    if (submitted) return;

    try {
      const token = localStorage.getItem('accessToken');
      const answers = quiz.questions.map((_, index) =>
        selectedAnswers[index] || ''
      );

      const response = await axios.post(
        `http://localhost:8080/api/quizzes/${quizId}/submit`,
        answers,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      setSubmitted(true);
      setScore(response.data.score);
    } catch (err) {
      console.error('Error submitting quiz:', err);
      alert(err.response?.data?.message || 'Failed to submit quiz');
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen bg-gradient-to-br from-blue-50 to-indigo-50">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
          className="h-12 w-12 rounded-full border-t-2 border-b-2 border-indigo-500"
        ></motion.div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 p-4">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="bg-white rounded-xl shadow-lg p-6 max-w-md w-full"
        >
          <div className="flex flex-col items-center text-center">
            <XCircleIcon className="h-16 w-16 text-red-500 mb-4" />
            <h2 className="text-xl font-bold text-gray-800 mb-2">Error Loading Quiz</h2>
            <p className="text-gray-600 mb-6">{error}</p>
            <motion.button
              onClick={() => navigate(-1)}
              className="px-6 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 flex items-center"
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              <ArrowLeftIcon className="h-5 w-5 mr-2" />
              Back to Previous Page
            </motion.button>
          </div>
        </motion.div>
      </div>
    );
  }

  if (submitted) {
    return (
      <div className="flex justify-center items-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 p-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl shadow-lg p-8 max-w-md w-full"
        >
          <div className="text-center">
            <motion.div
              initial={{ scale: 0.8 }}
              animate={{ scale: 1 }}
              transition={{ type: 'spring', stiffness: 300 }}
            >
              <CheckCircleIcon className="h-20 w-20 text-green-500 mx-auto mb-4" />
            </motion.div>
            
            <h2 className="text-2xl font-bold text-gray-800 mb-2">Quiz Submitted!</h2>
            <p className="text-gray-600 mb-1">You scored:</p>
            <motion.p 
              className="text-4xl font-bold text-indigo-600 mb-6"
              initial={{ scale: 0.5 }}
              animate={{ scale: 1 }}
              transition={{ delay: 0.2 }}
            >
              {score}%
            </motion.p>

            <motion.button
              onClick={() => navigate(`/courses/${quiz.courseId}`, { replace: true })}
              className="px-6 py-3 bg-gradient-to-r from-indigo-600 to-purple-600 text-white rounded-lg hover:from-indigo-700 hover:to-purple-700 w-full"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              Back to Course
            </motion.button>
          </div>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl shadow-xl overflow-hidden mb-8"
        >
          {/* Quiz Header */}
          <div className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white p-6">
            <div className="flex justify-between items-start">
              <div>
                <motion.h1 
                  className="text-2xl font-bold mb-2"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: 0.1 }}
                >
                  {quiz.title}
                </motion.h1>
                <p className="text-blue-100">{quiz.description}</p>
              </div>
              
              {timeLeft !== null && (
                <div className="flex items-center bg-blue-800/30 px-3 py-1 rounded-full">
                  <ClockIcon className="h-5 w-5 mr-1" />
                  <span>
                    {Math.floor(timeLeft / 60)}:{String(timeLeft % 60).padStart(2, '0')}
                  </span>
                </div>
              )}
            </div>
          </div>

          {/* Questions Section */}
          <div className="p-6">
            {quiz.questions.map((question, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05 }}
                className="mb-8 last:mb-0"
              >
                <div className="font-medium text-gray-800 mb-4 text-lg">
                  <span className="text-indigo-600 font-bold mr-2">{index + 1}.</span>
                  {question.text}
                </div>

                <div className="space-y-3">
                  {question.options.map((option, optIndex) => (
                    <motion.div
                      key={optIndex}
                      whileHover={{ scale: 1.01 }}
                      whileTap={{ scale: 0.99 }}
                      className={`p-4 border-2 rounded-lg cursor-pointer transition-all ${
                        selectedAnswers[index] === option
                          ? 'border-indigo-500 bg-indigo-50'
                          : 'border-gray-200 hover:border-indigo-300 hover:bg-indigo-50/50'
                      }`}
                      onClick={() => handleSelectAnswer(index, option)}
                    >
                      <div className="flex items-center">
                        <div className={`h-5 w-5 rounded-full border-2 mr-3 flex items-center justify-center ${
                          selectedAnswers[index] === option
                            ? 'border-indigo-600 bg-indigo-600'
                            : 'border-gray-300'
                        }`}>
                          {selectedAnswers[index] === option && (
                            <motion.div
                              initial={{ scale: 0 }}
                              animate={{ scale: 1 }}
                              className="h-2 w-2 rounded-full bg-white"
                            />
                          )}
                        </div>
                        <span className="text-gray-700">{option}</span>
                      </div>
                    </motion.div>
                  ))}
                </div>
              </motion.div>
            ))}
          </div>

          {/* Footer */}
          <div className="bg-gray-50 p-4 border-t flex flex-col sm:flex-row justify-between items-center">
            <motion.button
              onClick={() => navigate(`/courses/${quiz.courseId}`)}
              className="flex items-center text-indigo-600 hover:text-indigo-800 mb-4 sm:mb-0"
              whileHover={{ x: -3 }}
            >
              <ArrowLeftIcon className="h-5 w-5 mr-2" />
              Back to Course
            </motion.button>

            <motion.button
              onClick={handleSubmit}
              disabled={Object.keys(selectedAnswers).length !== quiz.questions.length}
              className={`px-6 py-3 rounded-lg font-medium flex items-center ${
                Object.keys(selectedAnswers).length === quiz.questions.length
                  ? 'bg-gradient-to-r from-green-600 to-emerald-600 text-white hover:from-green-700 hover:to-emerald-700 shadow-md'
                  : 'bg-gray-200 text-gray-500 cursor-not-allowed'
              }`}
              whileHover={
                Object.keys(selectedAnswers).length === quiz.questions.length 
                  ? { scale: 1.05 } 
                  : {}
              }
              whileTap={
                Object.keys(selectedAnswers).length === quiz.questions.length 
                  ? { scale: 0.98 } 
                  : {}
              }
            >
              <AcademicCapIcon className="h-5 w-5 mr-2" />
              Submit Quiz
            </motion.button>
          </div>
        </motion.div>
      </div>
    </div>
  );
};

export default QuizPage;