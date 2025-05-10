import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { motion } from 'framer-motion';
import {
  PencilIcon,
  XMarkIcon,
  CheckIcon,
  UserIcon,
  LockClosedIcon,
  PhotoIcon,
  TrashIcon
} from '@heroicons/react/24/solid';

const Profile = () => {
  const [profile, setProfile] = useState({
    firstName: '',
    lastName: '',
    username: '',
    profilePicture: null
  });
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState({ text: '', type: '' });
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [originalProfile, setOriginalProfile] = useState(null);

  const token = localStorage.getItem('accessToken');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setIsLoading(true);
        const response = await axios.get('http://localhost:8080/user/my-profile', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
        const data = response.data;
        console.log('Full profile response:', response);
        console.log('Profile data:', data);
        console.log('Profile links:', data._links);
        
        // Get profile picture URL from links
        const profilePictureLink = data._links?.['profile-picture']?.href;
        console.log('Profile picture URL from links:', profilePictureLink);
        
        const profileData = {
          firstName: data.firstName,
          lastName: data.lastName,
          username: data.username,
          profilePicture: profilePictureLink
        };
        
        setProfile(profileData);
        setOriginalProfile(profileData);
        setPreviewUrl(profilePictureLink);
        setError(null);
      } catch (error) {
        console.error('Error fetching profile:', error);
        setError('Failed to load profile data');
        setMessage({ text: 'Failed to load profile data', type: 'error' });
      } finally {
        setIsLoading(false);
      }
    };

    fetchProfile();
  }, [token]);

  const validatePassword = (password) => {
    if (password && password.length < 8) {
      return 'Password must be at least 8 characters long';
    }
    return null;
  };

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      return 'Please enter a valid email address';
    }
    return null;
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      console.log('Selected file:', file);
      // Validate file type
      if (!file.type.startsWith('image/')) {
        setMessage({ text: 'Please select an image file', type: 'error' });
        return;
      }
      // Validate file size (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        setMessage({ text: 'File size should be less than 5MB', type: 'error' });
        return;
      }
      setSelectedFile(file);
      const previewUrl = URL.createObjectURL(file);
      console.log('Generated preview URL:', previewUrl);
      setPreviewUrl(previewUrl);
    }
  };

  const handleCancel = () => {
    if (originalProfile) {
      setProfile(originalProfile);
      setPreviewUrl(originalProfile.profilePicture);
    }
    setSelectedFile(null);
    setPassword('');
    setMessage({ text: '', type: '' });
    setIsEditing(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const passwordError = validatePassword(password);
    if (passwordError) {
      setMessage({ text: passwordError, type: 'error' });
      return;
    }

    const emailError = validateEmail(profile.username);
    if (emailError) {
      setMessage({ text: emailError, type: 'error' });
      return;
    }

    try {
      setIsLoading(true);
      const formData = new FormData();
      formData.append('firstName', profile.firstName);
      formData.append('lastName', profile.lastName);
      if (password) formData.append('password', password);
      if (selectedFile) {
        console.log('Selected file:', selectedFile);
        formData.append('profilePicture', selectedFile);
      } else if (previewUrl === null) {
        formData.append('profilePicture', new File([], 'remove-profile-picture', { type: 'application/octet-stream' }));
      }

      console.log('Sending form data:', Object.fromEntries(formData));
      const response = await axios.put('/user/update-profile', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data'
        }
      });
      
      console.log('Full update response:', response);
      console.log('Response data:', response.data);
      console.log('Response links:', response.data._links);
      
      // Store new token if it's returned in the response
      const tokenLink = response.data._links?.token;
      if (tokenLink && tokenLink.href) {
        localStorage.setItem('accessToken', tokenLink.href);
      }

      // Update profile picture URL from response
      const profilePictureLink = response.data._links?.['profile-picture']?.href;
      console.log('Profile picture link from response:', profilePictureLink);
      
      const updatedProfile = {
        ...profile,
        profilePicture: profilePictureLink
      };
      
      setProfile(updatedProfile);
      setOriginalProfile(updatedProfile);
      setPreviewUrl(profilePictureLink);
      
      setMessage({ text: response.data.content, type: 'success' });
      setIsEditing(false);
      setPassword('');
      setSelectedFile(null);
    } catch (error) {
      console.error('Update failed:', error);
      setMessage({ 
        text: error.response?.data?.content || 'Failed to update profile', 
        type: 'error' 
      });
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading && !profile.username) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-purple-500"></div>
      </div>
    );
  }

  if (error && !profile.username) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-red-500 text-center">
          <p className="text-xl font-semibold">{error}</p>
          <button 
            onClick={() => window.location.reload()}
            className="mt-4 px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600"
          >
            Try Again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 py-12 px-4 sm:px-6 lg:px-8">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-3xl mx-auto"
      >
        <div className="bg-white rounded-3xl shadow-xl overflow-hidden">
          <div className="bg-gradient-to-r from-purple-600 to-blue-500 p-6 text-center">
            <div className="flex justify-between items-center">
              <h1 className="text-2xl font-bold text-white">Your Profile</h1>
              {!isEditing ? (
                <button
                  onClick={() => setIsEditing(true)}
                  className="flex items-center gap-1 px-4 py-2 bg-white/20 hover:bg-white/30 rounded-full text-white text-sm font-medium transition-all"
                >
                  <PencilIcon className="h-4 w-4" />
                  Edit Profile
                </button>
              ) : (
                <div className="flex gap-2">
                  <button
                    onClick={handleCancel}
                    className="flex items-center gap-1 px-3 py-1 bg-white/20 hover:bg-white/30 rounded-full text-white text-sm font-medium transition-all"
                  >
                    <XMarkIcon className="h-4 w-4" />
                    Cancel
                  </button>
                  <button
                    onClick={handleSubmit}
                    disabled={isLoading}
                    className="flex items-center gap-1 px-3 py-1 bg-white/20 hover:bg-white/30 rounded-full text-white text-sm font-medium transition-all disabled:opacity-50"
                  >
                    {isLoading ? (
                      <div className="h-4 w-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                    ) : (
                      <>
                        <CheckIcon className="h-4 w-4" />
                        Save
                      </>
                    )}
                  </button>
                </div>
              )}
            </div>
            
            <div className="relative mt-6">
              <div className="h-24 w-24 rounded-full overflow-hidden bg-purple-200 flex items-center justify-center">
                {previewUrl ? (
                  <img 
                    src={previewUrl} 
                    alt="Profile" 
                    className="w-full h-full object-cover"
                    onError={(e) => {
                      console.error('Error loading preview image:', e);
                      setPreviewUrl(null);
                    }}
                  />
                ) : (
                  <div className="h-full w-full flex items-center justify-center bg-purple-500 text-white text-2xl font-bold">
                    {`${profile.firstName.charAt(0)}${profile.lastName.charAt(0)}`.toUpperCase()}
                  </div>
                )}
              </div>
              {isEditing && (
                <div className="absolute bottom-0 right-0 flex gap-2">
                  <label className="bg-purple-600 rounded-full p-2 shadow-md hover:bg-purple-700 transition-colors cursor-pointer">
                    <input
                      type="file"
                      accept="image/*"
                      onChange={handleFileChange}
                      className="hidden"
                    />
                    <PhotoIcon className="h-4 w-4 text-white" />
                  </label>
                  {previewUrl && (
                    <button 
                      onClick={() => {
                        setSelectedFile(null);
                        setPreviewUrl(null);
                      }}
                      className="bg-red-500 rounded-full p-2 shadow-md hover:bg-red-600 transition-colors"
                    >
                      <TrashIcon className="h-4 w-4 text-white" />
                    </button>
                  )}
                </div>
              )}
            </div>
          </div>

          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            {message.text && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className={`p-3 rounded-lg text-sm ${message.type === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}
              >
                {message.text}
              </motion.div>
            )}

            <div>
              <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="firstName">
                First Name
              </label>
              {isEditing ? (
                <input
                  id="firstName"
                  type="text"
                  value={profile.firstName}
                  onChange={(e) => setProfile({ ...profile, firstName: e.target.value })}
                  className="w-full p-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  placeholder="First Name"
                  required
                />
              ) : (
                <div className="w-full p-3 rounded-lg bg-gray-50 text-gray-700">
                  {profile.firstName}
                </div>
              )}
            </div>

            <div>
              <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="lastName">
                Last Name
              </label>
              {isEditing ? (
                <input
                  id="lastName"
                  type="text"
                  value={profile.lastName}
                  onChange={(e) => setProfile({ ...profile, lastName: e.target.value })}
                  className="w-full p-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
                  placeholder="Last Name"
                  required
                />
              ) : (
                <div className="w-full p-3 rounded-lg bg-gray-50 text-gray-700">
                  {profile.lastName}
                </div>
              )}
            </div>

            <div>
              <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="username">
                Email
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <UserIcon className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="username"
                  type="email"
                  value={profile.username}
                  disabled
                  className="pl-10 w-full p-3 rounded-lg bg-gray-50 text-gray-700"
                  placeholder="Enter your email"
                />
              </div>
            </div>

            {isEditing && (
              <>
                <div className="pt-4 border-t border-gray-200">
                  <h3 className="text-lg font-medium text-gray-900">Change Password</h3>
                  <p className="text-sm text-gray-500 mt-1">Leave blank to keep current password</p>
                </div>

                <div>
                  <label className="block text-gray-700 text-sm font-medium mb-2" htmlFor="password">
                    New Password
                  </label>
                  <div className="relative">
                    <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                      <LockClosedIcon className="h-5 w-5 text-gray-400" />
                    </div>
                    <input
                      id="password"
                      type="password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="pl-10 w-full p-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-purple-500"
                      placeholder="••••••••"
                      minLength={8}
                    />
                  </div>
                </div>
              </>
            )}
          </form>
        </div>
      </motion.div>
    </div>
  );
};

export default Profile;
