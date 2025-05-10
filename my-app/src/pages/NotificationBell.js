// src/components/NotificationBell.js
import React, { useState } from 'react';
import { 
  Badge, 
  IconButton, 
  Popover, 
  List, 
  ListItem, 
  ListItemText, 
  Typography,
  Box,
  Button
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useNotifications } from '../context/NotificationContext.js';
import { AuthContext } from '../context/AuthContext.js';

const NotificationBell = () => {
  const [anchorEl, setAnchorEl] = useState(null);
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications();

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const id = open ? 'notification-popover' : undefined;

  return (
    <>
      <IconButton 
        color="inherit" 
        aria-label="notifications"
        onClick={handleClick}
      >
        <Badge badgeContent={unreadCount} color="error">
          <NotificationsIcon />
        </Badge>
      </IconButton>

      <Popover
        id={id}
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
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
          <Typography variant="h6" sx={{ mb: 1 }}>
            Notifications
            {unreadCount > 0 && (
              <Button 
                size="small" 
                sx={{ ml: 2 }}
                onClick={markAllAsRead}
              >
                Mark all as read
              </Button>
            )}
          </Typography>
          
          {notifications.length === 0 ? (
            <Typography variant="body2" sx={{ p: 2 }}>
              No notifications
            </Typography>
          ) : (
            <List dense>
              {notifications.map((notification) => (
                <ListItem 
                  key={notification.id}
                  sx={{ 
                    bgcolor: notification.read ? 'background.paper' : 'action.selected',
                    cursor: 'pointer',
                    '&:hover': { bgcolor: 'action.hover' }
                  }}
                  onClick={() => {
                    markAsRead(notification.id);
                    // Handle notification click (e.g., navigate to course)
                  }}
                >
                  <ListItemText
                    primary={notification.title}
                    secondary={notification.message}
                    primaryTypographyProps={{
                      fontWeight: notification.read ? 'normal' : 'bold'
                    }}
                  />
                  <Typography variant="caption" color="text.secondary">
                    {new Date(notification.createdAt).toLocaleTimeString()}
                  </Typography>
                </ListItem>
              ))}
            </List>
          )}
        </Box>
      </Popover>
    </>
  );
};

export default NotificationBell;