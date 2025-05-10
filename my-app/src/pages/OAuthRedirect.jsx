import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../context/AuthContext.js'; // عدل المسار حسب هيكل المشروع

const OAuthRedirect = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { login } = useAuth();

  useEffect(() => {
    const handleOAuthRedirect = async () => {
      const query = new URLSearchParams(location.search);
      const token = query.get('token');
      const error = query.get('error');

      if (error) {
        console.error('OAuth Error:', error);
        navigate('/login', { state: { error: 'Authentication failed' } });
        return;
      }

      if (!token) {
        navigate('/login');
        return;
      }

      try {
        // تخزين التوكن في localStorage
        localStorage.setItem('accessToken', token);

        // جلب بيانات المستخدم
        const response = axios.get('http://localhost:8080/auth/me', {
          headers: { 
            Authorization: `Bearer ${token}` 
          }
        })
        

        // تسجيل الدخول باستخدام البيانات
        login(response.data, token);
        
        // التوجيه إلى Dashboard
        navigate('/dashboard', { replace: true });
      } catch (err) {
        console.error('Failed to authenticate:', err);
        localStorage.removeItem('accessToken');
        navigate('/login', { state: { error: 'Authentication failed' } });
      }
    };

    handleOAuthRedirect();
  }, [location, navigate, login]);

  return (
    <div className="flex justify-center items-center h-screen">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mx-auto"></div>
        <p className="mt-4 text-lg">جاري تحميل بياناتك...</p>
      </div>
    </div>
  );
};

export default OAuthRedirect;