<template>
  <a-button @click="handleClick">AI生成题目</a-button>
  <a-modal
    v-model:visible="visible"
    title="AI生成应用"
    @ok="handleOk"
    hide-cancel
    ok-text="取消"
  >
    <a-form :model="form">
      <a-form-item field="name" label="题目数量">
        <a-input-number v-model="form.questionNum" />
      </a-form-item>
      <a-form-item field="name" label="选项数量">
        <a-input-number v-model="form.optionNum" />
      </a-form-item>
      <a-space>
        <a-button html-type="submit" @click="handleAiData" :loading="generating"
          >一键生成
        </a-button>
        <a-button @click="handleSseAIGenrate" :loading="generating"
          >实时生成
        </a-button>
      </a-space>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, ref, withDefaults, defineProps } from "vue";
import API from "@/api";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";

/**
 * 接受父页面传入来的appId
 */
interface Props {
  appId: string;
  onSuccess?: (result: API.QuestionContentDTO[]) => void;
  onSseSuccess?: (result: API.QuestionContentDTO) => void;
  onSseStart?: (event: any) => void;
  onSseError?: (event: any) => void;
  generateQuestionNumber?: (generateQuestionCount: any) => void;
}

/**
 * 初始化默认值
 */
const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const visible = ref(false);
/**
 * 定义发送的请求的消息的默认值
 */
const form = ref({
  questionNum: 10,
  optionNum: 2,
} as API.AIGenerateRequest);
const generating = ref(false);
const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleAiData = async () => {
  if (!props.appId) {
    return;
  }
  console.log("生成题目的数量" + form.value.questionNum);
  generating.value = true;
  const res = await aiGenerateQuestionUsingPost({
    appId: props.appId as any,
    ...form.value,
  });
  if (res.data.data == null) {
    return;
  }
  if (res.data.code == 0 && res.data.data.length > 0) {
    if (props.onSuccess) {
      props.onSuccess(res.data.data);
    } else {
      message.success("生成成功");
    }
  } else {
    message.error("生成失败" + res.data.message);
  }
  generating.value = false;
  visible.value = false;
};
const handleSseAIGenrate = async () => {
  if (!props.appId) {
    return;
  }
  console.log("生成题目的数量" + form.value.questionNum);
  generating.value = true;
  const eventSource = new EventSource(
    //需要手动填写完整的后端地址
    "http://localhost:8101/api/question/ai_generate/sse" +
      `?appId=${props.appId}&optionNum=${form.value.optionNum}&questionNum=${form.value.questionNum}`
  );
  /**
   * 开始生成
   * @param event
   */
  eventSource.onopen = function (event) {
    console.log("开始生成");
    // message.success("开始生成");
    props.onSseStart?.(event);
    props.generateQuestionNumber?.(form.value.questionNum);
    visible.value = false;
  };
  /**
   * 接收到消息
   * @param event
   */
  eventSource.onmessage = function (event) {
    console.log(event.data);
    props.onSseSuccess?.(JSON.parse(event.data));
  };
  /**
   * 生成错误或者是结束
   * @param event
   */
  eventSource.onerror = function (event) {
    if (event.eventPhase == EventSource.CLOSED) {
      console.log("生成完成");
      // message.success("生成完成");
      eventSource.close();
      props.onSseError?.(event);
    }
  };
  generating.value = false;
  visible.value = false;
};
</script>
