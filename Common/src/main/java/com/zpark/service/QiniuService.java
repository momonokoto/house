package com.zpark.service;

import org.springframework.web.multipart.MultipartFile;

public interface QiniuService {
    // 通用文件上传
    String uploadFile(MultipartFile file);

    // 图片上传（可添加图片处理参数）
    String uploadImage(MultipartFile image, String imageStyle);


    String generatePrivateUrl(String fileName, long expiresInSeconds);

    // 视频上传
    String uploadVideo(MultipartFile video);

    // 获取文件访问URL
    String getFileUrl(String fileName);

    // 获取私有文件访问URL
    String getPrivateFileUrl(String fileName, long expireInSeconds);

    // 获取图片处理后的URL
    String getImageWithStyle(String fileName, String imageStyle);

    // 获取视频缩略图URL
    String getVideoThumbnail(String fileName, String thumbnailStyle);
}
