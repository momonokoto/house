package com.zpark.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpark.entity.House;
import com.zpark.exception.InvalidFileException;
import com.zpark.mapper.HouseMapper_AdPu;
import com.zpark.service.IHouseService;
import com.zpark.service.MessageProducerService;
import com.zpark.service.QiniuService;
import com.zpark.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "房源和文件处理接口", description = "房源文件上传以及临时url获取")
public class MediaController {
    private final QiniuService qiniuService;

    private final MessageProducerService messageProducerService;


    private final JwtUtil jwtUtil;

    private final IHouseService houseService;

    private final HouseMapper_AdPu houseMapper;

    // 上传图片
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "上传图片或视频",
            description = "同时接收图片和视频文件上传，支持以下组合:\n" +
                         "1. 仅图片\n" +
                         "2. 仅视频\n" +
                         "3. 图片+视频\n" +
                         "至少需要上传一个文件",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "上传成功",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UploadResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "无效的文件类型或未上传任何文件"
                    )
            }
    )@Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<Map<String, Object>> uploadMedia(
            @Parameter(description = "房源JSON数据", required = true,
                    schema = @Schema(implementation = House.class ))
            @RequestPart("house") String houseJson,
            @Parameter(
                    description = "图片文件(可上传多个)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam(value = "image", required = false) MultipartFile[] imageFiles,

            @Parameter(
                    description = "视频文件(可上传多个)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam(value = "video", required = false) MultipartFile[] videoFiles,

            @Parameter(description = "图片处理样式(可选)")
            @RequestParam(value = "imageStyle", required = false) String imageStyle,
            HttpServletRequest request){

        try {
            // 解析JSON到House对象
            ObjectMapper objectMapper = new ObjectMapper();
            House house = objectMapper.readValue(houseJson, House.class);
            // 忽略前端传递的id，使用数据库自动递增
            house.setHouseId(null);

            // 处理JWT token
            String requestTokenHeader = request.getHeader("Authorization");
            String token = null;
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                token = requestTokenHeader.substring(7);
            }
            house.setOwnerId(jwtUtil.getUserFromToken(token).getId());
            // 检查是否至少上传了一个文件
            if ((imageFiles == null || imageFiles.length == 0) && (videoFiles == null || videoFiles.length == 0)) {
                throw new IllegalArgumentException("至少需要上传一个文件(图片或视频)");
            }

            Map<String, Object> result = new HashMap<>();
            StringBuilder imageFileNames = new StringBuilder();
            StringBuilder videoFileNames = new StringBuilder();

            // 处理图片上传
            if (imageFiles != null && imageFiles.length > 0) {
                for (MultipartFile imageFile : imageFiles) {
                    if (!isImage(imageFile)) {
                        throw new IllegalArgumentException("请上传有效的图片文件");
                    }
                    String imageFileName = qiniuService.uploadImage(imageFile, imageStyle);
                    if (imageFileNames.length() > 0) {
                        imageFileNames.append(",");
                    }
                    imageFileNames.append(imageFileName);
                    result.put("imageFileName", imageFileNames.toString());
                }
                // 确保有图片文件时才设置img字段
                if (imageFileNames.length() > 0) {
                    house.setImg(imageFileNames.toString());
                }
            }

            // 处理视频上传
            if (videoFiles != null && videoFiles.length > 0) {
                for (MultipartFile videoFile : videoFiles) {
                    if (!isVideo(videoFile)) {
                        throw new IllegalArgumentException("请上传有效的视频文件");
                    }
                    String videoFileName = qiniuService.uploadVideo(videoFile);
                    if (videoFileNames.length() > 0) {
                        videoFileNames.append(",");
                    }
                    videoFileNames.append(videoFileName);
                    result.put("videoFileName", videoFileNames.toString());
                    result.put("videoUrl", qiniuService.getFileUrl(videoFileName));
                    result.put("videoThumbnailUrl",
                            qiniuService.getVideoThumbnail(videoFileName, "vframe/jpg/offset/1/w/480/h/360"));
                }
                if (videoFileNames.length() > 0) {
                    house.setVideo(videoFileNames.toString());
                 }

            }

            // 保存房屋信息
            houseMapper.insert(house);

            messageProducerService.sendAdmin("有新的待审核房源，请及时审核");
            return ResponseEntity.ok().body(result);
        }catch (JsonProcessingException e){
            throw new InvalidFileException("房屋信息JSON解析失败");
        }



    }

    @PostMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request){
        messageProducerService.sendAdmin("有新的待审核房源，请及时审核");
        return ResponseEntity.ok().body("成功");
    }



    @GetMapping("/get-url")
    @Operation(
            summary = "获取文件信息",
            description = "根据文件名获取文件URL,可暂时访问，支持多个文件(逗号分隔),直接把数据库查到的放进来就行了，我会放回多个url"
    )@Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<Map<String, Object>> getFileUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "360000") long expiresInSeconds) {

        Map<String, Object> response = new HashMap<>();
        String[] fileNames = fileName.split(",");
        
        List<String> urls = new ArrayList<>();
        List<String> expiresAts = new ArrayList<>();
        
        for (String name : fileNames) {
            String url = qiniuService.generatePrivateUrl(name.trim(), expiresInSeconds);
            urls.add(url);
            expiresAts.add(String.valueOf(System.currentTimeMillis() + expiresInSeconds * 1000));
        }
        
        response.put("urls", urls);
        response.put("expiresAts", expiresAts);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get-url-map")
    @Operation(
            summary = "获取文件信息",
            description = "根据文件名获取文件URL,可暂时访问，支持多个文件(逗号分隔),直接把数据库查到的放进来就行了，我会放回多个url"
    )@Parameter(
            name = "Authorization",
            description = "认证令牌",
            required = true,
            in = ParameterIn.HEADER,
            schema = @Schema(type = "string", example = "Bearer xxx.yyy.zzz")
    )
    public ResponseEntity<Map<String, Object>> getFileUrlMap(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "3600") long expiresInSeconds) {

        Map<String, Object> response = new HashMap<>();
        String[] fileNames = fileName.split(",");

        Map<String, String> urlMap = new HashMap<>();
        List<String> expiresAts = new ArrayList<>();

        for (String name : fileNames) {
            String url = qiniuService.generatePrivateUrl(name.trim(), expiresInSeconds);
            urlMap.put(name.trim(), url);
            expiresAts.add(String.valueOf(System.currentTimeMillis() + expiresInSeconds * 1000));
        }

        response.put("urls", urlMap);
        response.put("expiresAts", expiresAts);
        return ResponseEntity.ok().body(response);
    }

