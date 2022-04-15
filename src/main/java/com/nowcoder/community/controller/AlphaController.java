package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        //获取请求数据
        System.out.println(request.getMethod());    //请求方法
        System.out.println(request.getServletPath());   //请求地址
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(PrintWriter writer = response.getWriter();) {  //在这里创建writer编译时会自动编写finally，
                                                            // 并在其中调用write.close()
            writer.write("<h1>牛客网</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // /student?currect=1&limit=20
    @RequestMapping(path = "/student", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "currcnt", required = false, defaultValue = "1") int currect,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(currect);
        System.out.println(limit);
        return "some student";
    }

    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getsudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "student";
    }

    // /student
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String addStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    //不加@ResponseBody, 默认网页
    public ModelAndView getTeacher() {  //模板文件+Model模板引擎渲染得到html
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "张三");   //设置值
        modelAndView.addObject("age", 30);  //设置值
        modelAndView.setViewName("/demo/view"); //设置模板
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    //方便转化 java对象 -> JSON字符串 -> JS对象
    //String字符串 每个语言都有
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 20);
        emp.put("salary", 8000);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> emps = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 20);
        emp.put("salary", 8000);
        emps.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 21);
        emp.put("salary", 9000);
        emps.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 22);
        emp.put("salary", 10000);
        emps.add(emp);
        return emps;
    }
}
