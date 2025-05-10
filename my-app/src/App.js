import { useEffect } from 'react';
import { Routes, Route, useNavigate, Navigate } from 'react-router-dom';
import Home from './pages/Home.js';
import Login from './pages/Login.js';
import Register from './pages/Register.jsx';
import Dashboard from './pages/Dashboard.jsx';
import PrivateRoute from './components/PrivateRoute.jsx';
import Profile from './pages/Profile.js';
import Courses from './pages/Courses.jsx';
import CourseDetails from './pages/CourseDetails.js';
import CreateCourseModal from './pages/CourseFormModal.js';
import AssignmentSubmission from './pages/AssignmentSubmission.js';
import CreateAssignment from './pages/CreateAssignment.js';
import NotificationBell from './pages/NotificationBell.js';
import CourseFormModal from './pages/CourseFormModal.js';
import OAuthRedirect  from './pages/OAuthRedirect.jsx';
import QuizPage from './pages/QuizPage.js';
import CreateQuizPage from './pages/CreateQuiz.jsx';

NotificationBell

function App() {
  const navigate = useNavigate();

  useEffect(() => {
    const handleAuthCheck = () => {
      const token = localStorage.getItem('accessToken');
      const isAuthPage = ['/login', '/register'].includes(window.location.pathname);

      if (!token && !isAuthPage) {
        navigate('/', { replace: true }); // توجيه إلى الصفحة الرئيسية
      } else if (token && isAuthPage) {
        navigate('/dashboard', { replace: true });
      }
    };

    handleAuthCheck();
    window.addEventListener('storage', handleAuthCheck);

    return () => {
      window.removeEventListener('storage', handleAuthCheck);
    };
  }, [navigate]);

  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/oauth-redirect" element={<OAuthRedirect />} />

      <Route element={<PrivateRoute />}>
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/courses" element={<Courses />} />
        <Route path="/courses/:courseId" element={<CourseDetails />} />
        <Route path="/courses/:courseId/create-assignment" element={<CreateAssignment />} />
        <Route path="/NotificationBell" element={<NotificationBell />} />
        <Route path="/CourseFormModal" element={<CourseFormModal />} />


        
         <Route path="/quiz/:quizId" element={<QuizPage />} />
        <Route path="/courses/:courseId/quizzes/new" element={<CreateQuizPage />} />
        
        <Route
          path="/courses/create"
          element={
            <CreateCourseModal 
              isOpen={true} 
              onClose={() => navigate('/courses', { replace: true })} 
            />
          }
        />
<Route path="/assignments/:assignmentId" element={<AssignmentSubmission />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
