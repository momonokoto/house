package com.zpark.service.impl;

import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.zpark.config.QiniuConfig;
import com.zpark.service.QiniuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class QiniuServiceImpl implements QiniuService {
    private final Auth auth;
    private final UploadManager uploadManager;

    private final QiniuConfig qiniuConfig;


    /**
     * 生成私有文件的临时下载URL（带Token）
     * @param fileName 文件名（如 "1750670821492_263.mp4"）
     * @param expiresInSeconds 有效期（秒，默认3600=1小时）
     * @return 带Token的可访问URL
     */
    public String generatePrivateUrl(String fileName, long expiresInSeconds) {
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        String baseUrl = qiniuConfig.getDomain() + fileName;
        return auth.privateDownloadUrl(baseUrl, expiresInSeconds);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String uploadToken = auth.uploadToken(qiniuConfig.getBucket());
            Response response = uploadManager.put(file.getBytes(), fileName, uploadToken);
            if (response.isOK()) {
                return fileName;
            }
            throw new RuntimeException("上传失败: " + response.error);
        } catch (Exception e) {
            throw new RuntimeException("上传到七牛云失败", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile image, String imageStyle) {
        String fileName = uploadFile(image);
        // 可以在这里添加图片处理逻辑
        return fileName;
    }

    @Override
    public String uploadVideo(MultipartFile video) {
        // 视频上传和普通文件上传类似，但可以添加视频处理参数
        return uploadFile(video);
    }

    @Override
    public String getFileUrl(String fileName) {
        return qiniuConfig.getDomain() + fileName;
    }

    @Override
    public String getPrivateFileUrl(String fileName, long expireInSeconds) {
        String publicUrl = getFileUrl(fileName);
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }

    @Override
    public String getImageWithStyle(String fileName, String imageStyle) {
        // 图片样式处理，如: "imageView2/1/w/200/h/200"
        return getFileUrl(fileName) + "?" + imageStyle;
    }

    @Override
    public String getVideoThumbnail(String fileName, String thumbnailStyle) {
        // 视频缩略图处理，如: "vframe/jpg/offset/1/w/480/h/360"
        return getFileUrl(fileName) + "?" + thumbnailStyle;
    }

    private String generateFileName(String originalFilename) {
        // 生成唯一文件名: 时间戳 + 随机数 + 后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        return System.currentTimeMillis() + "_" +
                (int)(Math.random() * 1000) + suffix;
    }
}
