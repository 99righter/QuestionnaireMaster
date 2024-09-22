package com.dazhuang.answerPlatform.mapper;

import com.dazhuang.answerPlatform.model.dto.app.AppAnswerCountDTO;
import com.dazhuang.answerPlatform.model.dto.app.AppAnswerResultCountDTO;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author admin
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2024-08-17 20:53:01
 * @Entity com.dazhuang.answerPlatform.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {
    List<AppAnswerCountDTO> getAnswerGroupByApp();
    List<AppAnswerResultCountDTO> getAppAnswerResultCountGroupByResult(Long appId);
}




