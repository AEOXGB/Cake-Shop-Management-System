package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.User;
import com.fr.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setName("Test User");
        testUser.setPhone("13800138000");
        testUser.setAddress("test address");
    }

    @Test
    @DisplayName("testFindUsers")
    void testFindUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(testUser);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(userList);
        List<User> result = userService.findUsers(queryWrapper);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("testAddUser_Success")
    void testAddUser_Success() {
        when(userMapper.insert(any(User.class))).thenReturn(1);
        int result = userService.addUser(testUser);
        assertEquals(1, result);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("testUpdateUser_Success")
    void testUpdateUser_Success() {
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        int result = userService.updateUser(testUser);
        assertEquals(1, result);
        verify(userMapper, times(1)).updateById(any(User.class));
    }
}