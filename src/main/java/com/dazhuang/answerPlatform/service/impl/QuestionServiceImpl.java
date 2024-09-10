package com.dazhuang.answerPlatform.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dazhuang.answerPlatform.common.ErrorCode;
import com.dazhuang.answerPlatform.constant.AIGenerateSystemMessageConfig;
import com.dazhuang.answerPlatform.constant.CommonConstant;
import com.dazhuang.answerPlatform.exception.ThrowUtils;
import com.dazhuang.answerPlatform.manager.AiManager;
import com.dazhuang.answerPlatform.mapper.QuestionMapper;
import com.dazhuang.answerPlatform.model.dto.question.QuestionContentDTO;
import com.dazhuang.answerPlatform.model.dto.question.QuestionQueryRequest;
import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.Question;
import com.dazhuang.answerPlatform.model.entity.User;
import com.dazhuang.answerPlatform.model.enums.AppTypeEnum;
import com.dazhuang.answerPlatform.model.vo.QuestionVO;
import com.dazhuang.answerPlatform.model.vo.UserVO;
import com.dazhuang.answerPlatform.service.AppService;
import com.dazhuang.answerPlatform.service.QuestionService;
import com.dazhuang.answerPlatform.service.UserService;
import com.dazhuang.answerPlatform.utils.SqlUtils;
import com.zhipu.oapi.service.v4.model.ModelData;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 题目服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    @Resource
    private AppService appService;

    @Resource
    private AiManager aiManager;

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);

        //  从对象中取值

        String questionContent = question.getQuestionContent();
        Long appId = question.getAppId();
        //Long userId = question.getUserId();


        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(questionContent), ErrorCode.PARAMS_ERROR, "问题为空");
            App app = new App();
            app = appService.getById(appId);
            ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR, "应用不存在");
//            User user = userService.getById(userId);
//            ThrowUtils.throwIf(user == null,ErrorCode.PARAMS_ERROR,"用户不存在");

        }
        // 修改数据时，有参数则校验
        //  补充校验规则
//        if (StringUtils.isNotBlank(title)) {
//            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
//        }
    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = questionQueryRequest.getId();
        List<QuestionContentDTO> questionContent = questionQueryRequest.getQuestionContent();
        Long appId = questionQueryRequest.getAppId();
        Long userId = questionQueryRequest.getUserId();
        String searchText = questionQueryRequest.getSearchText();

        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        // 模糊查询
        queryWrapper.like(!JSONUtil.isNull(questionContent), "questionContent", questionContent);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 对象转封装类
        QuestionVO questionVO = QuestionVO.objToVo(question);

        //  可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
