package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Goods;
import com.fr.javaBean.Type;
import com.fr.mapper.GoodsMapper;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.TypeMapper;
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
class GoodsServiceTest {

    @Mock
    private GoodsMapper goodsMapper;

    @Mock
    private TypeMapper typeMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private GoodsServiceImpl goodsService;

    private Goods testGoods;

    @BeforeEach
    void setUp() {
        testGoods = new Goods();
        testGoods.setId(1);
        testGoods.setName("test cake");
        testGoods.setPrice("99.9");
        testGoods.setStock(100);
        testGoods.setTypeId(1);
        testGoods.setCover("/picture/test.jpg");
    }

    @Test
    @DisplayName("testFindGoods")
    void testFindGoods() {
        List<Goods> goodsList = new ArrayList<>();
        goodsList.add(testGoods);
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        when(goodsMapper.selectList(any(QueryWrapper.class))).thenReturn(goodsList);
        List<Goods> result = goodsService.findGoods(queryWrapper);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("testUpdateGoods_Success")
    void testUpdateGoods_Success() {
        testGoods.setTypeId(1);
        List<Type> typeList = new ArrayList<>();
        typeList.add(new Type());
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        when(typeMapper.selectList(any(QueryWrapper.class))).thenReturn(typeList);
        when(goodsMapper.update(any(Goods.class), any(QueryWrapper.class))).thenReturn(1);
        boolean result = goodsService.updateGoods(testGoods, queryWrapper);
        assertTrue(result);
        verify(goodsMapper, times(1)).update(any(Goods.class), any(QueryWrapper.class));
    }

    @Test
    @DisplayName("testUpdateGoods_Failure")
    void testUpdateGoods_Failure() {
        testGoods.setTypeId(1);
        List<Type> typeList = new ArrayList<>();
        typeList.add(new Type());
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        when(typeMapper.selectList(any(QueryWrapper.class))).thenReturn(typeList);
        when(goodsMapper.update(any(Goods.class), any(QueryWrapper.class))).thenReturn(0);
        boolean result = goodsService.updateGoods(testGoods, queryWrapper);
        assertFalse(result);
    }

    @Test
    @DisplayName("testUpdateGoods_InvalidTypeId")
    void testUpdateGoods_InvalidTypeId() {
        testGoods.setTypeId(999);
        List<Type> typeList = new ArrayList<>();
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        when(typeMapper.selectList(any(QueryWrapper.class))).thenReturn(typeList);
        assertThrows(RuntimeException.class, () -> {
            goodsService.updateGoods(testGoods, queryWrapper);
        });
    }

    @Test
    @DisplayName("testCountGoods")
    void testCountGoods() {
        when(goodsMapper.selectCount(null)).thenReturn(10L);
        long result = goodsService.countGoods();
        assertEquals(10, result);
    }
}