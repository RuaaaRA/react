import { motion } from 'framer-motion';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { LockClosedIcon, UserIcon } from '@heroicons/react/24/outline';
import { FcGoogle } from 'react-icons/fc/index.js';
import axios from 'axios';

const Login = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    firstName: '',
    lastName: ''
  });
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [isGoogleLoading, setIsGoogleLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  // Handle OAuth2 redirect and token storage
  useEffect(() => {
    const query = new URLSearchParams(location.search);
    const accessToken = query.get('access_token');
    const refreshToken = query.get('refresh_token');

    if (accessToken) {
      localStorage.setItem('accessToken', accessToken);
      if (refreshToken) {
        localStorage.setItem('refreshToken', refreshToken);
      }
      // Set default axios authorization header
      axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
      navigate('/dashboard', { replace: true });
    }

    // Redirect to dashboard if already logged in
    const token = localStorage.getItem('accessToken');
    if (token && location.pathname === '/login') {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      navigate('/dashboard', { replace: true });
    }
  }, [navigate, location]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error when user types
    if (errors[name]) {
      setErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };

  const validate = () => {
    const newErrors = {};
    
    if (!isLogin) {
      if (!formData.firstName.trim()) newErrors.firstName = 'First name is required';
      if (!formData.lastName.trim()) newErrors.lastName = 'Last name is required';
    }

    if (!formData.username.trim()) {
      newErrors.username = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.username)) {
      newErrors.username = 'Please enter a valid email address';
    }

    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setIsLoading(true);
    setErrors({});

    try {
      if (isLogin) {
        const response = await axios.post('http://localhost:8080/auth/login', {
          username: formData.username,
          password: formData.password
        });

        const { access_token, refresh_token } = response.data;
        if (!access_token) throw new Error('No access token received');

        localStorage.setItem('accessToken', access_token);
        if (refresh_token) localStorage.setItem('refreshToken', refresh_token);
        axios.defaults.headers.common['Authorization'] = `Bearer ${access_token}`;
        
        navigate('/dashboard', { replace: true });
      } else {
        const response = await axios.post('http://localhost:8080/auth/register', {
          username: formData.username,
          password: formData.password,
          firstName: formData.firstName,
          lastName: formData.lastName
        });

        setIsLogin(true);
        setFormData(prev => ({
          ...prev,
          password: '',
          firstName: '',
          lastName: ''
        }));
        setErrors({
          api: response.data.message || 'Registered successfully! Please log in.'
        });
      }
    } catch (error) {
      console.error('Authentication error:', error);
      setErrors({
        api: error.response?.data?.message || 
             error.message || 
             'Authentication failed. Please try again.'
      });
    } finally {
      setIsLoading(false);
    }
  };

  const initiateGoogleLogin = async () => {
    try {
      setIsGoogleLoading(true);
      const response = await axios.get('http://localhost:8080/auth/google-link');
      
      if (!response.data?._links?.['google-login']?.href) {
        throw new Error('Invalid Google login URL response');
      }
      
      window.location.href = response.data._links['google-login'].href;
    } catch (error) {
      console.error('Google login error:', error);
      setErrors({
        api: error.response?.data?.message || 
             'Failed to initiate Google login. Please try again.'
      });
    } finally {
      setIsGoogleLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 flex items-center justify-center p-4">
      <motion.div
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md"
      >
        <div className="bg-white rounded-3xl shadow-2xl overflow-hidden">
          <div className="bg-gradient-to-r from-purple-600 to-blue-500 p-6 text-center">
            <h2 className="text-3xl font-bold text-white">
              {isLogin ? 'Welcome Back!' : 'Join Us Today!'}
            </h2>
            <p className="text-purple-100 mt-2">
              {isLogin
                ? 'Login to continue your learning journey'
                : 'Create an account to get started'}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="p-8">
            {errors.api && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className={`mb-4 p-3 rounded-lg text-sm ${
                  errors.api.toLowerCase().includes('success')
                    ? 'bg-green-100 text-green-700'
                    : 'bg-red-100 text-red-700'
                }`}
              >
                {errors.api}
              </motion.div>
            )}

            {!isLogin && (
              <>
                <motion.div 
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: 0.1 }}
                  className="mb-4"
                >
                  <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="firstName">
                    First Name
                  </label>
                  <input
                    id="firstName"
                    name="firstName"
                    type="text"
                    value={formData.firstName}
                    onChange={handleChange}
                    className={`w-full p-3 rounded-lg border ${errors.firstName ? 'border-red-500' : 'border-gray-300'} focus:outline-none focus:ring-2 focus:ring-purple-500`}
                    placeholder="John"
                  />
                  {errors.firstName && <p className="text-red-500 text-xs mt-1">{errors.firstName}</p>}
                </motion.div>

                <motion.div 
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: 0.2 }}
                  className="mb-4"
                >
                  <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="lastName">
                    Last Name
                  </label>
                  <input
                    id="lastName"
                    name="lastName"
                    type="text"
                    value={formData.lastName}
                    onChange={handleChange}
                    className={`w-full p-3 rounded-lg border ${errors.lastName ? 'border-red-500' : 'border-gray-300'} focus:outline-none focus:ring-2 focus:ring-purple-500`}
                    placeholder="Doe"
                  />
                  {errors.lastName && <p className="text-red-500 text-xs mt-1">{errors.lastName}</p>}
                </motion.div>
              </>
            )}

            <div className="mb-4">
              <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="username">
                Email
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <UserIcon className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="username"
                  name="username"
                  type="email"
                  value={formData.username}
                  onChange={handleChange}
                  className={`pl-10 w-full p-3 rounded-lg border ${errors.username ? 'border-red-500' : 'border-gray-300'} focus:outline-none focus:ring-2 focus:ring-purple-500`}
                  placeholder="Enter your email"
                />
              </div>
              {errors.username && <p className="text-red-500 text-xs mt-1">{errors.username}</p>}
            </div>

            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: isLogin ? 0.2 : 0.4 }}
              className="mb-6"
            >
              <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="password">
                Password
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <LockClosedIcon className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="password"
                  name="password"
                  type="password"
                  value={formData.password}
                  onChange={handleChange}
                  className={`pl-10 w-full p-3 rounded-lg border ${errors.password ? 'border-red-500' : 'border-gray-300'} focus:outline-none focus:ring-2 focus:ring-purple-500`}
                  placeholder="••••••••"
                />
              </div>
              {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
            </motion.div>

            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={isLoading}
              className="w-full bg-purple-600 hover:bg-purple-700 text-white font-bold py-3 px-4 rounded-lg transition-all duration-300 shadow-md hover:shadow-lg flex justify-center items-center"
            >
              {isLoading ? (
                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              ) : isLogin ? 'Login' : 'Sign Up'}
            </motion.button>

            <div className="mt-6">
              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-300"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-2 bg-white text-gray-500">
                    Or continue with
                  </span>
                </div>
              </div>

              <motion.button
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                type="button"
                onClick={initiateGoogleLogin}
                disabled={isGoogleLoading}
                className="w-full mt-6 bg-white border border-gray-300 rounded-lg shadow-sm py-2 px-4 flex items-center justify-center text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500"
              >
                {isGoogleLoading ? (
                  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-gray-700" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                ) : (
                  <>
                    <FcGoogle className="h-5 w-5 mr-2" />
                    Sign in with Google
                  </>
                )}
              </motion.button>
            </div>

            <div className="mt-6 text-center">
              <button
                type="button"
                onClick={() => {
                  setIsLogin(!isLogin);
                  setErrors({});
                }}
                className="text-purple-600 hover:text-purple-800 font-medium text-sm"
              >
                {isLogin ? "Don't have an account? Sign Up" : "Already have an account? Login"}
              </button>
            </div>
          </form>
        </div>

        <div className="mt-8 text-center">
          <Link to="/" className="text-gray-600 hover:text-gray-800 text-sm flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 10H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clipRule="evenodd" />
            </svg>
            Back to Home
          </Link>
        </div>
      </motion.div>
    </div>
  );
};

export default Login;