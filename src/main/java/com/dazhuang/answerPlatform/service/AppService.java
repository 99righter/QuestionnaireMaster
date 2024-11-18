package com.dazhuang.answerPlatform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerCountDTO;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerResultCountDTO;
import com.dazhuang.answerPlatform.model.dto.app.AppQueryRequest;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.vo.AppVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 问卷服务
 *

 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface AppService extends IService<App> {

    /**
     * 校验数据
     *
     * @param app
     * @param add 对创建的数据进行校验
     */
    void validApp(App app, boolean add);

    /**
     * 获取查询条件
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest);
    
    /**
     * 获取问卷封装
     *
     * @param app
     * @param request
     * @return
     */
    AppVO getAppVO(App app, HttpServletRequest request);

    /**
     * 分页获取问卷封装
     *
     * @param appPage
     * @param request
     * @return
     */
    Page<AppVO> getAppVOPage(Page<App> appPage, HttpServletRequest request);
    /**
     * 获取每个题目的答题数量
     */
    List<AppAnswerCountDTO> getAnswerCountGroupByApp();

    /**
     * 获取某个app的答题结果统计
     */
    List<AppAnswerResultCountDTO> getAnswerResultCount(Long appId);
}
