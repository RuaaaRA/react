import { createContext, useState, useEffect, useCallback, useMemo } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

// إنشاء سياق المصادقة
const AuthContext = createContext();

// مكون مزود السياق
export const AuthProvider = ({ children }) => {
  const [authState, setAuthState] = useState({
    user: null,
    isLoading: true,
    error: null,
    isAuthenticated: false
  });

  const navigate = useNavigate();

  // تكوين axios interceptors
  useEffect(() => {
    const requestInterceptor = axios.interceptors.request.use(config => {
      const token = localStorage.getItem('accessToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    const responseInterceptor = axios.interceptors.response.use(
      response => response,
      error => {
        if (error.response?.status === 401) {
          handleAutoLogout();
        }
        return Promise.reject(error);
      }
    );

    return () => {
      axios.interceptors.request.eject(requestInterceptor);
      axios.interceptors.response.eject(responseInterceptor);
    };
  }, []); // This only runs on mount

  // تسجيل الخروج التلقائي عند انتهاء الصلاحية
  const handleAutoLogout = useCallback(() => {
    localStorage.removeItem('accessToken');
    setAuthState(prev => ({
      ...prev,
      user: null,
      isAuthenticated: false,
      error: 'انتهت جلسة العمل، يرجى تسجيل الدخول مرة أخرى'
    }));
    navigate('/login', { replace: true, state: { from: 'session-expired' } });
  }, [navigate]);

  // التحقق من حالة تسجيل الدخول
  const checkUserLoggedIn = useCallback(async () => {
    try {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        throw new Error('لا يوجد token');
      }

      const response = await axios.get('http://localhost:8080/auth/verify');
      const userData = response.data?.user;

      if (!userData) {
        throw new Error('بيانات المستخدم غير صالحة');
      }

      setAuthState({
        user: {
          id: userData.id,
          username: userData.username,
          role: userData.role,
          firstName: userData.firstName,
          lastName: userData.lastName,
        },
        isLoading: false,
        error: null,
        isAuthenticated: true
      });

      return true;
    } catch (err) {
      console.error('فشل التحقق من المصادقة:', err);
      setAuthState(prev => ({
        ...prev,
        user: null,
        isLoading: false,
        error: err.message,
        isAuthenticated: false
      }));
      return false;
    }
  }, []);

  // تهيئة حالة المصادقة عند التحميل
  useEffect(() => {
    const initializeAuth = async () => {
      const token = localStorage.getItem('accessToken');
      if (!token) {
        setAuthState(prev => ({
          ...prev,
          isLoading: false
        }));
        return;
      }
      await checkUserLoggedIn();
    };
  
    initializeAuth();
  }, [checkUserLoggedIn]);
  
  // تسجيل الدخول
  const login = async (credentials) => {
    try {
      setAuthState(prev => ({ ...prev, isLoading: true, error: null }));
      
      const response = await axios.post('http://localhost:8080/auth/login', credentials);
      const { accessToken, user: userData } = response.data;

      if (!accessToken || !userData) {
        throw new Error('استجابة تسجيل الدخول غير صالحة');
      }

      localStorage.setItem('accessToken', accessToken);

      const userPayload = {
        id: userData.id,
        username: userData.username,
        role: userData.role,
        firstName: userData.firstName,
        lastName: userData.lastName,
      };

      setAuthState({
        user: userPayload,
        isLoading: false,
        error: null,
        isAuthenticated: true
      });

      return { success: true, user: userPayload };
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message;
      console.error('خطأ في تسجيل الدخول:', errorMessage);
      setAuthState(prev => ({
        ...prev,
        isLoading: false,
        error: errorMessage
      }));
      return { success: false, error: errorMessage };
    }
  };

  // التسجيل
  const register = async (userData) => {
    try {
      setAuthState(prev => ({ ...prev, isLoading: true, error: null }));
      
      const response = await axios.post('http://localhost:8080/auth/register', userData);
      
      setAuthState(prev => ({ ...prev, isLoading: false }));
      return { success: true, data: response.data };
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message;
      console.error('خطأ في التسجيل:', errorMessage);
      setAuthState(prev => ({
        ...prev,
        isLoading: false,
        error: errorMessage
      }));
      return { success: false, error: errorMessage };
    }
  };

  // تسجيل الخروج
  const logout = useCallback(() => {
    localStorage.removeItem('accessToken');
    setAuthState({
      user: null,
      isLoading: false,
      error: null,
      isAuthenticated: false
    });
    navigate('/login', { replace: true });
  }, [navigate]);

  // قيمة السياق المذكرة
  const contextValue = useMemo(() => ({
    ...authState,
    login,
    register,
    logout,
    checkUserLoggedIn
  }), [authState, logout, checkUserLoggedIn]);

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};

export { AuthContext };
