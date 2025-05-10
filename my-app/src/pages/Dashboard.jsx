import React, { useContext, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  AppBar, Toolbar, Typography, Drawer, List, ListItem, 
  ListItemIcon, ListItemText, Box, Grid, Card, CardContent, 
  Avatar, IconButton, Divider, Menu, MenuItem, Badge,
  Popover, ListItemAvatar, Chip, Button
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  School as SchoolIcon,
  People as PeopleIcon,
  Assignment as AssignmentIcon,
  VideoLibrary as VideoLibraryIcon,
  Settings as SettingsIcon,
  Menu as MenuIcon,
  Logout as LogoutIcon,
  AccountCircle as AccountCircleIcon,
  Notifications as NotificationsIcon,
  Circle as CircleIcon
} from '@mui/icons-material';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import RadioButtonUncheckedIcon from '@mui/icons-material/RadioButtonUnchecked';

const Dashboard = () => {
  const { user, logout, isAuthenticated } = useContext(AuthContext);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
  const [notificationsAnchorEl, setNotificationsAnchorEl] = useState(null);
  const [stats, setStats] = useState({
    courses: 0,
    students: 0,
    instructors: 0,
    assignments: 0
  });
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const navigate = useNavigate();
  const [lessonProgress, setLessonProgress] = useState([]);

useEffect(() => {
  const fetchLessonProgress = async () => {
    if (user?.role === 'STUDENT') {
      try {
        const response = await axios.get('http://localhost:8080/lessons/progress/course/{courseId}', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        setLessonProgress(response.data);
      } catch (error) {
        console.error('Failed to fetch lesson progress:', error);
      }
    }
  };

  fetchLessonProgress();
}, [user]);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const response = await axios.get('http://localhost:8080/notifications', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        setNotifications(response.data);
        setUnreadCount(response.data.filter(n => !n.read).length);
      } catch (error) {
        console.error('فشل في جلب الإشعارات:', error);
      }
    };
  
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 30000); // تحديث كل 30 ثانية
  
    return () => clearInterval(interval);
  }, []);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleNotificationsClick = (event) => {
    setNotificationsAnchorEl(event.currentTarget);
  };

  const handleNotificationsClose = () => {
    setNotificationsAnchorEl(null);
  };

  const markAsRead = async (id) => {
    try {
      await axios.patch(`http://localhost:8080/notifications/${id}/read`);
      setNotifications(notifications.map(n => 
        n.id === id ? {...n, read: true} : n
      ));
      setUnreadCount(unreadCount - 1);
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      await axios.patch('http://localhost:8080/notifications/mark-all-read');
      setNotifications(notifications.map(n => ({...n, read: true})));
      setUnreadCount(0);
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleProfileClick = () => {
    navigate('/profile');
  };

  const drawer = (
    <div>
      <Toolbar />
      <Divider />
      <List>
        <ListItem button component={Link} to="/dashboard">
          <ListItemIcon><DashboardIcon /></ListItemIcon>
          <ListItemText primary="Dashboard" />
        </ListItem>
        <ListItem button component={Link} to="/courses">
          <ListItemIcon><SchoolIcon /></ListItemIcon>
          <ListItemText primary="Courses" />
        </ListItem>
      </List>
    </div>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        sx={{
          zIndex: (theme) => theme.zIndex.drawer + 1,
          backgroundColor: 'white',
          color: 'black',
          boxShadow: 'none',
          borderBottom: '1px solid rgba(0, 0, 0, 0.12)'
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            LMS Dashboard
          </Typography>
          <div>
            <IconButton
              size="large"
              aria-label="show notifications"
              color="inherit"
              onClick={handleNotificationsClick}
            >
              <Badge badgeContent={unreadCount} color="error">
                <NotificationsIcon />
              </Badge>
            </IconButton>
            <Popover
  open={Boolean(notificationsAnchorEl)}
  anchorEl={notificationsAnchorEl}
  onClose={handleNotificationsClose}
  anchorOrigin={{
    vertical: 'bottom',
    horizontal: 'right',
  }}
  transformOrigin={{
    vertical: 'top',
    horizontal: 'right',
  }}
>
  <Box sx={{ width: 360, p: 2 }}>
    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
      <Typography variant="h6">Notifications</Typography>
      {unreadCount > 0 && (
        <Button size="small" onClick={markAllAsRead}>
          Mark all as read
        </Button>
      )}
    </Box>
    <Divider />
   <List sx={{ maxHeight: 400, overflow: 'auto' }}>
  {notifications.length > 0 ? (
    notifications.map((notification) => (
      <ListItem 
        key={notification.id}
        sx={{ 
          backgroundColor: notification.read ? '#f9f9f9' : '#e3f2fd',
          borderLeft: notification.read ? 'none' : '4px solid #2196f3',
          mb: 1,
          borderRadius: 1
        }}
      >
        <ListItemAvatar>
          <Avatar sx={{ bgcolor: 'primary.main' }}>
            <SchoolIcon />
          </Avatar>
        </ListItemAvatar>
        <ListItemText
          primary={
            <Typography variant="subtitle1" fontWeight="bold">
              {notification.title}
            </Typography>
          }
          secondary={
            <>
              <Typography variant="body2">
                {notification.message}
              </Typography>
              <Chip 
                label={notification.courseName}
                size="small"
                sx={{ mt: 1 }}
                color="primary"
              />
            </>
          }
        />
      </ListItem>
    ))
  ) : (
    <ListItem>
      <ListItemText 
        primary="لا توجد إشعارات حاليا"
        sx={{ textAlign: 'center', color: 'text.secondary' }}
      />
    </ListItem>
  )}
</List>
    <Divider />
    <Box sx={{ display: 'flex', justifyContent: 'center', pt: 1 }}>
      <Button 
        component={Link} 
        to="/notifications" 
        size="small"
        sx={{ color: 'primary.main' }}
      >
        View all notifications
      </Button>
    </Box>
  </Box>
</Popover>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleMenu}
              color="inherit"
            >
              <Avatar alt={user?.firstName} src="/static/images/avatar/1.jpg" />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorEl}
              anchorOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              <MenuItem onClick={handleProfileClick}>
                <ListItemIcon><AccountCircleIcon fontSize="small" /></ListItemIcon>
                UserProfile
              </MenuItem>
              <MenuItem onClick={handleLogout}>
                <ListItemIcon><LogoutIcon fontSize="small" /></ListItemIcon>
                Logout
              </MenuItem>
            </Menu>
          </div>
        </Toolbar>
      </AppBar>

      <Box component="nav" sx={{ width: { sm: 240 }, flexShrink: { sm: 0 } }}>
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: 240 },
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: 240 },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      <Box
        component="main"
        sx={{ 
          flexGrow: 1, 
          p: 3, 
          width: { sm: `calc(100% - 240px)` },
          backgroundColor: '#f5f5f5',
          minHeight: '100vh'
        }}
      >
        <Toolbar />
        <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
          Welcome back, {user?.firstName || 'User'}!
        </Typography>

        <Grid container spacing={3}>
          {/* Stats Cards... */}
        </Grid>

        <Grid container spacing={3} sx={{ mt: 2 }}>
          {/* Recent Activity... */}
        </Grid>
      </Box>

      <Box
  component="main"
  sx={{ 
    flexGrow: 1, 
    p: 3, 
    width: { sm: `calc(100% - 240px)` },
    backgroundColor: '#f5f5f5',
    minHeight: '100vh'
  }}
>
  <Toolbar />
  <Typography variant="h4" gutterBottom sx={{ mb: 4 }}>
    Welcome back, {user?.firstName || 'User'}!
  </Typography>

  {user?.role === 'STUDENT' && (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              Your Course Progress
            </Typography>
            <Divider sx={{ my: 2 }} />
            {lessonProgress.length > 0 ? (
              <List>
                {lessonProgress.map((lesson, index) => (
                  <ListItem key={index}>
                    <ListItemIcon>
                      {lesson.completed ? (
                        <CheckCircleIcon color="success" />
                      ) : (
                        <RadioButtonUncheckedIcon />
                      )}
                    </ListItemIcon>
                    <ListItemText
                      primary={lesson.lessonTitle}
                      secondary={`Course: ${lesson.courseId}`}
                    />
                    <Chip
                      label={lesson.completed ? 'Completed' : 'In Progress'}
                      color={lesson.completed ? 'success' : 'warning'}
                    />
                  </ListItem>
                ))}
              </List>
            ) : (
              <Typography variant="body1" color="textSecondary">
                No progress data available.
              </Typography>
            )}
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  )}

  {user?.role === 'INSTRUCTOR' && (
    <Grid container spacing={3}>
      <Grid item xs={12} md={6}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              Course Management
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Button
              variant="contained"
              color="primary"
              component={Link}
              to="/courses/create"
              sx={{ mr: 2 }}
            >
              Create New Course
            </Button>
            <Button
              variant="outlined"
              component={Link}
              to="/courses"
            >
              View All Courses
            </Button>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12} md={6}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              Student Progress Overview
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Typography variant="body1">
              View and manage your students' progress
            </Typography>
            <Button
              variant="outlined"
              sx={{ mt: 2 }}
              component={Link}
              to="/progress"
            >
              View Progress Reports
            </Button>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  )}

  {user?.role === 'ADMIN' && (
    <Grid container spacing={3}>
      <Grid item xs={12} md={4}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              User Management
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Button
              variant="contained"
              color="primary"
              component={Link}
              to="/users"
              fullWidth
              sx={{ mb: 2 }}
            >
              Manage Users
            </Button>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12} md={4}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              System Settings
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Button
              variant="contained"
              color="secondary"
              component={Link}
              to="/settings"
              fullWidth
            >
              System Configuration
            </Button>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12} md={4}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>
              Reports
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Button
              variant="outlined"
              component={Link}
              to="/reports"
              fullWidth
              sx={{ mb: 1 }}
            >
              Generate Reports
            </Button>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  )}
</Box>
    </Box>
  );
};

export default Dashboard;