//        long questionId = question.getId();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            // 获取点赞
//            QueryWrapper<QuestionThumb> questionThumbQueryWrapper = new QueryWrapper<>();
//            questionThumbQueryWrapper.in("questionId", questionId);
//            questionThumbQueryWrapper.eq("userId", loginUser.getId());
//            QuestionThumb questionThumb = questionThumbMapper.selectOne(questionThumbQueryWrapper);
//            questionVO.setHasThumb(questionThumb != null);
//            // 获取收藏
//            QueryWrapper<QuestionFavour> questionFavourQueryWrapper = new QueryWrapper<>();
//            questionFavourQueryWrapper.in("questionId", questionId);
//            questionFavourQueryWrapper.eq("userId", loginUser.getId());
//            QuestionFavour questionFavour = questionFavourMapper.selectOne(questionFavourQueryWrapper);
//            questionVO.setHasFavour(questionFavour != null);
//        }
        // endregion
        System.out.println("questionVO：---------------------" + questionVO);
        return questionVO;
    }

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            return QuestionVO.objToVo(question);
        }).collect(Collectors.toList());

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
//        Map<Long, Boolean> questionIdHasThumbMap = new HashMap<>();
//        Map<Long, Boolean> questionIdHasFavourMap = new HashMap<>();
//        User loginUser = userService.getLoginUserPermitNull(request);
//        if (loginUser != null) {
//            Set<Long> questionIdSet = questionList.stream().map(Question::getId).collect(Collectors.toSet());
//            loginUser = userService.getLoginUser(request);
//            // 获取点赞
//            QueryWrapper<QuestionThumb> questionThumbQueryWrapper = new QueryWrapper<>();
//            questionThumbQueryWrapper.in("questionId", questionIdSet);
//            questionThumbQueryWrapper.eq("userId", loginUser.getId());
//            List<QuestionThumb> questionQuestionThumbList = questionThumbMapper.selectList(questionThumbQueryWrapper);
//            questionQuestionThumbList.forEach(questionQuestionThumb -> questionIdHasThumbMap.put(questionQuestionThumb.getQuestionId(), true));
//            // 获取收藏
//            QueryWrapper<QuestionFavour> questionFavourQueryWrapper = new QueryWrapper<>();
//            questionFavourQueryWrapper.in("questionId", questionIdSet);
//            questionFavourQueryWrapper.eq("userId", loginUser.getId());
//            List<QuestionFavour> questionFavourList = questionFavourMapper.selectList(questionFavourQueryWrapper);
//            questionFavourList.forEach(questionFavour -> questionIdHasFavourMap.put(questionFavour.getQuestionId(), true));
//        }
        // 填充信息
        questionVOList.forEach(questionVO -> {
            Long userId = questionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
//            questionVO.setHasThumb(questionIdHasThumbMap.getOrDefault(questionVO.getId(), false));
//            questionVO.setHasFavour(questionIdHasFavourMap.getOrDefault(questionVO.getId(), false));
        });
        // endregion

        questionVOPage.setRecords(questionVOList);
        System.out.println("questionVOPage------------" + questionVOPage);
        return questionVOPage;
    }

    /**
     * 创建ai生成题目的用户消息
     *
     * @param app
     * @param questionNum
     * @param optionNum
     * @return
     */
    public  String getUserManage(App app, int questionNum, int optionNum) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(AppTypeEnum.getEnumByValue(app.getAppType()).getText() + "类").append("\n");
        userMessage.append(questionNum).append("\n");
        userMessage.append(optionNum);
        return userMessage.toString();
    }

    /**
     * AI生成题目
     *
     * @param app         APP
     * @param questionNum 题目数量
     * @param optionNum   选项数量
     * @return
     */
    @Override
    public List<QuestionContentDTO> getAIGenerateQuestion(App app, int questionNum, int optionNum) {
        String userMessage = getUserManage(app, questionNum, optionNum);
        String result = aiManager.doSyncRequest(AIGenerateSystemMessageConfig.AI_GENERATE_SYSTEM_MESSAGE, userMessage, null);
        int startIndex = result.indexOf("[");
        int endIndex = result.lastIndexOf("]");
        String json = result.substring(startIndex, endIndex + 1);
        List<QuestionContentDTO> questionContentDTOList = JSONUtil.toList(json, QuestionContentDTO.class);
        return questionContentDTOList;
    }

    /**
     * 获取AI生成题目的流式返回
     * @param app APP
     * @param questionNum 题目数量
     * @param optionNum 选项数量
     * @return
     */
    @Override
    public SseEmitter getSseAIGenerateQuestion(App app, int questionNum, int optionNum) {
        String userMessage = getUserManage(app, questionNum, optionNum);
        //这里传入的参数0L表示不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        //AI生成流式返回
        Flowable<ModelData> modelDataFlowable = aiManager.doStreamRequest(AIGenerateSystemMessageConfig.AI_GENERATE_SYSTEM_MESSAGE, userMessage, null);
        StringBuilder contentBuilder = new StringBuilder();
        //括号计数器
        AtomicInteger index = new AtomicInteger(0);
        modelDataFlowable.observeOn(Schedulers.io())
                .map(chunk -> chunk.getChoices().get(0).getDelta().getContent())
                .map(message -> message.replaceAll("\\s", ""))
                .filter(StrUtil::isNotBlank)
                .flatMap(message -> {
                    //将数组转换为List<Character>
                    List<Character> charList = new ArrayList<>();
                    for (char c : message.toCharArray()) {
                        charList.add(c);
                    }
                    return Flowable.fromIterable(charList);
                })
                .doOnNext(c -> {
                    if (c == '{') {
                        index.addAndGet(1);
                    }
                    if (index.get() > 0) {
                        contentBuilder.append(c);
                    }
                    if (c == '}') {
                        index.addAndGet(-1);
                        if (index.get() == 0) {
                            sseEmitter.send(JSONUtil.toJsonStr(contentBuilder.toString()));
                            contentBuilder.setLength(0);
                        }
                    }
                }).doOnComplete(sseEmitter::complete).subscribe();
        return sseEmitter;
    }
}
