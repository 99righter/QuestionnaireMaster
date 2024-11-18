<script setup lang="ts">
import { APP_TYPE_ENUM } from "@/constant/app";
import PictureUpload from "@/components/PictureUpload.vue";
import { defineProps, ref, watch, withDefaults } from "vue";
import API from "@/api";
import {
  addScoringResultUsingPost,
  editScoringResultUsingPost,
} from "@/api/scoringResultController";
import message from "@arco-design/web-vue/es/message";

interface Props {
  id: string;
  editAppType: number;
  doScoringResultUpdate?: API.ScoringResultAddRequest;
  closeFather: () => void;
  updateId?: string;
}

const resultImageUrl = ref<string>("");
const getResultImageUrl = (url: any) => {
  resultImageUrl.value = url;
};
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
  editAppType: () => {
    return 0;
  },
  doScoringResultUpdate: () => {
    return {} as API.ScoringResultAddRequest;
  },
});

const intiScoringResult = {
  resultDesc: "",
  resultName: "",
  resultPicture: "",
};

const formScoringResult = ref({
  resultDesc: "",
  resultName: "",
  resultPicture: "",
} as API.ScoringResultAddRequest);
const updateId = ref<any>();
watch(props, (newValue) => {
  formScoringResult.value = {
    ...props.doScoringResultUpdate,
  };
});
const handleOK = async () => {
  console.log("执行了handleOK方法");
  if (!props.id) {
    console.log("id为空");
    return;
  }
  let res: any;
  console.log("传过来的值" + JSON.stringify(props.doScoringResultUpdate));
  //如果是修改
  if (props.updateId) {
    if (resultImageUrl.value) {
      formScoringResult.value.resultPicture = resultImageUrl.value;
    }
    res = await editScoringResultUsingPost({
      id: props.updateId as any,
      ...formScoringResult.value,
    });
  } else {
    formScoringResult.value.resultPicture = resultImageUrl.value;
    console.log("执行了添加方法");
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
  formScoringResult.value = {
    ...intiScoringResult,
  };
  props.closeFather();
};
</script>

<template>
  <div>
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
        <PictureUpload
          :onChange="getResultImageUrl"
          :imageUrl="formScoringResult.resultPicture"
        />
      </a-form-item>
      <a-form-item
        v-if="editAppType === APP_TYPE_ENUM.TEST"
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
  </div>
</template>

<style scoped></style>
