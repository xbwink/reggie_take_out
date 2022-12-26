package com.xb.reggie.controller;

import com.xb.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 文件上传和下载
 * @author xb
 * @create 2022-12-11 9:34
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //动态获取路径地址
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件,需要保存到指定位置，否则本次请求完成后临时文件将会删除
        log.info("file={}",file);

        //通过原始图片名称获取后缀名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用uuid创建一个全新的图片名称
        String fileName = UUID.randomUUID()+suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在,需要创建
            dir.mkdirs();
        }

        //将临时文件保存到指定位置
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //创建输入流读取文件
            FileInputStream fileInputStream = new FileInputStream(basePath + name);
            //通过输出流将文件写回给浏览器,使浏览器能够展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            fileInputStream.close();
            outputStream.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
