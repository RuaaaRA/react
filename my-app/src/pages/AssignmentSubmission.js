import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { motion } from 'framer-motion';
import { ArrowLeftIcon, ArrowDownTrayIcon, DocumentTextIcon, ClockIcon, UserIcon } from '@heroicons/react/24/outline';

const AssignmentSubmission = () => {
  const { assignmentId } = useParams();
  const navigate = useNavigate();
  const [assignment, setAssignment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [solutionText, setSolutionText] = useState('');
  const [solutionFile, setSolutionFile] = useState(null);
  const [submissionStatus, setSubmissionStatus] = useState(null);
  const [userRole, setUserRole] = useState(null);
  const [submissions, setSubmissions] = useState([]);
  const [showSubmissions, setShowSubmissions] = useState(false);

  useEffect(() => {
    const fetchUserRole = () => {
      const token = localStorage.getItem('accessToken');
      if (token) {
        const payload = JSON.parse(atob(token.split('.')[1]));
        setUserRole(payload.role);
      }
    };

    fetchUserRole();
  }, []);

  useEffect(() => {
    const fetchAssignment = async () => {
      try {
        const token = localStorage.getItem('accessToken');
        const response = await axios.get(`http://localhost:8080/assignments/${assignmentId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setAssignment(response.data);

        // Fetch submissions if user is admin or instructor
        if (userRole === 'ADMIN' || userRole === 'INSTRUCTOR') {
          const submissionsResponse = await axios.get(
            `http://localhost:8080/assignments/${assignmentId}/submissions`,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
          setSubmissions(submissionsResponse.data);
        }
      } catch (err) {
        setError('Failed to load assignment details');
        console.error('Error fetching assignment:', err);
      } finally {
        setLoading(false);
      }
    };

    if (userRole) {
      fetchAssignment();
    }
  }, [assignmentId, userRole]);

  const downloadAssignmentFile = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      const response = await axios.get(
        `http://localhost:8080/assignments/${assignmentId}/download`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          responseType: 'blob',
        }
      );

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `assignment_${assignmentId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Error downloading file:', err);
      setError('Failed to download assignment file');
    }
  };

  const downloadStudentFile = async (fileUrl, studentName) => {
    try {
      const token = localStorage.getItem('accessToken');
      const response = await axios.get(fileUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        responseType: 'blob',
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      // Extract file extension from URL
      const fileExtension = fileUrl.split('.').pop();
      link.setAttribute('download', `solution_${studentName || 'student'}_${assignmentId}.${fileExtension}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Error downloading student file:', err);
      setError('Failed to download student solution file');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('solutionText', solutionText);
    if (solutionFile) {
      formData.append('solutionFile', solutionFile);
    }

    try {
      const token = localStorage.getItem('accessToken');
      const response = await axios.post(
        `http://localhost:8080/assignments/${assignmentId}/submit`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'multipart/form-data',
          },
        }
      );

      if (response.data.success) {
        setSubmissionStatus('success');
        setSolutionText('');
        setSolutionFile(null);
      }
    } catch (err) {
      setSubmissionStatus('error');
      console.error('Error submitting solution:', err);
    }
  };

  const handleFileChange = (e) => {
    setSolutionFile(e.target.files[0]);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-screen">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 max-w-md"
        >
          <p>{error}</p>
          <button
            onClick={() => navigate(-1)}
            className="mt-2 px-4 py-2 bg-gray-200 rounded hover:bg-gray-300"
          >
            Go Back
          </button>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <motion.button
          onClick={() => navigate(-1)}
          className="flex items-center text-gray-600 hover:text-gray-900 mb-6 transition-colors duration-200"
          whileHover={{ x: -3 }}
        >
          <ArrowLeftIcon className="h-5 w-5 mr-2" />
          Back to Assignments
        </motion.button>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="bg-white rounded-xl shadow-md overflow-hidden mb-6 border border-gray-200"
        >
          <div className="p-6 md:p-8">
            <h1 className="text-2xl font-bold text-gray-900 mb-2">{assignment.title}</h1>
            <div className="mb-6">
              <h2 className="text-lg font-semibold text-gray-800 mb-2">Description</h2>
              <p className="text-gray-600 whitespace-pre-line">{assignment.description}</p>
            </div>
            <div className="flex items-center justify-between text-sm text-gray-500 mb-2">
              <div className="flex items-center">
                <ClockIcon className="h-4 w-4 mr-1" />
                <span>
                  Due: {new Date(assignment.dueDate).toLocaleDateString()} at{' '}
                  {new Date(assignment.dueDate).toLocaleTimeString()}
                </span>
              </div>
              <button 
                onClick={downloadAssignmentFile}
                className="flex items-center text-blue-600 hover:text-blue-800"
              >
                <ArrowDownTrayIcon className="h-4 w-4 mr-1" />
                Download Assignment
              </button>
            </div>
          </div>
        </motion.div>

        {/* Submissions Section for Admin/Instructor */}
        {(userRole === 'ADMIN' || userRole === 'INSTRUCTOR') && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.1 }}
            className="bg-white rounded-xl shadow-md overflow-hidden mb-6 border border-gray-200"
          >
            <div className="p-6 md:p-8">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold text-gray-900">Student Submissions</h2>
                <button
                  onClick={() => setShowSubmissions(!showSubmissions)}
                  className="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-md text-sm font-medium"
                >
                  {showSubmissions ? 'Hide Submissions' : 'Show Submissions'}
                </button>
              </div>

              {showSubmissions && (
                <div className="space-y-4">
                  {submissions.length > 0 ? (
                    submissions.map((submission) => (
                      <div key={submission.id} className="border border-gray-200 rounded-lg p-4">
                        <div className="flex items-center mb-3">
                          <UserIcon className="h-5 w-5 text-gray-500 mr-2" />
                          <span className="font-medium text-gray-800">
                            {submission.studentName || `Student ID: ${submission.studentId}`}
                          </span>
                          <span className="ml-auto text-xs text-gray-500">
                            Submitted: {new Date(submission.submissionDate).toLocaleString()}
                          </span>
                        </div>
                        
                        {submission.grade && (
                          <div className="mb-3 bg-blue-50 p-2 rounded">
                            <h3 className="text-sm font-semibold text-blue-700">Grade:</h3>
                            <p className="text-blue-600 font-medium">{submission.grade}</p>
                          </div>
                        )}

                        {submission.feedback && (
                          <div className="mb-3 bg-yellow-50 p-2 rounded">
                            <h3 className="text-sm font-semibold text-yellow-700">Feedback:</h3>
                            <p className="text-yellow-600">{submission.feedback}</p>
                          </div>
                        )}

                        <div className="mb-3">
                          <h3 className="text-sm font-semibold text-gray-700 mb-1">Solution Text:</h3>
                          <p className="text-gray-600 whitespace-pre-line bg-gray-50 p-3 rounded">
                            {submission.solutionText || 'No text solution provided'}
                          </p>
                        </div>

                        {submission.solutionFileUrl && (
                          <button
                            onClick={() => downloadStudentFile(submission.solutionFileUrl, submission.studentName || `student_${submission.studentId}`)}
                            className="flex items-center text-blue-600 hover:text-blue-800 text-sm"
                          >
                            <DocumentTextIcon className="h-4 w-4 mr-1" />
                            Download Solution File
                          </button>
                        )}
                      </div>
                    ))
                  ) : (
                    <p className="text-gray-500 text-center py-4">No submissions yet</p>
                  )}
                </div>
              )}
            </div>
          </motion.div>
        )}

        {/* Submission Form for Students */}
        {userRole === 'STUDENT' && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
            className="bg-white rounded-xl shadow-md overflow-hidden border border-gray-200"
          >
            <div className="p-6 md:p-8">
              <h2 className="text-xl font-bold text-gray-900 mb-4">Submit Your Solution</h2>

              {submissionStatus === 'success' && (
                <motion.div
                  initial={{ opacity: 0, y: -20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-green-50 border-l-4 border-green-500 p-4 mb-6 rounded"
                >
                  <div className="flex items-center">
                    <div className="h-5 w-5 text-green-500 mr-3" />
                    <p className="text-green-700">Your solution has been submitted successfully!</p>
                  </div>
                </motion.div>
              )}

              {submissionStatus === 'error' && (
                <motion.div
                  initial={{ opacity: 0, y: -20 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="bg-red-50 border-l-4 border-red-500 p-4 mb-6 rounded"
                >
                  <div className="flex items-center">
                    <div className="h-5 w-5 text-red-500 mr-3" />
                    <p className="text-red-700">Failed to submit your solution. Please try again.</p>
                  </div>
                </motion.div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="mb-6">
                  <label htmlFor="solutionText" className="block text-sm font-medium text-gray-700 mb-2">
                    Solution Text
                  </label>
                  <textarea
                    id="solutionText"
                    rows={6}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                    value={solutionText}
                    onChange={(e) => setSolutionText(e.target.value)}
                    placeholder="Write your solution here..."
                    required
                  />
                </div>

                <div className="mb-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Upload Solution File (Optional)
                  </label>
                  <div className="mt-1 flex items-center">
                    <label className="cursor-pointer bg-white py-2 px-3 border border-gray-300 rounded-md shadow-sm text-sm leading-4 font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                      <span>Choose file</span>
                      <input
                        type="file"
                        className="sr-only"
                        onChange={handleFileChange}
                        accept=".pdf,.doc,.docx,.txt"
                      />
                    </label>
                    <span className="ml-3 text-sm text-gray-500">
                      {solutionFile ? solutionFile.name : 'No file chosen'}
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-gray-500">
                    PDF, DOC, DOCX, or TXT files (Max 5MB)
                  </p>
                </div>

                <div className="flex justify-end">
                  <motion.button
                    type="submit"
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    className="px-6 py-2 bg-blue-600 text-white rounded-md text-sm font-medium shadow-md hover:bg-blue-700 transition-all duration-200"
                  >
                    Submit Solution
                  </motion.button>
                </div>
              </form>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
};

export default AssignmentSubmission;