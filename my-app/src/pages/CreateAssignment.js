import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import { 
  ArrowLeftIcon,
  DocumentTextIcon,
  CalendarIcon,
  ClockIcon,
  PaperClipIcon,
  CheckCircleIcon,
  ExclamationCircleIcon
} from '@heroicons/react/24/outline';
import { motion } from 'framer-motion';

const CreateAssignment = () => {
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    dueDate: '',
    totalMarks: 100,
    file: null
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    setFormData(prev => ({
      ...prev,
      file: e.target.files[0]
    }));
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');
  
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        throw new Error('No authentication token found');
      }
  
      // تحويل التاريخ إلى التنسيق المطلوب (yyyy-MM-dd HH:mm:ss)
      const dueDate = new Date(formData.dueDate);
      const formattedDueDate = `${dueDate.getFullYear()}-${String(dueDate.getMonth() + 1).padStart(2, '0')}-${String(dueDate.getDate()).padStart(2, '0')} ${String(dueDate.getHours()).padStart(2, '0')}:${String(dueDate.getMinutes()).padStart(2, '0')}:${String(dueDate.getSeconds()).padStart(2, '0')}`;
  
      const formDataToSend = new FormData();
      
      formDataToSend.append('assignmentDTO', JSON.stringify({
        title: formData.title,
        description: formData.description,
        dueDate: formattedDueDate,
        maxScore: formData.totalMarks  // تغيير من totalMarks إلى maxScore لتتوافق مع DTO
      }));
      
      if (formData.file) {
        formDataToSend.append('file', formData.file);
      }
      
      formDataToSend.append('courseId', courseId);
  
      console.log('Sending data:', {
        title: formData.title,
        description: formData.description,
        dueDate: formattedDueDate,
        maxScore: formData.totalMarks,
        file: formData.file?.name,
        courseId
      });
  
      const response = await axios.post(
        'http://localhost:8080/assignments/create',
        formDataToSend,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'multipart/form-data'
          }
        }
      );
  
      setSuccess('Assignment created successfully!');
      setTimeout(() => {
        navigate(`/courses/${courseId}`);
      }, 1500);
    } catch (err) {
      console.error('Error details:', err.response?.data || err.message);
      setError(err.response?.data?.message || 'Failed to create assignment. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white shadow-md rounded-lg overflow-hidden"
        >
          <div className="bg-gradient-to-r from-blue-600 to-blue-500 p-6 text-white">
            <div className="flex items-center justify-between">
              <button
                onClick={() => navigate(`/courses/${courseId}`)}
                className="flex items-center text-white hover:text-blue-200 transition-colors"
              >
                <ArrowLeftIcon className="h-5 w-5 mr-2" />
                Back to Course
              </button>
              <h2 className="text-2xl font-bold flex items-center">
                <DocumentTextIcon className="h-6 w-6 mr-2" />
                Create New Assignment
              </h2>
            </div>
          </div>

          <div className="p-6">
            {error && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="mb-6 bg-red-50 border-l-4 border-red-500 p-4 rounded"
              >
                <div className="flex items-center">
                  <ExclamationCircleIcon className="h-5 w-5 text-red-500 mr-3" />
                  <p className="text-red-700">{error}</p>
                </div>
              </motion.div>
            )}

            {success && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="mb-6 bg-green-50 border-l-4 border-green-500 p-4 rounded"
              >
                <div className="flex items-center">
                  <CheckCircleIcon className="h-5 w-5 text-green-500 mr-3" />
                  <p className="text-green-700">{success}</p>
                </div>
              </motion.div>
            )}

            <form onSubmit={handleSubmit}>
              <div className="space-y-6">
                <div>
                  <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
                    Assignment Title *
                  </label>
                  <input
                    type="text"
                    id="title"
                    name="title"
                    value={formData.title}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div>
                  <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
                    Description *
                  </label>
                  <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    required
                    rows={4}
                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label htmlFor="dueDate" className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                      <CalendarIcon className="h-5 w-5 mr-2 text-blue-500" />
                      Due Date *
                    </label>
                    <input
  type="datetime-local"
  id="dueDate"
  name="dueDate"
  value={formData.dueDate}
  onChange={handleChange}
  required
  className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
/>
                  </div>

                  <div>
                    <label htmlFor="totalMarks" className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                      <ClockIcon className="h-5 w-5 mr-2 text-blue-500" />
                      Total Marks
                    </label>
                    <input
                      type="number"
                      id="totalMarks"
                      name="totalMarks"
                      value={formData.totalMarks}
                      onChange={handleChange}
                      min="1"
                      className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                <div>
                  <label htmlFor="file" className="block text-sm font-medium text-gray-700 mb-1 flex items-center">
                    <PaperClipIcon className="h-5 w-5 mr-2 text-blue-500" />
                    Assignment File *
                  </label>
                  <div className="mt-1 flex justify-center px-6 pt-5 pb-6 border-2 border-gray-300 border-dashed rounded-md">
                    <div className="space-y-1 text-center">
                      <div className="flex text-sm text-gray-600">
                        <label
                          htmlFor="file-upload"
                          className="relative cursor-pointer bg-white rounded-md font-medium text-blue-600 hover:text-blue-500 focus-within:outline-none"
                        >
                          <span>Upload a file</span>
                          <input
                            id="file-upload"
                            name="file"
                            type="file"
                            onChange={handleFileChange}
                            required
                            className="sr-only"
                          />
                        </label>
                        <p className="pl-1">or drag and drop</p>
                      </div>
                      <p className="text-xs text-gray-500">
                        PDF, DOCX, PPTX up to 10MB
                      </p>
                      {formData.file && (
                        <p className="text-sm text-green-600 mt-2">
                          Selected file: {formData.file.name}
                        </p>
                      )}
                    </div>
                  </div>
                </div>

                <div className="flex justify-end">
                  <motion.button
                    type="submit"
                    disabled={loading}
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                    className="px-6 py-3 bg-blue-600 text-white rounded-md shadow-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {loading ? (
                      <span className="flex items-center">
                        <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                        </svg>
                        Creating...
                      </span>
                    ) : (
                      'Create Assignment'
                    )}
                  </motion.button>
                </div>
              </div>
            </form>
          </div>
        </motion.div>
      </div>
    </div>
  );
};

export default CreateAssignment;