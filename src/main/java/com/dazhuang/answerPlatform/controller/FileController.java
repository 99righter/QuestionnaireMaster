package com.dazhuang.answerPlatform.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dazhuang.answerPlatform.annotation.AuthCheck;
import com.dazhuang.answerPlatform.common.BaseResponse;
import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.common.ResultUtils;
import com.dazhuang.answerPlatform.constant.UserConstant;
import com.dazhuang.answerPlatform.exception.BusinessException;
import com.dazhuang.answerPlatform.exception.ThrowUtils;
import com.dazhuang.answerPlatform.manager.CosManager;
import com.dazhuang.answerPlatform.manager.MinioManager;
import com.dazhuang.answerPlatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 文件接口
 *
 

 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private MinioManager minioManager;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(multipartFile), ErrorCode.PARAMS_ERROR, "文件不能为空");
        validFile(multipartFile);
        String fileName = minioManager.upload(multipartFile);
        String fileUrl = minioManager.getFileUrl(fileName);
        return ResultUtils.success(fileUrl);
    }

    /**
     * 校验文件
     *
     * @param multipartFile 传入的文件
     */
    private void validFile(MultipartFile multipartFile) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 10240 * 10240L;

        if (fileSize > ONE_M) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 10M");
        }
        if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        }

    }
}
