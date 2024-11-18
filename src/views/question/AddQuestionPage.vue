<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import API from "@/api";
import { withDefaults, defineProps } from "vue";
import { useRouter } from "vue-router";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { APP_TYPE_ENUM } from "@/constant/app";
import {
  addQuestionUsingPost,
  listQuestionVoByPageUsingPost,
  updateQuestionUsingPost,
} from "@/api/questionController";
import AIGenerateDrawer from "@/views/question/AIGenerateDrawer.vue";

/**
 * 定义题目列表
 */
const questionContent = ref<API.QuestionContentDTO[]>([]);

interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});
/**
 * 获取当前App
 */
const data = ref<API.AppVO>({});

/**
 * 获取应用
 */
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });
  if (res.data.code === 0) {
    data.value = res.data.data as any;
  } else {
    message.error("获取应用详情失败," + res.data.message);
  }
};

/**
 * 定义路由
 */
const router = useRouter();
/**
 * 添加题目
 */

const addQuestion = (index: number) => {
  // console.log("当前题目编号" + index);
  questionContent.value.splice(index, 0, {
    title: "",
    options: [],
  });
};
/**
 * 删除题目
 * @param index
 */
const handleDelete = (index: number) => {
  questionContent.value.splice(index, 1);
};
/**
 * 添加选项
 */
const addOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 0, {
    key: "",
    value: "",
  });
};
/**
 * 删除选项
 */
const deleteOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 1);
};
/**
 * 加载问题数据
 */
const oldQuestion = ref<API.QuestionVO>();
const loadQuestion = async () => {
  if (!props.appId) {
    return;
  }
  // console.log("搜索的题目的应用id" + props.appId);
  const res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    oldQuestion.value = res.data.data?.records[0];
    if (oldQuestion.value) {
      questionContent.value = oldQuestion.value.questionContent ?? [];
    }
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
const onAiGenrateQuestion = (result: API.QuestionContentDTO[]) => {
  questionContent.value = [...questionContent.value, ...result];
  message.success(`AI生成题目成功，生成了${result.length}道题目`);
};
/**
 * AI生成题目流式返回
 * @param result
 */
const generateQuestionCount = ref<number>(0);
const generateSchedule = (generateNumber: any) => {
  generateQuestionCount.value = generateNumber;
};
const currentGenerateNumber = ref<number>(0);
const onAiGenerateSseSuccess = (result: API.QuestionContentDTO) => {
  questionContent.value = [...questionContent.value, result];
  currentGenerateNumber.value++;
};
const onSSEStart = (event: any) => {
  message.success("开始生成");
  currentGenerateNumber.value = 0;
};
const onSSEClose = (event: any) => {
  message.success("生成完毕");
};
/**
 *提交数据
 */
const handleNewData = async () => {
  if (
    !props.appId ||
    !questionContent.value ||
    questionContent.value.length == 0
  ) {
    return;
  }
  let res: any;
  /**
   * 如果是修改的话
   */
  if (oldQuestion.value?.id) {
    res = await updateQuestionUsingPost({
      id: oldQuestion.value.id,
      questionContent: questionContent.value,
    });
  } else {
    res = await addQuestionUsingPost({
      appId: props.appId as any,
      questionContent: questionContent.value,
    });
  }
  if (res.data.code === 0) {
    message.success("提交成功,即将跳转到页面详情页");
    setTimeout(() => {
      router.push({
        // path: "/app/detail/" + props.appId,
        path: `/app/detail/${props.appId}`, //又少了“/”你真牛逼
        replace: true,
      });
    }, 1000);
  } else {
    message.error("提交失败，" + res.data.message);
  }
};
watchEffect(() => {
  loadData();
  loadQuestion();
});
</script>

<template>
  <div class="add-question">
    <h2>设置题目页面</h2>
    <a-form :model="questionContent" :style="{ width: '600px' }">
      <a-form-item field="name" label="应用编号">{{ appId }}</a-form-item>

      <a-form-item label="题目列表" :content-flex="false" :merge-props="false">
        <a-space size="medium">
          <a-button
            @click="addQuestion(questionContent.length)"
            size="large"
            status="warning"
            >从末尾添加题目
          </a-button>
          <AIGenerateDrawer
            :appId="appId"
            :onSuccess="onAiGenrateQuestion"
            :onSseSuccess="onAiGenerateSseSuccess"
            :onSseError="onSSEClose"
            :onSseStart="onSSEStart"
            :generateQuestionNumber="generateSchedule"
          />
        </a-space>
        <a-space
          style="margin-top: 10px"
          direction="vertical"
          :style="{ width: '50%' }"
          v-if="generateQuestionCount != 0"
        >
          <a-col>
            <div>生成进度：</div>
            <a-progress
              :percent="currentGenerateNumber / generateQuestionCount"
            />
          </a-col>
        </a-space>
        <div v-for="(question, index) of questionContent" :key="index">
          <a-form-item label="题目编号">{{ index + 1 }}</a-form-item>
          <a-form-item label="应用描述" :key="index">
            <a-input v-model="question.title" placeholder="请输入题目描述" />
            <a-space size="small">
              <a-button
                @click="handleDelete(index)"
                :style="{ marginLeft: '10px' }"
                status="danger"
                >删除题目
              </a-button>
              <a-button @click="addQuestion(index + 1)">添加题目</a-button>
            </a-space>
          </a-form-item>
          <a-space>
            <h4>题目 {{ index + 1 }} 选项列表</h4>
            <a-button @click="addOption(question, question.options.length)"
              >添加选项
            </a-button>
          </a-space>
          <a-form-item
            v-for="(option, optionIndex) in question.options"
            :key="optionIndex"
            :label="`选项 ${optionIndex + 1}`"
            :content-flex="false"
            :merge-props="false"
          >
            <a-form-item label="选项 key">
              <a-input v-model="option.key" placeholder="请输入选项 key" />
            </a-form-item>
            <a-form-item label="选项值">
              <a-input v-model="option.value" placeholder="请输入选项值" />
            </a-form-item>
            <a-form-item
              v-if="data.appType == APP_TYPE_ENUM.TEST"
              label="选项结果"
            >
              <a-input v-model="option.result" placeholder="请输入选项结果" />
            </a-form-item>
            <a-form-item v-else label="选项得分">
              <a-input-number
                v-model="option.score"
                placeholder="请输入选项得分"
              />
            </a-form-item>
            <a-space size="large">
              <a-button
                size="mini"
                @click="addOption(question, optionIndex + 1)"
              >
                添加选项
              </a-button>
              <a-button
                size="mini"
                status="danger"
                @click="deleteOption(question, optionIndex as any)"
              >
                删除选项
              </a-button>
            </a-space>
          </a-form-item>
        </div>
      </a-form-item>
      <a-button html-type="submit" @click="handleNewData">提交</a-button>
    </a-form>
  </div>
</template>

<style scoped>
.add-question {
  margin-top: 20px;
}
</style>
