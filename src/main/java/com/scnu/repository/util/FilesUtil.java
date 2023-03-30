package com.scnu.repository.util;

import com.scnu.repository.service.DocService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FilesUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    public static String uploadImg(MultipartFile file, String path1, String name, String path2){
        // 通过uuid产生一个图片名字
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-","");
        String imgName = uuid + name;
        // 这是我随机选择了一文件夹 这里只是我个人爱好
//        String code = Integer.toString(new Random().nextInt(5) + 1);
        // 拼接路径
//        String imgPath = path1 + "img" + code + "\\";
        String imgPath = path1;
        LOG.info("图片路径", imgPath);
//        String requestPath = path2 + "img" + code + "/";
        String requestPath = path2;

        try {
            // 上传操作
            File imgFile = new File(imgPath, imgName);
            file.transferTo(imgFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestPath + imgName;
    }
}
