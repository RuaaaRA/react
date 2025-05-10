import { useEffect, useState, useCallback, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { motion } from 'framer-motion';
import {
  BookOpenIcon,
  AcademicCapIcon,
  ClockIcon,
  ArrowPathIcon,
  PlusIcon,
  PencilIcon,
  TrashIcon,
  UserPlusIcon
} from '@heroicons/react/24/outline';
// أضف هذا الاستيراد في أعلى الملف
import CourseFormModal from './CourseFormModal.js';
import AssignInstructorModal from './AssignInstructorModal.js';
import { AuthContext } from '../context/AuthContext.js';

const Courses = () => {
  const navigate = useNavigate();
  const { user } = useContext(AuthContext);

  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshing, setRefreshing] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isAssignModalOpen, setIsAssignModalOpen] = useState(false);
  const [editingCourse, setEditingCourse] = useState(null);
  const [currentCourse, setCurrentCourse] = useState(null);
  const [enrolledCourses, setEnrolledCourses] = useState([]);
  const [instructors, setInstructors] = useState([]);

  const fetchCourses = useCallback(async () => {
    try {
      const response = await axios.get('http://localhost:8080/courses', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      setCourses(response.data);
      setError(null);
    } catch (err) {
      console.error('Error fetching courses:', err);
      setError('Failed to fetch courses. Please try again later.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  const fetchInstructors = useCallback(async () => {
    try {
      const response = await axios.get('http://localhost:8080/courses/users/instructors', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      setInstructors(response.data);
    } catch (err) {
      console.error('Error fetching instructors:', err);
    }
  }, []);

  const fetchEnrollments = useCallback(async () => {
    if (user?.role === 'STUDENT') {
      try {
        const res = await axios.get('http://localhost:8080/enrollments/my-courses', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`
          }
        });
        setEnrolledCourses(res.data.map(e => e.courseId));
      } catch (err) {
        console.error('Error fetching enrollments:', err);
      }
    }
  }, [user]);

  useEffect(() => {
    fetchCourses();
    fetchEnrollments();
    if (user?.role === 'ADMIN') {
      fetchInstructors();
    }
  }, [fetchCourses, fetchEnrollments, fetchInstructors, user]);

  const handleRefresh = () => {
    setRefreshing(true);
    fetchCourses();
    fetchEnrollments();
    if (user?.role === 'ADMIN') {
      fetchInstructors();
    }
  };

  const handleAssignInstructor = async (courseId, instructorId) => {
    try {
      await axios.post('http://localhost:8080/courses/assign-instructor', {
        courseId,
        instructorId
      }, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      fetchCourses();
      setIsAssignModalOpen(false);
    } catch (err) {
      console.error('Assigning instructor failed:', err.response?.data || err.message);
      setError(err.response?.data?.message || 'Failed to assign instructor. Please check the instructor ID and try again.');
    }
  };
  
  const handleAssignSelf = async (courseId) => {
    try {
      await axios.post('http://localhost:8080/courses/assign-self', {
        courseId
      }, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      fetchCourses();
    } catch (err) {
      console.error('Assign self failed:', err);
      setError('Could not assign yourself to this course. You may already be assigned or the course may not exist.');
    }
  };
  
  const handleCreateCourse = async (courseData) => {
    try {
      await axios.post('http://localhost:8080/courses', courseData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      fetchCourses();
      setIsModalOpen(false);
    } catch (error) {
      console.error('Error creating course:', error);
      setError('Failed to create course. Please try again.');
    }
  };

  const handleUpdateCourse = async (courseData) => {
    try {
      await axios.put(`http://localhost:8080/courses/${editingCourse.id}`, courseData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      fetchCourses();
      setEditingCourse(null);
      setIsModalOpen(false);
    } catch (error) {
      console.error('Error updating course:', error.response?.data || error.message);
      setError(error.response?.data?.message || 'Failed to update course. Please try again.');
    }
  };

  const handleUnenroll = async (courseId) => {
    try {
      await axios.delete(`http://localhost:8080/enrollments/unenroll`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        },
        data: { courseId }
      });
      fetchEnrollments();
    } catch (err) {
      console.error('Unenrollment failed:', err);
      setError('Could not unenroll from course.');
    }
  };

  const handleDeleteCourse = async (courseId) => {
    try {
      await axios.delete(`http://localhost:8080/courses/${courseId}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      fetchCourses();
    } catch (error) {
      console.error('Error deleting course:', error);
      setError('Failed to delete course. Please try again.');
    }
  };

  const handleEnroll = async (courseId) => {
    try {
      await axios.post(`http://localhost:8080/enrollments/enroll`, { courseId }, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      });
      fetchEnrollments();
    } catch (err) {
      console.error('Enrollment failed:', err);
      setError('Could not enroll in course. You may already be enrolled or the course may be full.');
    }
  };

  const handleCourseClick = (courseId) => {
    if (enrolledCourses.includes(courseId) || user?.role !== 'STUDENT') {
      navigate(`/courses/${courseId}`);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 py-8 px-4 sm:px-6 lg:px-8">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 2, repeat: Infinity, ease: 'linear' }}
          className="h-12 w-12 rounded-full border-4 border-purple-500 border-t-transparent"
        />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 py-8 px-4 sm:px-6 lg:px-8">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-7xl mx-auto"
      >
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-2">
              <BookOpenIcon className="h-8 w-8 text-purple-600" />
              Available Courses
            </h1>
            <p className="text-gray-600 mt-2">Explore our comprehensive learning programs</p>
          </div>

          <div className="flex gap-3">
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={handleRefresh}
              disabled={refreshing}
              className="flex items-center gap-2 px-4 py-2 bg-white border border-gray-300 rounded-lg shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50"
            >
              <ArrowPathIcon className={`h-4 w-4 ${refreshing ? 'animate-spin' : ''}`} />
              Refresh
            </motion.button>

            {user?.role === 'ADMIN' && (
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => {
                  setEditingCourse(null);
                  setIsModalOpen(true);
                }}
                className="flex items-center gap-2 px-4 py-2 bg-purple-600 text-white rounded-lg text-sm font-medium hover:bg-purple-700"
              >
                <PlusIcon className="h-4 w-4" />
                Add Course
              </motion.button>
            )}
          </div>
        </div>

        {error && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 text-red-700 rounded"
          >
            <p>{error}</p>
          </motion.div>
        )}

        {courses.length === 0 ? (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="bg-white rounded-xl shadow-sm p-8 text-center"
          >
            <AcademicCapIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-lg font-medium text-gray-900">No courses available</h3>
            <p className="mt-1 text-gray-500">Check back later or contact support.</p>
            {user?.role === 'ADMIN' && (
              <button
                onClick={() => setIsModalOpen(true)}
                className="mt-4 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
              >
                Create Your First Course
              </button>
            )}
          </motion.div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {courses.map((course) => {
              const isEnrolled = enrolledCourses.includes(course.id);
              const showAssignSelf = user?.role === 'INSTRUCTOR' && !course.instructor;
              const isAdmin = user?.role === 'ADMIN';

              return (
                <motion.div
                  key={course.id}
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ duration: 0.3 }}
                  whileHover={{ y: -5 }}
                  className={`bg-white rounded-xl shadow-lg overflow-hidden border border-gray-200 transition-all relative ${
                    isEnrolled || user?.role !== 'STUDENT' ? 'cursor-pointer hover:shadow-xl' : ''
                  }`}
                  onClick={() => handleCourseClick(course.id)}
                >
                  {isAdmin && (
                    <div className="absolute top-3 right-3 flex gap-2 z-10">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setEditingCourse(course);
                          setIsModalOpen(true);
                        }}
                        className="p-1.5 bg-blue-100 text-blue-600 rounded-full hover:bg-blue-200"
                        title="Edit Course"
                      >
                        <PencilIcon className="h-4 w-4" />
                      </button>

                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          if (window.confirm('Are you sure you want to delete this course?')) {
                            handleDeleteCourse(course.id);
                          }
                        }}
                        className="p-1.5 bg-red-100 text-red-600 rounded-full hover:bg-red-200"
                        title="Delete Course"
                      >
                        <TrashIcon className="h-4 w-4" />
                      </button>

                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setCurrentCourse(course);
                          setIsAssignModalOpen(true);
                        }}
                        className="p-1.5 bg-yellow-100 text-yellow-600 rounded-full hover:bg-yellow-200"
                        title="Assign Instructor"
                      >
                        <UserPlusIcon className="h-4 w-4" />
                      </button>
                    </div>
                  )}

                  <div className="p-6">
                    <div className="flex items-center justify-between mb-3">
                      <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                        {course.category || 'General'}
                      </span>
                      <span className="flex items-center text-sm text-gray-500">
                        <ClockIcon className="h-4 w-4 mr-1" />
                        {course.duration || 'N/A'} hrs
                      </span>
                    </div>

                    <h3 className="text-xl font-bold text-gray-900 mb-2 line-clamp-2">{course.title}</h3>

                    <p className="text-gray-600 mb-4 line-clamp-3">{course.description}</p>

                    <div className="flex items-center justify-between mt-4">
                    <div className="flex items-center">
  <div className="h-8 w-8 rounded-full bg-purple-200 flex items-center justify-center text-purple-600 font-medium">
    {course.instructor?.firstName?.charAt(0) || 'I'}
  </div>
  <div className="ml-2 text-sm font-medium text-gray-700">
    {course.instructor?.firstName ? 
      `${course.instructor.firstName}${course.instructor.lastName ? ' ' + course.instructor.lastName : ''}` 
      : 'No Instructor'}
  </div>
</div>

                      <div className="flex gap-2">
                        {showAssignSelf && (
                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              handleAssignSelf(course.id);
                            }}
                            className="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"
                          >
                            Assign Self
                          </button>
                        )}

                        {user?.role === 'STUDENT' && (
                          isEnrolled ? (
                            <button
                              onClick={(e) => {
                                e.stopPropagation();
                                handleUnenroll(course.id);
                              }}
                              className="px-3 py-1 bg-red-100 text-red-600 rounded hover:bg-red-200 text-sm"
                            >
                              Unenroll
                            </button>
                          ) : (
                            <button
                              onClick={(e) => {
                                e.stopPropagation();
                                handleEnroll(course.id);
                              }}
                              className="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700 text-sm"
                            >
                              Enroll
                            </button>
                          )
                        )}
                      </div>
                    </div>
                  </div>
                </motion.div>
              );
            })}
          </div>
        )}
      </motion.div>

      <CourseFormModal
  isOpen={isModalOpen}
  setIsOpen={setIsModalOpen}
  onCreate={handleCreateCourse}
  onUpdate={handleUpdateCourse}
  course={editingCourse}
  onClose={() => {
    setIsModalOpen(false);
    setEditingCourse(null);
  }}
/>

<AssignInstructorModal
        isOpen={isAssignModalOpen}
        setIsOpen={setIsAssignModalOpen}
        onAssign={handleAssignInstructor}
        course={currentCourse}
        instructors={instructors}
        onClose={() => setIsAssignModalOpen(false)}
      />
    </div>
  );
};

export default Courses;