//    @GetMapping("/file-info")
//    @Operation(
//            summary = "获取文件信息",
//            description = "根据文件名获取文件URL和相关信息"
//    )
//    public ResponseEntity<Map<String, String>> getFileInfo(
//            @Parameter(description = "文件名", required = true)
//            @RequestParam String fileName,
//
//            @Parameter(description = "文件类型(image/video)", required = true)
//            @RequestParam String fileType,
//
//            @Parameter(description = "图片处理样式(仅对图片有效)")
//            @RequestParam(required = false) String style) {
//
//        Map<String, String> result = new HashMap<>();
//
//        if ("image".equalsIgnoreCase(fileType)) {
//            result.put("url", style != null ?
//                    qiniuService.getImageWithStyle(fileName, style) :
//                    qiniuService.getFileUrl(fileName));
//        } else if ("video".equalsIgnoreCase(fileType)) {
//            result.put("url", qiniuService.getFileUrl(fileName));
//            result.put("thumbnailUrl",
//                    qiniuService.getVideoThumbnail(fileName, "vframe/jpg/offset/1/w/480/h/360"));
//        } else {
//            throw new IllegalArgumentException("不支持的文件类型: " + fileType);
//        }
//
//        return ResponseEntity.ok(result);
//    }




    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private boolean isVideo(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }

    @Schema(name = "UploadResponse", description = "上传响应数据")
    private static class UploadResponse {
        @Schema(description = "图片文件名", example = "1654324567890_image.jpg")
        public String imageFileName;

        @Schema(description = "图片访问URL", example = "http://qiniu.com/1654324567890_image.jpg")
        public String imageUrl;

        @Schema(description = "带样式的图片URL(如果提供了样式)", example = "http://qiniu.com/1654324567890_image.jpg?imageView2/1/w/200/h/200")
        public String styledImageUrl;

        @Schema(description = "视频文件名", example = "1654324567891_video.mp4")
        public String videoFileName;

        @Schema(description = "视频访问URL", example = "http://qiniu.com/1654324567891_video.mp4")
        public String videoUrl;

        @Schema(description = "视频缩略图URL", example = "http://qiniu.com/1654324567891_video.mp4?vframe/jpg/offset/1/w/480/h/360")
        public String videoThumbnailUrl;
    }
}
