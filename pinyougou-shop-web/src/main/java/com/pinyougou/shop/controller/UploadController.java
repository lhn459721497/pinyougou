package com.pinyougou.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//图片服务器地址

    //上传服务
    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

        //获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf("."));

        try {
            //创建FastDFS对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");

            //执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(),extName);
            //接收返回的url和ip地址，拼接为完整url
            String url = FILE_SERVER_URL + path;
            return new Result(true,url);

        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false,"上传失败");
        }

    }

}
