package com.dazhuang.answerPlatform.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.constant.CommonConstant;
import com.dazhuang.answerPlatform.exception.ThrowUtils;
import com.dazhuang.answerPlatform.mapper.AppMapper;
import com.dazhuang.answerPlatform.mapper.UserAnswerMapper;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerCountDTO;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerResultCountDTO;
import com.dazhuang.answerPlatform.model.dto.app.AppQueryRequest;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.User;
import com.dazhuang.answerPlatform.model.enums.AppScoringStrategyEnum;
import com.dazhuang.answerPlatform.model.enums.AppTypeEnum;
import com.dazhuang.answerPlatform.model.enums.ReviewTypeEnum;
import com.dazhuang.answerPlatform.model.vo.AppVO;
import com.dazhuang.answerPlatform.model.vo.UserVO;
import com.dazhuang.answerPlatform.service.AppService;
import com.dazhuang.answerPlatform.service.UserService;
import com.dazhuang.answerPlatform.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;
    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 校验数据
     *
     * @param app
     * @param add 对创建的数据进行校验
     */
    @Override
    public void validApp(App app, boolean add) {
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String appName = app.getAppName();
        String appDesc = app.getAppDesc();
        String appIcon = app.getAppIcon();
        Integer appType = app.getAppType();
        Integer scoringStrategy = app.getScoringStrategy();
        //用户刚开始创建的应用的审核状态都是待审核，为默认字段
        Integer reviewStatus = ReviewTypeEnum.REVIEWING.getValue();
        Long userId = app.getUserId();

        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(appName), ErrorCode.PARAMS_ERROR, "应用名称不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(appDesc), ErrorCode.PARAMS_ERROR, "应用描述不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(appIcon), ErrorCode.PARAMS_ERROR, "应用图标不能为空");
            AppTypeEnum apptype = AppTypeEnum.getEnumByValue(appType);
            ThrowUtils.throwIf(apptype == null, ErrorCode.PARAMS_ERROR, "应用类型不存在");
            AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(scoringStrategy);
            ThrowUtils.throwIf(appScoringStrategyEnum == null, ErrorCode.PARAMS_ERROR, "评分策略类型不存在");
            ReviewTypeEnum reviewTypeEnum = ReviewTypeEnum.getEnumByValue(reviewStatus);
            ThrowUtils.throwIf(reviewTypeEnum == null, ErrorCode.PARAMS_ERROR, "审核状态不能为空");
            // 当前登录用户
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            User loginUser = userService.getLoginUser(request);
            ThrowUtils.throwIf(loginUser == null, ErrorCode.PARAMS_ERROR, "用户未登录");
        }
        // 修改数据时，有参数则校验
        //  补充校验规则
        if (StringUtils.isNotBlank(appName)) {
            ThrowUtils.throwIf(appName.length() > 80, ErrorCode.PARAMS_ERROR, "应用名称过长");
        }

    }

    /**
     * 获取查询条件
     *
     * @param appQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest) {
        QueryWrapper<App> queryWrapper = new QueryWrapper<>();
        if (appQueryRequest == null) {
            return queryWrapper;
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String appDesc = appQueryRequest.getAppDesc();
        String appIcon = appQueryRequest.getAppIcon();
        Integer appType = appQueryRequest.getAppType();
        Integer scoringStrategy = appQueryRequest.getScoringStrategy();
        Integer reviewStatus = appQueryRequest.getReviewStatus();
        Long reviewerId = appQueryRequest.getReviewerId();
        Long userId = appQueryRequest.getUserId();

        String searchText = appQueryRequest.getSearchText();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();


        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("appName", searchText).or().like("appDesc", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(appName), "appName", appName);
        queryWrapper.like(StringUtils.isNotBlank(appDesc), "content", appDesc);
        queryWrapper.like(StringUtils.isNotBlank(appIcon), "appIcon", appIcon);


//        // JSON 数组查询
//        if (CollUtil.isNotEmpty(tagList)) {
//            for (String tag : tagList) {
//                queryWrapper.like("tags", "\"" + tag + "\"");
//            }
//        }
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(appType), "appType", appType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringStrategy), "scoringStrategy", scoringStrategy);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取应用封装
     *
     * @param app
     * @param request
     * @return
     */
    @Override
    public AppVO getAppVO(App app, HttpServletRequest request) {
        // 对象转封装类
        AppVO appVO = AppVO.objToVo(app);

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = app.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        appVO.setUser(userVO);
//        // 2. 已登录，获取用户点赞、收藏状态
//        long appId = app.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<AppThumb> appThumbQueryWrapper = new QueryWrapper<>();
//            appThumbQueryWrapper.in("appId", appId);
//            appThumbQueryWrapper.eq("userId", loginUser.getId());
//            AppThumb appThumb = appThumbMapper.selectOne(appThumbQueryWrapper);
//            appVO.setHasThumb(appThumb != null);
//            // 获取收藏
//            QueryWrapper<AppFavour> appFavourQueryWrapper = new QueryWrapper<>();
//            appFavourQueryWrapper.in("appId", appId);
//            appFavourQueryWrapper.eq("userId", loginUser.getId());
//            AppFavour appFavour = appFavourMapper.selectOne(appFavourQueryWrapper);
//            appVO.setHasFavour(appFavour != null);
//        }
//        // endregion

        return appVO;
    }

    /**
     * 分页获取应用封装
     *
     * @param appPage
     * @param request
     * @return
     */
    @Override
    public Page<AppVO> getAppVOPage(Page<App> appPage, HttpServletRequest request) {
        List<App> appList = appPage.getRecords();
        Page<AppVO> appVOPage = new Page<>(appPage.getCurrent(), appPage.getSize(), appPage.getTotal());
        if (CollUtil.isEmpty(appList)) {
            return appVOPage;
        }
        // 对象列表 => 封装对象列表
        List<AppVO> appVOList = appList.stream().map(app -> {
            return AppVO.objToVo(app);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
//        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> appIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> appIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> appIdSet = appList.stream().map(App::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<AppThumb> appThumbQueryWrapper = new QueryWrapper<>();
//            appThumbQueryWrapper.in("appId", appIdSet);
//            appThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<AppThumb> appAppThumbList = appThumbMapper.selectList(appThumbQueryWrapper);
//            appAppThumbList.forEach(appAppThumb -> appIdHasThumbMap.put(appAppThumb.getAppId(), true));
//            // 获取收藏
//            QueryWrapper<AppFavour> appFavourQueryWrapper = new QueryWrapper<>();
//            appFavourQueryWrapper.in("appId", appIdSet);
//            appFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<AppFavour> appFavourList = appFavourMapper.selectList(appFavourQueryWrapper);
//            appFavourList.forEach(appFavour -> appIdHasFavourMap.put(appFavour.getAppId(), true));
//        }
        // 填充信息
        appVOList.forEach(appVO -> {
            Long userId = appVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            appVO.setUser(userService.getUserVO(user));
//            appVO.setHasThumb(appIdHasThumbMap.getOrDefault(appVO.getId(), false));
//            appVO.setHasFavour(appIdHasFavourMap.getOrDefault(appVO.getId(), false));
        });
        // endregion

        appVOPage.setRecords(appVOList);
        return appVOPage;
    }

    @Override
    public List<AppAnswerCountDTO> getAnswerCountGroupByApp() {
        return userAnswerMapper.getAnswerGroupByApp();
    }

    @Override
    public List<AppAnswerResultCountDTO> getAnswerResultCount(Long appId) {
        return userAnswerMapper.getAppAnswerResultCountGroupByResult(appId);
    }

}
