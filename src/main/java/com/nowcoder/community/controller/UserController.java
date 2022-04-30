package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


@Controller
@RequestMapping("/user")
public class UserController {

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String updateHeaderUrl(MultipartFile multipartFile, Model model) {
        //1.检查是否选择文件
        if(multipartFile == null) {
            model.addAttribute("error", "您未选择图片！");
            return "/site/setting";
        }

        //2.获取越来的文件名，提取后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if(!(suffix.equals("png") || suffix.equals("jpg") || suffix.equals("tif"))) {
            model.addAttribute("error", "仅支持.png,.jpg,.tif格式文件！");
            return "/site/setting";
        }

        //3.生成新的文件名，将文件存进服务器内存
        String filename = CommunityUtil.generateUUID() + suffix;

        File dest = new File(uploadPath + "/" + filename);
        try {
            multipartFile.transferTo(dest);
        } catch (IOException e) {
            logger.error("文件上传失败！" + e.getMessage());
            throw new RuntimeException("文件上传失败，服务器内部错误", e);
        }

        //4.更新user的头像路径
        User user = hostHolder.getUser();
        //http://localhost:8080/community/user/hearder/filname
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    //获取头像
    @RequestMapping(path =  "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //获取文件全类名
        filename = uploadPath + "/" + filename;
        //获取后缀
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);
        //响应图片
        response.setContentType("image/" + suffix);


        try (
                //浏览器输出
                ServletOutputStream os = response.getOutputStream();
                //文件读入
                FileInputStream fis = new FileInputStream(filename);
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("头像获取失败：" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/revise", method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword, String verifyPassword) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user, oldPassword, newPassword, verifyPassword);
        if(map == null || map.isEmpty()) {
            model.addAttribute("msg", "密码修改成功，请重新登录！");
            model.addAttribute("target", "/logout");
            return "/site/operate-result";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("verifyPasswordMsg", map.get("verifyPasswordMsg"));
            return "/site/setting";
        }

    }
}
