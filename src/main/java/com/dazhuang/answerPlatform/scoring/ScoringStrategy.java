package com.dazhuang.answerPlatform.scoring;

import com.dazhuang.answerPlatform.model.entity.App;
import com.dazhuang.answerPlatform.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ScoringStrategy {
    UserAnswer doScore(List<String> choices, App app);

}
