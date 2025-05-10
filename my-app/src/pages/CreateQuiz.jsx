import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { 
  ArrowLeftIcon,
  PlusCircleIcon,
  XCircleIcon,
  CheckCircleIcon,
  ClockIcon,
  AcademicCapIcon
} from '@heroicons/react/24/outline';
import { motion } from 'framer-motion';

const CreateQuiz = () => {
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [quizData, setQuizData] = useState({
    title: '',
    description: '',
    questions: [],
    timeLimit: 30,
    closeTime: ''
  });
  const [currentQuestion, setCurrentQuestion] = useState({
    text: '',
    options: ['', ''],
    correctAnswer: ''
  });
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleQuizChange = (e) => {
    const { name, value } = e.target;
    setQuizData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleQuestionChange = (e) => {
    const { name, value } = e.target;
    setCurrentQuestion(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleOptionChange = (index, value) => {
    const newOptions = [...currentQuestion.options];
    newOptions[index] = value;
    setCurrentQuestion(prev => ({
      ...prev,
      options: newOptions
    }));
  };

  const addOption = () => {
    if (currentQuestion.options.length < 5) {
      setCurrentQuestion(prev => ({
        ...prev,
        options: [...prev.options, '']
      }));
    }
  };

  const removeOption = (index) => {
    if (currentQuestion.options.length > 2) {
      const newOptions = [...currentQuestion.options];
      newOptions.splice(index, 1);
      
      setCurrentQuestion(prev => ({
        ...prev,
        options: newOptions,
        correctAnswer: prev.correctAnswer === prev.options[index] ? '' : prev.correctAnswer
      }));
    }
  };

  const addQuestion = () => {
    if (!currentQuestion.text.trim()) {
      setError('Question text cannot be empty');
      return;
    }

    if (currentQuestion.options.some(opt => !opt.trim())) {
      setError('All options must be filled');
      return;
    }

    if (!currentQuestion.correctAnswer) {
      setError('Please select a correct answer');
      return;
    }

    setQuizData(prev => ({
      ...prev,
      questions: [...prev.questions, currentQuestion]
    }));

    setCurrentQuestion({
      text: '',
      options: ['', ''],
      correctAnswer: ''
    });
    setError('');
  };

  const removeQuestion = (index) => {
    const newQuestions = [...quizData.questions];
    newQuestions.splice(index, 1);
    setQuizData(prev => ({
      ...prev,
      questions: newQuestions
    }));
  };

  const submitQuiz = async () => {
    if (!quizData.title.trim()) {
      setError('Quiz title cannot be empty');
      return;
    }

    if (quizData.questions.length === 0) {
      setError('Please add at least one question');
      return;
    }

    setIsSubmitting(true);
    setError('');

    try {
      const token = localStorage.getItem('accessToken');
      const payload = {
        title: quizData.title,
        description: quizData.description,
        courseId: courseId,
        questions: quizData.questions.map(q => ({
          text: q.text,
          options: q.options,
          correctAnswer: q.correctAnswer
        })),
        timeLimit: quizData.timeLimit,
        closeTime: quizData.closeTime || null
      };

      await axios.post('http://localhost:8080/api/quizzes', payload, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      navigate(`/courses/${courseId}`);
    } catch (err) {
      console.error('Error creating quiz:', err);
      setError('Failed to create quiz. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <motion.button
          onClick={() => navigate(`/courses/${courseId}`)}
          className="flex items-center text-indigo-600 hover:text-indigo-800 mb-6"
          whileHover={{ x: -3 }}
          whileTap={{ scale: 0.95 }}
        >
          <ArrowLeftIcon className="h-5 w-5 mr-2" />
          Back to Course
        </motion.button>

        <motion.div 
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl shadow-xl overflow-hidden border border-gray-200/50"
        >
          {/* Header Section */}
          <div className="bg-gradient-to-r from-orange-500 to-amber-500 p-6 text-white">
            <h1 className="text-2xl font-bold mb-2">Create New Quiz</h1>
            <p className="text-orange-100">Design an engaging quiz for your students</p>
          </div>

          <div className="p-6">
            <div className="space-y-6">
              {/* Quiz Info Section */}
              <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Quiz Title*</label>
                  <input
                    type="text"
                    name="title"
                    value={quizData.title}
                    onChange={handleQuizChange}
                    className="w-full rounded-lg border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-3 border"
                    placeholder="Enter quiz title"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Time Limit (minutes)*</label>
                  <div className="relative">
                    <input
                      type="number"
                      name="timeLimit"
                      min="1"
                      value={quizData.timeLimit}
                      onChange={handleQuizChange}
                      className="w-full rounded-lg border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-3 border"
                    />
                    <div className="absolute right-3 top-3 text-gray-400">
                      <ClockIcon className="h-5 w-5" />
                    </div>
                  </div>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                <textarea
                  name="description"
                  value={quizData.description}
                  onChange={handleQuizChange}
                  rows={3}
                  className="w-full rounded-lg border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-3 border"
                  placeholder="Enter quiz description (optional)"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Close Date (optional)</label>
                <input
                  type="datetime-local"
                  name="closeTime"
                  value={quizData.closeTime}
                  onChange={handleQuizChange}
                  className="w-full rounded-lg border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-3 border"
                />
              </div>

              {/* Questions Section */}
              <div className="border-t pt-6">
                <h2 className="text-lg font-medium text-gray-800 mb-4 flex items-center">
                  <AcademicCapIcon className="h-5 w-5 mr-2 text-orange-500" />
                  Quiz Questions
                </h2>

                <div className="space-y-4">
                  {quizData.questions.map((question, index) => (
                    <motion.div
                      key={index}
                      initial={{ opacity: 0, y: 10 }}
                      animate={{ opacity: 1, y: 0 }}
                      className="bg-orange-50 p-4 rounded-lg border border-orange-100"
                    >
                      <div className="flex justify-between items-start">
                        <div>
                          <p className="font-medium text-gray-800">
                            <span className="text-orange-600 font-bold mr-2">{index + 1}.</span>
                            {question.text}
                          </p>
                          <div className="mt-2">
                            <p className="text-sm text-gray-600">
                              <span className="font-medium">Options:</span> {question.options.join(', ')}
                            </p>
                            <p className="text-sm text-green-600 mt-1">
                              <span className="font-medium">Correct Answer:</span> {question.correctAnswer}
                            </p>
                          </div>
                        </div>
                        <motion.button
                          onClick={() => removeQuestion(index)}
                          className="text-orange-500 hover:text-orange-700 p-1"
                          whileHover={{ scale: 1.1 }}
                          whileTap={{ scale: 0.9 }}
                        >
                          <XCircleIcon className="h-5 w-5" />
                        </motion.button>
                      </div>
                    </motion.div>
                  ))}

                  {/* Add Question Form */}
                  <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="bg-white p-4 rounded-lg border border-orange-200 shadow-sm"
                  >
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-1">Question Text*</label>
                      <input
                        type="text"
                        name="text"
                        value={currentQuestion.text}
                        onChange={handleQuestionChange}
                        className="w-full rounded-lg border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-3 border"
                        placeholder="Enter question text"
                      />
                    </div>

                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-2">Options*</label>
                      <div className="space-y-2">
                        {currentQuestion.options.map((option, index) => (
                          <motion.div
                            key={index}
                            whileHover={{ x: 3 }}
                            className="flex items-center space-x-2"
                          >
                            <input
                              type="text"
                              value={option}
                              onChange={(e) => handleOptionChange(index, e.target.value)}
                              className="flex-1 rounded-lg border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2.5 border"
                              placeholder={`Option ${index + 1}`}
                            />
                            <motion.button
                              type="button"
                              onClick={() => removeOption(index)}
                              className="text-orange-500 hover:text-orange-700 p-2"
                              disabled={currentQuestion.options.length <= 2}
                              whileTap={{ scale: 0.9 }}
                            >
                              <XCircleIcon className="h-5 w-5" />
                            </motion.button>
                          </motion.div>
                        ))}
                      </div>
                      {currentQuestion.options.length < 5 && (
                        <motion.button
                          type="button"
                          onClick={addOption}
                          className="mt-2 flex items-center text-orange-600 text-sm"
                          whileHover={{ x: 3 }}
                        >
                          <PlusCircleIcon className="h-4 w-4 mr-1" />
                          Add Option
                        </motion.button>
                      )}
                    </div>

                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700 mb-1">Correct Answer*</label>
                      <select
                        name="correctAnswer"
                        value={currentQuestion.correctAnswer}
                        onChange={handleQuestionChange}
                        className="w-full rounded-lg border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2.5 border"
                      >
                        <option value="">Select correct answer</option>
                        {currentQuestion.options.map((option, index) => (
                          option && (
                            <option key={index} value={option}>
                              Option {index + 1}: {option}
                            </option>
                          )
                        ))}
                      </select>
                    </div>

                    <motion.button
                      type="button"
                      onClick={addQuestion}
                      className="w-full bg-orange-500 text-white py-2.5 px-4 rounded-lg hover:bg-orange-600 flex items-center justify-center shadow-md"
                      whileHover={{ scale: 1.02 }}
                      whileTap={{ scale: 0.98 }}
                    >
                      <PlusCircleIcon className="h-5 w-5 mr-2" />
                      Add Question
                    </motion.button>
                  </motion.div>
                </div>
              </div>

              {error && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="bg-red-50 border-l-4 border-red-500 p-4 rounded-lg"
                >
                  <p className="text-red-700">{error}</p>
                </motion.div>
              )}

              {/* Action Buttons */}
              <div className="flex justify-end space-x-3 pt-4 border-t">
                <motion.button
                  onClick={() => navigate(`/courses/${courseId}`)}
                  className="px-5 py-2.5 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                >
                  Cancel
                </motion.button>
                <motion.button
                  onClick={submitQuiz}
                  disabled={isSubmitting || quizData.questions.length === 0}
                  className={`px-5 py-2.5 rounded-lg font-medium flex items-center ${
                    isSubmitting || quizData.questions.length === 0
                      ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                      : 'bg-gradient-to-r from-orange-500 to-amber-500 text-white hover:from-orange-600 hover:to-amber-600 shadow-md'
                  }`}
                  whileHover={
                    !isSubmitting && quizData.questions.length > 0 
                      ? { scale: 1.05 } 
                      : {}
                  }
                  whileTap={
                    !isSubmitting && quizData.questions.length > 0 
                      ? { scale: 0.95 } 
                      : {}
                  }
                >
                  {isSubmitting ? (
                    'Creating...'
                  ) : (
                    <>
                      <CheckCircleIcon className="h-5 w-5 mr-2" />
                      Create Quiz
                    </>
                  )}
                </motion.button>
              </div>
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
};

export default CreateQuiz;