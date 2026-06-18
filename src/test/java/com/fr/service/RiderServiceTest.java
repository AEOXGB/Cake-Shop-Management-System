package com.fr.service;

import com.fr.javaBean.Rider;
import com.fr.mapper.RiderMapper;
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
class RiderServiceTest {

    @Mock
    private RiderMapper riderMapper;

    @InjectMocks
    private RiderServiceImpl riderService;

    private Rider testRider;

    @BeforeEach
    void setUp() {
        testRider = new Rider();
        testRider.setId(1);
        testRider.setPhone("13800138000");
        testRider.setPassword("password123");
        testRider.setName("Test Rider");
        testRider.setIdCard("123456199001011234");
        testRider.setStatus(1);
    }

    @Test
    @DisplayName("testLogin_Success")
    void testLogin_Success() {
        when(riderMapper.selectOne(any())).thenReturn(testRider);
        Rider result = riderService.login("13800138000", "password123");
        assertNotNull(result);
        assertEquals("Test Rider", result.getName());
    }

    @Test
    @DisplayName("testLogin_Failure_WrongPassword")
    void testLogin_Failure_WrongPassword() {
        when(riderMapper.selectOne(any())).thenReturn(testRider);
        Rider result = riderService.login("13800138000", "wrongpassword");
        assertNull(result);
    }

    @Test
    @DisplayName("testLogin_Failure_NotApproved")
    void testLogin_Failure_NotApproved() {
        testRider.setStatus(0);
        when(riderMapper.selectOne(any())).thenReturn(testRider);
        Rider result = riderService.login("13800138000", "password123");
        assertNull(result);
    }

    @Test
    @DisplayName("testLogin_Failure_NotFound")
    void testLogin_Failure_NotFound() {
        when(riderMapper.selectOne(any())).thenReturn(null);
        Rider result = riderService.login("13800138000", "password123");
        assertNull(result);
    }

    @Test
    @DisplayName("testGetRiderById")
    void testGetRiderById() {
        when(riderMapper.selectById(anyInt())).thenReturn(testRider);
        Rider result = riderService.getRiderById(1);
        assertNotNull(result);
        assertEquals("13800138000", result.getPhone());
    }

    @Test
    @DisplayName("testAddRider_Success")
    void testAddRider_Success() {
        when(riderMapper.insert(any(Rider.class))).thenReturn(1);
        boolean result = riderService.addRider(testRider);
        assertTrue(result);
        verify(riderMapper, times(1)).insert(any(Rider.class));
    }

    @Test
    @DisplayName("testAddRider_Failure")
    void testAddRider_Failure() {
        when(riderMapper.insert(any(Rider.class))).thenReturn(0);
        boolean result = riderService.addRider(testRider);
        assertFalse(result);
    }

    @Test
    @DisplayName("testUpdateRider_Success")
    void testUpdateRider_Success() {
        when(riderMapper.updateById(any(Rider.class))).thenReturn(1);
        boolean result = riderService.updateRider(testRider);
        assertTrue(result);
        verify(riderMapper, times(1)).updateById(any(Rider.class));
    }

    @Test
    @DisplayName("testDeleteRider_Success")
    void testDeleteRider_Success() {
        when(riderMapper.deleteById(anyInt())).thenReturn(1);
        boolean result = riderService.deleteRider(1);
        assertTrue(result);
        verify(riderMapper, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("testGetAllRiders")
    void testGetAllRiders() {
        List<Rider> riderList = new ArrayList<>();
        riderList.add(testRider);
        when(riderMapper.selectList(null)).thenReturn(riderList);
        List<Rider> result = riderService.getAllRiders();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("testGetRiderByPhone")
    void testGetRiderByPhone() {
        when(riderMapper.selectOne(any())).thenReturn(testRider);
        Rider result = riderService.getRiderByPhone("13800138000");
        assertNotNull(result);
        assertEquals("Test Rider", result.getName());
    }
}