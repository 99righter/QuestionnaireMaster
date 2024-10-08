package com.dazhuang.answerPlatform.manager;

import com.dazhuang.answerPlatform.config.AiConfig;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiManager {
    /**
     * 稳定的温度
     */
    private static final float STABLE_TEMPERATURE = 0.05f;
    /**
     * 随机性较大的温度
     */
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    @Resource
    private ClientV4 clientV4;

    /**
     * 构造不稳定的随机性的请求，答案较为随机
     */
    public String doUnstableRequest(String systemMessage, String userMessage) {
        return doSyncRequest(systemMessage, userMessage, UNSTABLE_TEMPERATURE);
    }

    /**
     * 构造稳定的随机请求，答案较为稳定
     */
    public String doStableRequest(String systemMessage, String userMessage) {
        return doSyncRequest(systemMessage, userMessage, STABLE_TEMPERATURE);
    }

    /**
     * 构造同步的通用请求,也就是不支持流式输出
     */
    public String doSyncRequest(String systemMessage, String userMessage, Float temperature) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, temperature);
    }

    /**
     * 构造通用的ai请求
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @param isStream      是否流式返回
     * @param temperature   采样温度，控制输出的随机性，温度越高，随机性越大
     * @return doRequest 输出的choice对象，内有生成的信息
     */
    public String doRequest(String systemMessage, String userMessage, Boolean isStream, Float temperature) {
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(systemChatMessage);
        chatMessages.add(userChatMessage);
        return doRequest(chatMessages, isStream, temperature);
    }

    /**
     * 构造ai请求
     *
     * @param chatMessages 对话消息
     * @param isStream     是否流式返回
     * @param temperature  采样温度，控制输出的随机性，温度越高，随机性越大
     * @return doRequest 生成的choice对象，内有输出信息
     */
    public String doRequest(List<ChatMessage> chatMessages, Boolean isStream, Float temperature) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .maxTokens(1024)
                .stream(isStream)
                .invokeMethod(Constants.invokeMethod)
                .messages(chatMessages)
                .temperature(temperature)
                .build();
        ModelApiResponse invokeModelApiResponse = clientV4.invokeModelApi(chatCompletionRequest);
        return invokeModelApiResponse.getData().getChoices().get(0).toString();
    }

    //regin 流式请求

    /**
     * 创建流式请求，生成一条返回一条
     *
     * @param chatMessages 对话消息
     * @param temperature  随机性参数
     * @return Flowable<ModelData> 流式返回
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> chatMessages, Float temperature) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .maxTokens(1024)
                .stream(Boolean.TRUE)
                .invokeMethod(Constants.invokeMethod)
                .messages(chatMessages)
                .temperature(temperature)
                .build();
        ModelApiResponse invokeModelApiResponse = clientV4.invokeModelApi(chatCompletionRequest);
        return invokeModelApiResponse.getFlowable();
    }

    /**
     * 简化的流式返回调用
     *
     * @param systemMessage 系统prompt
     * @param userMessage   用户消息
     * @param temperature   随机性
     * @return 流式返回
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage, String userMessage, Float temperature) {
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(systemChatMessage);
        chatMessages.add(userChatMessage);
        return doStreamRequest(chatMessages, temperature);
    }

    /**
     * 稳定的流式返回对象
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @return 流式返回
     */
    public Flowable<ModelData> doStableStreamRequest(String systemMessage, String userMessage) {
        return doStreamRequest(systemMessage, userMessage, STABLE_TEMPERATURE);
    }

    //endregion
}
