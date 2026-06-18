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

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RiderService riderService;

    @RequestMapping("/login")
    public ModelAndView userLogin(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();

        HttpSession session = request.getSession(true);
        System.out.println(session);
        //int succ = 0;

        String username = request.getParameter("username");//就是form表单里的 name="username"的文本输入框
        String pwd = request.getParameter("passwd");

        if (username != null && pwd != null && !"".equals(username.trim()) && !"".equals(pwd.trim())) {

            QueryWrapper<User> userQuery = new QueryWrapper<>();
            userQuery.eq("password", pwd);
            userQuery.and(wrapper -> wrapper.eq("username", username).or().eq("phone", username));

            List<User> users = userService.findUsers(userQuery);
            if (users == null || users.size() == 0) {
                Rider rider = riderService.login(username, pwd);
                if (rider != null) {
                    session.setAttribute("rider", rider);
                    modelAndView.setViewName("redirect:/rider/index");
                    return modelAndView;
                }
                session.invalidate();
                modelAndView.addObject("msg", "用户名/手机号或者密码错误！");
                modelAndView.setViewName("login");

            } else {
                User user = users.get(0);
                
                // 检查用户是否已冻结
                if ("2".equals(user.getIsvalidate())) {
                    session.invalidate();
                    modelAndView.addObject("msg", "当前账号已经被冻结！");
                    modelAndView.setViewName("login");
                    return modelAndView;
                }
                
                // 检查用户是否已审核
                if ("0".equals(user.getIsvalidate())) {
                    session.invalidate();
                    modelAndView.addObject("msg", "你当前的账户未经过审核！");
                    modelAndView.setViewName("login");
                    return modelAndView;
                }
                
                session.setAttribute("user", user);
                modelAndView.addObject("users", user);

                // 判断是否是管理员
                if ("1".equals(user.getIsadmin())) {
                    // 管理员跳转到销售统计页面
                    modelAndView.setViewName("forward:/admin/orderStats");
                } else {
                    // 普通用户跳转到前台首页
                    modelAndView.setViewName("forward:/goods/goodsList");
                }
            }
        } else {
            modelAndView.setViewName("login");
        }

        return modelAndView;


    }

    @RequestMapping("/register")
    public ModelAndView registerUser(@Valid HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String passwd = request.getParameter("passwd");
        String receiveName = request.getParameter("receiveName");
        String receivePhone = request.getParameter("receivePhone");
        String receiveAddress = request.getParameter("receiveAddress");
        String userType = request.getParameter("userType");
        int succ = 0;

        if ("rider".equals(userType)) {
            Rider existingRider = riderService.getRiderByPhone(receivePhone);
            
            if (existingRider != null) {
                modelAndView.addObject("msg2", "该手机号已注册为骑手！");
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../register");
                return modelAndView;
            }

            String createtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            Rider rider = new Rider();
            rider.setPhone(receivePhone);
            rider.setPassword(passwd);
            rider.setName(receiveName);
            rider.setIdCard("");
            rider.setStatus(0);
            rider.setCreateTime(createtime);
            rider.setUpdateTime(createtime);
            
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
            QueryWrapper<User> checkQuery = new QueryWrapper<>();
            checkQuery.eq("username", username);
            List<User> existingUsers = userService.findUsers(checkQuery);
            
            if (existingUsers != null && !existingUsers.isEmpty()) {
                modelAndView.addObject("msg2", "用户名已存在！");
                modelAndView.addObject("succ", succ);
                modelAndView.setViewName("/../register");
                return modelAndView;
            }

            String createtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            User user = new User(0, username, email, passwd, receiveName, receivePhone, receiveAddress, "0", "0", createtime);
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

    @RequestMapping("/loginout")
    public ModelAndView loginout(HttpServletRequest request){

        ModelAndView modelAndView = new ModelAndView();

        request.getSession().removeAttribute("user");

        modelAndView.setViewName("login");

        return  modelAndView;

    }
}
