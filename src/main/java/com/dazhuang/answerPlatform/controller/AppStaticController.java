package com.dazhuang.answerPlatform.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import com.dazhuang.answerPlatform.annotation.AuthCheck;
import com.dazhuang.answerPlatform.common.BaseResponse;
import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.common.ResultUtils;
import com.dazhuang.answerPlatform.constant.UserConstant;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerCountDTO;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerResultCountDTO;
import com.dazhuang.answerPlatform.model.entity.User;
import com.dazhuang.answerPlatform.service.AppService;
import com.dazhuang.answerPlatform.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController()
@RequestMapping("/app/static")
@Slf4j
/**
 * 应用分析借口
 */
public class AppStaticController {
    @Resource
    private AppService appService;
    @Resource
    private UserService userService;

    @GetMapping("/answer_count_group_by_app")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<AppAnswerCountDTO>> getAnswerCountGroupByApp(HttpServletRequest request) {
        // 只有管理员才能够有权限访问
        //首先获取当前用户登陆态
        //更新审核状态
        User loginUser = userService.getLoginUser(request);
        //鉴权
        //如果是管理员
        if (!userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "不具有管理员权限");
        }
        return ResultUtils.success(appService.getAnswerCountGroupByApp());
    }

    @GetMapping("/result_group_by_app")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<AppAnswerResultCountDTO>> getResultGroupByApp(HttpServletRequest request, Long appId) {
        // 只有管理员才能够有权限访问
        //首先获取当前用户登陆态
        //更新审核状态
        User loginUser = userService.getLoginUser(request);
        //鉴权
        //如果是管理员
        if (!userService.isAdmin(loginUser)) {
            return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, "不具有管理员权限");
        }
        if (ObjectUtil.isEmpty(appId)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "appId不能为空");
        }
        return ResultUtils.success(appService.getAnswerResultCount(appId));
    }
}
