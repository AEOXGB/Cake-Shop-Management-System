package com.fr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Rider;
import com.fr.javaBean.User;
import com.fr.service.RiderService;
import com.fr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 * 所属模块：用户管理模块
 * 处理用户相关的请求，包括用户登录、注册、退出登录等功能
 * 请求路径前缀：/user
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RiderService riderService;

    /**
     * 用户登录方法
     * 处理用户登录请求，支持用户名/手机号+密码登录，同时支持骑手登录
     * @param request HttpServletRequest请求对象，包含用户名和密码参数
     * @return ModelAndView 登录成功跳转到对应页面，失败返回登录页面并提示错误信息
     */
    @RequestMapping("/login")
    public ModelAndView userLogin(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();

        // 获取或创建Session
        HttpSession session = request.getSession(true);
        System.out.println(session);

        // 从请求参数中获取用户名和密码
        String username = request.getParameter("username");
        String pwd = request.getParameter("passwd");

        // 验证用户名和密码不为空
        if (username != null && pwd != null && !"".equals(username.trim()) && !"".equals(pwd.trim())) {

            // 构建用户查询条件：密码匹配，且用户名或手机号匹配
            QueryWrapper<User> userQuery = new QueryWrapper<>();
            userQuery.eq("password", pwd);
            userQuery.and(wrapper -> wrapper.eq("username", username).or().eq("phone", username));

            // 查询用户列表
            List<User> users = userService.findUsers(userQuery);
            if (users == null || users.size() == 0) {
                // 用户表中未找到，尝试骑手登录
                Rider rider = riderService.login(username, pwd);
                if (rider != null) {
                    // 骑手登录成功，将骑手信息存入Session
                    session.setAttribute("rider", rider);
                    modelAndView.setViewName("redirect:/rider/index");
                    return modelAndView;
                }
                // 登录失败，销毁Session并返回错误提示
                session.invalidate();
                modelAndView.addObject("msg", "用户名/手机号或者密码错误！");
                modelAndView.setViewName("login");

            } else {
                // 获取第一个匹配的用户
                User user = users.get(0);
                
                // 检查用户是否已冻结（状态2表示冻结）
                if ("2".equals(user.getIsvalidate())) {
                    session.invalidate();
                    modelAndView.addObject("msg", "当前账号已经被冻结！");
                    modelAndView.setViewName("login");
                    return modelAndView;
                }
                
                // 检查用户是否已审核（状态0表示未审核）
                if ("0".equals(user.getIsvalidate())) {
                    session.invalidate();
                    modelAndView.addObject("msg", "你当前的账户未经过审核！");
                    modelAndView.setViewName("login");
                    return modelAndView;
                }
                
                // 登录成功，将用户信息存入Session
                session.setAttribute("user", user);
                modelAndView.addObject("users", user);

                // 根据用户角色跳转到不同页面
                if ("1".equals(user.getIsadmin())) {
                    // 管理员跳转到销售统计页面
                    modelAndView.setViewName("forward:/admin/orderStats");
                } else {
                    // 普通用户跳转到前台首页（商品列表）
                    modelAndView.setViewName("forward:/goods/goodsList");
                }
            }
        } else {
            // 参数为空，直接返回登录页面
            modelAndView.setViewName("login");
        }

        return modelAndView;


    }

    /**
     * 用户注册方法
     * 处理普通用户和骑手的注册请求，根据userType参数区分注册类型
     * @param request HttpServletRequest请求对象，包含注册信息（用户名、邮箱、密码、姓名、电话、地址、用户类型等）
     * @return ModelAndView 注册成功跳转到登录页面，失败返回注册页面并提示错误信息
     */
    @RequestMapping("/register")
    public ModelAndView registerUser(@Valid HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();

        // 获取注册表单参数
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String passwd = request.getParameter("passwd");
        String receiveName = request.getParameter("receiveName");
        String receivePhone = request.getParameter("receivePhone");
        String receiveAddress = request.getParameter("receiveAddress");
        String userType = request.getParameter("userType");
        int succ = 0;

        // 判断注册类型：骑手注册
        if ("rider".equals(userType)) {
            // 检查该手机号是否已注册为骑手
            Rider existingRider = riderService.getRiderByPhone(receivePhone);
            
            if (existingRider != null) {
                modelAndView.addObject("msg2", "该手机号已注册为骑手！");
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../register");
                return modelAndView;
            }

            // 格式化当前时间作为创建时间
            String createtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            // 构建骑手对象
            Rider rider = new Rider();
            rider.setPhone(receivePhone);
            rider.setPassword(passwd);
            rider.setName(receiveName);
            rider.setIdCard("");
            rider.setStatus(0); // 状态0表示待审核
            rider.setCreateTime(createtime);
            rider.setUpdateTime(createtime);
            
            // 调用服务层添加骑手
            boolean result = riderService.addRider(rider);

            if (result) {
                modelAndView.addObject("msg2", "骑手注册成功！请等待管理员审核");
                succ = 1;
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../login");
            } else {
                modelAndView.addObject("msg2", "系统繁忙，请稍后再试！");
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../register");
            }
        } else {
            // 普通用户注册：先检查用户名是否已存在
            QueryWrapper<User> checkQuery = new QueryWrapper<>();
            checkQuery.eq("username", username);
            List<User> existingUsers = userService.findUsers(checkQuery);
            
            if (existingUsers != null && !existingUsers.isEmpty()) {
                modelAndView.addObject("msg2", "用户名已存在！");
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../register");
                return modelAndView;
            }

            // 格式化当前时间作为创建时间
            String createtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            // 构建用户对象（isadmin=0普通用户，isvalidate=0待审核）
            User user = new User(0, username, email, passwd, receiveName, receivePhone, receiveAddress, "0", "0", createtime);
            // 调用服务层添加用户
            int result = userService.addUser(user);

            if (result > 0) {
                modelAndView.addObject("msg2", "注册成功！");
                succ = 1;
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../login");
            } else {
                modelAndView.addObject("msg2", "系统繁忙，请稍后再试！");
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../register");
            }
        }

        return modelAndView;
    }

    /**
     * 用户退出登录方法
     * 清除Session中的用户信息，跳转到登录页面
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回登录页面
     */
    @RequestMapping("/loginout")
    public ModelAndView loginout(HttpServletRequest request){

        ModelAndView modelAndView = new ModelAndView();

        // 从Session中移除用户信息
        request.getSession().removeAttribute("user");

        // 设置视图为登录页面
        modelAndView.setViewName("login");

        return  modelAndView;

    }
}
