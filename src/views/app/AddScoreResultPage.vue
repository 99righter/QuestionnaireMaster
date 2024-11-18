<script setup lang="ts">
import AppScoreResultPage from "@/views/app/components/AppScoreResultPage.vue";
import { defineProps, ref, watchEffect, withDefaults } from "vue";
import API from "@/api";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { APP_TYPE_ENUM } from "@/constant/app";
import {
  addScoringResultUsingPost,
  editScoringResultUsingPost,
} from "@/api/scoringResultController";
import PictureUpload from "@/components/PictureUpload.vue";

interface Props {
  id: string;
}

const resultImageUrl = ref<string>("");
const getResultImageUrl = (url: any) => {
  resultImageUrl.value = url;
};
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const visible = ref(false);
const intiScoringResult = {
  resultDesc: "",
  resultName: "",
  resultPicture: "",
};
const tableRef = ref();

const formScoringResult = ref({
  resultDesc: "",
  resultName: "",
  resultPicture: "",
} as API.ScoringResultAddRequest);
const handleClick = () => {
  //如果是添加的话就需要初始化formScoringResult;
  formScoringResult.value = {};
  visible.value = true;
};
const handleBeforeOk = async () => {
  console.log("执行了打开弹窗方法");
  formScoringResult.value = {
    ...intiScoringResult,
  };
  console.log("是否为更新方法" + updateId.value);
  visible.value = false;
};
/**
 * 上传结果集
 */
const handleOK = async () => {
  if (!props.id) {
    return;
  }
  let res: any;
  //如果是修改
  if (updateId.value) {
    if (resultImageUrl.value) {
      formScoringResult.value.resultPicture = resultImageUrl.value;
    }
    res = await editScoringResultUsingPost({
      id: updateId.value as any,
      ...formScoringResult.value,
    });
  } else {
    // console.log("结果图片的url" + resultImageUrl.value);
    formScoringResult.value.resultPicture = resultImageUrl.value;
    // console.log(
    //   "formScoringResult的url" + formScoringResult.value.resultPicture
    // );
    res = await addScoringResultUsingPost({
      appId: props.id as any,
      ...formScoringResult.value,
    });
  }
  resultImageUrl.value = "";
  console.log("将resultImageUrl置空" + resultImageUrl.value);
  formScoringResult.value.resultPicture = "";
  console.log(
    "将formScoringResult的url置空" + formScoringResult.value.resultPicture
  );
  if (res.data.code === 0 && updateId.value) {
    // console.log(res.data.message);
    message.success("更新成功");
  } else if (res.data.code !== 0 && updateId.value) {
    message.error("更新失败," + res.data.message);
  } else if (res.data.code === 0 && !updateId.value) {
    message.success("添加成功");
  } else {
    message.error("添加失败" + res.data.message);
  }
  if (tableRef.value) {
    await tableRef.value.loadData();
    // console.log("执行了数据加载方法");
    updateId.value = undefined;
  }
  formScoringResult.value = {
    ...intiScoringResult,
  };
  visible.value = false;
};
/**
 * 根据Appid获取应用
 */
const appData = ref<API.AppVO>({});
/**
 * 更新结果
 */
const updateId = ref<any>();
const imageUrl = ref("");
const doUpdate = (scoringResult: API.ScoringResultVO) => {
  // console.log("执行更新方法");
  visible.value = true;
  updateId.value = scoringResult.id;
  imageUrl.value = scoringResult.resultPicture as any;
  formScoringResult.value = scoringResult;
  console.log(
    "formScoringResult.value:" + JSON.stringify(formScoringResult.value)
  );
};
/**
 * 获取应用
 */
const loadAppData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id as any,
  });
  if (res.data.code === 0) {
    appData.value = res.data.data as any;
  } else {
    message.error("获取应用详情失败," + res.data.message);
  }
};
watchEffect(() => {
  loadAppData();
});
</script>

<template>
  <div class="addScoreResultPage">
    <h2>当前应用id:{{ id }}</h2>
    <div class="inputScoringResultModal">
      <a-button @click="handleClick">创建一条新的评分结果</a-button>
      <a-modal
        v-model:visible="visible"
        title="评分结果"
        @before-ok="handleBeforeOk"
        hide-cancel
        ok-text="取消"
      >
        <a-form :model="formScoringResult">
          <a-form-item field="resultName" label="结果名称">
            <a-input
              v-model="formScoringResult.resultName"
              placeholder="请输入结果名称"
            />
          </a-form-item>
          <a-form-item field="resultDesc" label="结果描述">
            <a-input
              v-model="formScoringResult.resultDesc"
              placeholder="请输入结果描述"
            />
          </a-form-item>
          <a-form-item field="resultPicture" label="结果图片">
            <!--            <a-input-->
            <!--              v-model="formScoringResult.resultPicture"-->
            <!--              placeholder="请输入结果图片"-->
            <!--            />-->
            <PictureUpload
              :onChange="getResultImageUrl"
              :imageUrl="formScoringResult.resultPicture"
            />
          </a-form-item>
          <a-form-item
            v-if="appData.appType === APP_TYPE_ENUM.TEST"
            field="resultProp"
            label="结果集"
          >
            <a-input-tag
              v-model="formScoringResult.resultProp"
              :style="{ width: '320px' }"
              placeholder="按下↩︎确认标签"
              allow-clear
            />
          </a-form-item>
          <a-form-item v-else field="resultScoringRange" label="结果得分">
            <a-input-number
              v-model="formScoringResult.resultScoreRange"
              placeholder="请输入结果得分"
            />
          </a-form-item>
          <a-button status="success" html-type="submit" @click="handleOK"
            >提交
          </a-button>
        </a-form>
        <!--        {{ formScoringResult.resultPicture }}-->
      </a-modal>
    </div>
    <div class="appScoringResultTable">
      <AppScoreResultPage :appId="id" :doUpdate="doUpdate" ref="tableRef" />
    </div>
  </div>
</template>

<style scoped>
.inputScoringResultModal {
  margin-top: 20px;
}

.appScoringResultTable {
  margin-top: 20px;
}
</style>
