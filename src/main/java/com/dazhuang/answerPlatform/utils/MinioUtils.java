package com.dazhuang.answerPlatform.utils;

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
public class MinioUtils {


}
