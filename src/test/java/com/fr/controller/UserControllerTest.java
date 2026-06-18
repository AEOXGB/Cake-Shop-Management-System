package com.fr.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("testIndex_RedirectToGoodsList")
    void testIndex_RedirectToGoodsList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("forward:/goods/goodsList"));
    }

    @Test
    @DisplayName("testLoginPage_Redirect")
    void testLoginPage_Redirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/login"));
    }

    @Test
    @DisplayName("testRegisterPage_NotFound")
    void testRegisterPage_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("testGoodsDetail_RequiresLogin")
    void testGoodsDetail_RequiresLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/goodsDetail").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/login"));
    }

    @Test
    @DisplayName("testCartList_RequiresLogin")
    void testCartList_RequiresLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cartList"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/login"));
    }

    @Test
    @DisplayName("testMyOrders_RequiresLogin")
    void testMyOrders_RequiresLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/myOrders"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/login"));
    }
}