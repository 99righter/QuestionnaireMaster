package com.dazhuang.answerPlatform.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.constant.CommonConstant;
import com.dazhuang.answerPlatform.exception.ThrowUtils;
import com.dazhuang.answerPlatform.mapper.UserAnswerMapper;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerCountDTO;
import com.dazhuang.answerPlatform.model.dto.userAnswer.UserAnswerQueryRequest;
import com.dazhuang.answerPlatform.model.entity.User;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import com.dazhuang.answerPlatform.model.enums.AppScoringStrategyEnum;
import com.dazhuang.answerPlatform.model.enums.AppTypeEnum;
import com.dazhuang.answerPlatform.model.vo.UserAnswerVO;
import com.dazhuang.answerPlatform.model.vo.UserVO;
import com.dazhuang.answerPlatform.service.UserAnswerService;
import com.dazhuang.answerPlatform.service.UserService;
import com.dazhuang.answerPlatform.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户答案服务实现
 *
 
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class UserAnswerServiceImpl extends ServiceImpl<UserAnswerMapper, UserAnswer> implements UserAnswerService {

    @Resource
    private UserService userService;
    /**
     * 校验数据
     *
     * @param userAnswer
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validUserAnswer(UserAnswer userAnswer, boolean add) {
        ThrowUtils.throwIf(userAnswer == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Long id = userAnswer.getId();
        Long appId = userAnswer.getAppId();
        Integer appType = userAnswer.getAppType();
        Integer scoringStrategy = userAnswer.getScoringStrategy();
        String choices = userAnswer.getChoices();
//        Long resultId = userAnswer.getResultId();
//        String resultName = userAnswer.getResultName();
//        String resultDesc = userAnswer.getResultDesc();
//        String resultPicture = userAnswer.getResultPicture();
//        Integer resultScore = userAnswer.getResultScore();
        Long userId = userAnswer.getUserId();


        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(ObjectUtils.isEmpty(id),ErrorCode.PARAMS_ERROR,"用户答案编号为空");
            ThrowUtils.throwIf(ObjectUtils.isEmpty(appId),ErrorCode.PARAMS_ERROR,"问卷编号为空");
//            AppTypeEnum appTypeEnum  = AppTypeEnum.getEnumByValue(appType);
//            ThrowUtils.throwIf(appTypeEnum == null, ErrorCode.PARAMS_ERROR,"问卷类型不存在");
//            AppScoringStrategyEnum appScoringStrategyEnum = AppScoringStrategyEnum.getEnumByValue(scoringStrategy);
//            ThrowUtils.throwIf(appScoringStrategyEnum == null,ErrorCode.PARAMS_ERROR,"评分类型不存在");
            ThrowUtils.throwIf(StringUtils.isBlank(choices),ErrorCode.PARAMS_ERROR,"用户答案为空");
//            ThrowUtils.throwIf(ObjectUtils.isEmpty(resultId),ErrorCode.PARAMS_ERROR,"用户答案结果编号为空");
//            ThrowUtils.throwIf(ObjectUtils.isEmpty(resultScore),ErrorCode.PARAMS_ERROR,"得分范围为空");
//            ThrowUtils.throwIf(StringUtils.isBlank(resultName),ErrorCode.PARAMS_ERROR,"用户答案名为空");
//            ThrowUtils.throwIf(StringUtils.isBlank(resultDesc),ErrorCode.PARAMS_ERROR,"答案描述为空");
//            ThrowUtils.throwIf(StringUtils.isBlank(resultPicture),ErrorCode.PARAMS_ERROR,"答案图片为空");
//            ThrowUtils.throwIf(ObjectUtils.isEmpty(userId),ErrorCode.PARAMS_ERROR,"用户编号为空");
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(choices)) {
            ThrowUtils.throwIf(choices.length() > 80, ErrorCode.PARAMS_ERROR, "答案过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param userAnswerQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<UserAnswer> getQueryWrapper(UserAnswerQueryRequest userAnswerQueryRequest) {
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (userAnswerQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值

        Long id = userAnswerQueryRequest.getId();
        Long appId = userAnswerQueryRequest.getAppId();
        Integer appType = userAnswerQueryRequest.getAppType();
        Integer scoringStrategy = userAnswerQueryRequest.getScoringStrategy();
        List<String> choices = userAnswerQueryRequest.getChoices();
        Long resultId = userAnswerQueryRequest.getResultId();
        String resultName = userAnswerQueryRequest.getResultName();
        String resultDesc = userAnswerQueryRequest.getResultDesc();
        String resultPicture = userAnswerQueryRequest.getResultPicture();
        Integer resultScore = userAnswerQueryRequest.getResultScore();
        Long userId = userAnswerQueryRequest.getUserId();
        Long notId = userAnswerQueryRequest.getNotId();
        String searchText = userAnswerQueryRequest.getSearchText();
        int current = userAnswerQueryRequest.getCurrent();
        int pageSize = userAnswerQueryRequest.getPageSize();
        String sortField = userAnswerQueryRequest.getSortField();
        String sortOrder = userAnswerQueryRequest.getSortOrder();

        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("resultName", searchText).or().like("resultDesc", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(resultDesc), "resultDesc", resultDesc);
        queryWrapper.like(StringUtils.isNotBlank(resultName), "resultName", resultName);
        queryWrapper.like(StringUtils.isNotBlank(resultPicture), "resultPicture", resultPicture);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(choices)) {
            for (String tag : choices) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultScore), "resultScore", resultScore);
        queryWrapper.eq(ObjectUtils.isNotEmpty(scoringStrategy), "scoringStrategy", scoringStrategy);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultId), "resultId", resultId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appType),"appType", appType);

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取用户答案封装
     *
     * @param userAnswer
     * @param request
     * @return
     */
    @Override
    public UserAnswerVO getUserAnswerVO(UserAnswer userAnswer, HttpServletRequest request) {
        // 对象转封装类
        UserAnswerVO userAnswerVO = UserAnswerVO.objToVo(userAnswer);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = userAnswer.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        userAnswerVO.setUser(userVO);
//        // 2. 已登录，获取用户点赞、收藏状态
//        long userAnswerId = userAnswer.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<UserAnswerThumb> userAnswerThumbQueryWrapper = new QueryWrapper<>();
//            userAnswerThumbQueryWrapper.in("userAnswerId", userAnswerId);
//            userAnswerThumbQueryWrapper.eq("userId", loginUser.getId());
//            UserAnswerThumb userAnswerThumb = userAnswerThumbMapper.selectOne(userAnswerThumbQueryWrapper);
//            userAnswerVO.setHasThumb(userAnswerThumb != null);
//            // 获取收藏
//            QueryWrapper<UserAnswerFavour> userAnswerFavourQueryWrapper = new QueryWrapper<>();
//            userAnswerFavourQueryWrapper.in("userAnswerId", userAnswerId);
//            userAnswerFavourQueryWrapper.eq("userId", loginUser.getId());
//            UserAnswerFavour userAnswerFavour = userAnswerFavourMapper.selectOne(userAnswerFavourQueryWrapper);
//            userAnswerVO.setHasFavour(userAnswerFavour != null);
//        }
        // endregion

        return userAnswerVO;
    }

    /**
     * 分页获取用户答案封装
     *
     * @param userAnswerPage
     * @param request
     * @return
     */
    @Override
    public Page<UserAnswerVO> getUserAnswerVOPage(Page<UserAnswer> userAnswerPage, HttpServletRequest request) {
        List<UserAnswer> userAnswerList = userAnswerPage.getRecords();
        Page<UserAnswerVO> userAnswerVOPage = new Page<>(userAnswerPage.getCurrent(), userAnswerPage.getSize(), userAnswerPage.getTotal());
        if (CollUtil.isEmpty(userAnswerList)) {
            return userAnswerVOPage;
        }
        // 对象列表 => 封装对象列表
        List<UserAnswerVO> userAnswerVOList = userAnswerList.stream().map(userAnswer -> {
            return UserAnswerVO.objToVo(userAnswer);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = userAnswerList.stream().map(UserAnswer::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> userAnswerIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> userAnswerIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> userAnswerIdSet = userAnswerList.stream().map(UserAnswer::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<UserAnswerThumb> userAnswerThumbQueryWrapper = new QueryWrapper<>();
//            userAnswerThumbQueryWrapper.in("userAnswerId", userAnswerIdSet);
//            userAnswerThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<UserAnswerThumb> userAnswerUserAnswerThumbList = userAnswerThumbMapper.selectList(userAnswerThumbQueryWrapper);
//            userAnswerUserAnswerThumbList.forEach(userAnswerUserAnswerThumb -> userAnswerIdHasThumbMap.put(userAnswerUserAnswerThumb.getUserAnswerId(), true));
//            // 获取收藏
//            QueryWrapper<UserAnswerFavour> userAnswerFavourQueryWrapper = new QueryWrapper<>();
//            userAnswerFavourQueryWrapper.in("userAnswerId", userAnswerIdSet);
//            userAnswerFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<UserAnswerFavour> userAnswerFavourList = userAnswerFavourMapper.selectList(userAnswerFavourQueryWrapper);
//            userAnswerFavourList.forEach(userAnswerFavour -> userAnswerIdHasFavourMap.put(userAnswerFavour.getUserAnswerId(), true));
//        }
        // 填充信息
        userAnswerVOList.forEach(userAnswerVO -> {
            Long userId = userAnswerVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            userAnswerVO.setUser(userService.getUserVO(user));
//            userAnswerVO.setHasThumb(userAnswerIdHasThumbMap.getOrDefault(userAnswerVO.getId(), false));
//            userAnswerVO.setHasFavour(userAnswerIdHasFavourMap.getOrDefault(userAnswerVO.getId(), false));
        });
        // endregion

        userAnswerVOPage.setRecords(userAnswerVOList);
        return userAnswerVOPage;
    }



}
