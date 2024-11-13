package com.dazhuang.answerPlatform.manager;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.config.MinioConfig;
import com.dazhuang.answerPlatform.exception.ThrowUtils;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MinioManager {

    @Resource
    private MinioConfig minioConfig;

    @Resource
    private MinioClient minioClient;

    /**
     * 图片文件的上传
     *
     * @param file       文件对象
     * @param bucketName 桶名
     * @return 文件存储路径
     */
    public String upload(MultipartFile file, String bucketName) {
        //判断桶是否存在
        if (!bucketExists(bucketName)) {
            //如果桶不存在，直接返回null；
            return null;
        }
        //判断对象是否为空
        if (ObjectUtil.isEmpty(file)) {
            return null;
        }
        //都不为空的话，进行存储操作
        try (InputStream inputStream = file.getInputStream()) {
            //获取文件名
            String objectName = file.getOriginalFilename();
            //生成新的文件名
            String fileName = UUID.randomUUID().toString().replace("-", "") + objectName;
            //构造文件路径名
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String currentDate = simpleDateFormat.format(new Date());
            String filePath = currentDate + "/" + fileName;
            //存储文件
            this.minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .stream(inputStream, inputStream.available(), -1)
                    .contentType("application/octet-stream")
                    .build());
            return filePath;
        } catch (Exception e) {
            ThrowUtils.throwIf(false, ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
        return null;
    }

    /**
     * 图片文件上传再封装
     *
     * @param file 传入的文件
     * @return 文件路径
     */
    public String upload(MultipartFile file) {
        return upload(file, minioConfig.getBucketName());
    }

    /**
     * 判断桶是否存在
     *
     * @param bucketName 桶名
     * @return 存在-true 不存在-false
     */
    public Boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build());
        } catch (Exception e) {
            ThrowUtils.throwIf(false, ErrorCode.SYSTEM_ERROR, "桶不存在");
        }
        return false;
    }

    /**
     * 获取图片的网址链接
     *
     * @param filePath
     * @return
     */
    public String getFileUrl(String filePath) {
        //判断桶是否存在
        if (bucketExists(minioConfig.getBucketName())) {
            try {
                return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(minioConfig.getBucketName())
                        .object(filePath)
                        .build());
            } catch (Exception e) {
                ThrowUtils.throwIf(false, ErrorCode.SYSTEM_ERROR, "获取图片失败");
            }
        }
        return null;
    }

    /**
     * 获取图片的直接访问链接
     *
     * @param filePath 文件路径
     * @return 文件的直接访问链接
     */
    public String getImageUrl(String filePath) {
        if (bucketExists(minioConfig.getBucketName())) {
            return minioConfig.getUrl() + "/" + minioConfig.getBucketName()  + filePath;
        }
        return null;
    }
}
