<template>
  <div class="login_page">
    <a-space
      direction="vertical"
      size="large"
      :style="{ width: '600px' }"
      class="login_box"
    >
      <a-form :model="form" @submit="AddAppSubmit" layout="horizontal">
        <a-form-item field="name" label="应用名">
          <a-input v-model="form.appName" placeholder="请输入你的应用名" />
        </a-form-item>
        <a-form-item field="post" label="应用描述">
          <a-input v-model="form.appDesc" placeholder="请输入你的应用描述" />
        </a-form-item>
        <a-form-item field="post" label="应用图标">
          <a-input v-model="form.appIcon" placeholder="请输入你的应用图标" />
        </a-form-item>
        <a-form-item field="appType" label="应用类型">
          <a-select
            v-model="form.appType"
            :style="{ width: '320px' }"
            placeholder="请选择应用类型"
          >
            <a-option
              v-for="(value, key) of APP_TYPE_MAP"
              :key="key"
              :value="Number(key)"
              :label="value"
            />
          </a-select>
        </a-form-item>
        <a-form-item field="scoringStrategy" label="评分策略">
          <a-select
            v-model="form.scoringStrategy"
            :style="{ width: '320px' }"
            placeholder="请选择评分策略"
          >
            <a-option
              v-for="(value, key) of APP_SCORING_STRATEGY_MAP"
              :key="key"
              :value="Number(key)"
              :label="value"
            />
          </a-select>
        </a-form-item>
        <div>
          <a-form-item class="choose_button">
            <a-col flex="auto">
              <a-button class="login_button" html-type="submit">登录</a-button>
            </a-col>
          </a-form-item>
        </div>
      </a-form>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import message from "@arco-design/web-vue/es/message";
import API from "@/api";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import { userLoginUsingPost } from "@/api/userController";
import { addAppUsingPost } from "@/api/appController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";

const layout = ref("vertical");
const form = reactive({
  appDesc: "",
  appIcon: "",
  appName: "",
  appType: 0,
  scoringStrategy: 0,
}) as API.AppAddRequest;
const router = useRouter();
/**
 * 提交表单
 */
const AddAppSubmit = async () => {
  console.log("执行注册方法");
  const res = await addAppUsingPost(form);
  if (res.data.code === 0) {
    message.success("创建成功");
    /**
     * 跳转到详情页面
     */
    router.push(`/app/detail/${res.data.data}`);
  } else {
    message.error("注册失败：" + res.data.message);
  }
};
</script>
<style scoped>
.login_page {
  display: flex;
  justify-content: center;
  height: 70vh;
  margin-top: 40px;
}

.choose_button {
  display: flex;
  display: -webkit-flex;
  justify-content: space-around;
}
</style>
