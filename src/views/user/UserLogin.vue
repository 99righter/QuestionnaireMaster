<template>
  <div class="login_page">
    <a-space
      direction="vertical"
      size="large"
      :style="{ width: '600px' }"
      class="login_box"
    >
      <a-form :model="form" @submit="handleSubmit" layout="horizontal">
        <a-form-item field="name" label="账号">
          <a-input v-model="form.userAccount" placeholder="请输入你的账户" />
        </a-form-item>
        <a-form-item field="post" label="密码">
          <a-input-password
            v-model="form.userPassword"
            placeholder="请输入你的密码"
          />
        </a-form-item>
        <div>
          <a-form-item class="choose_button">
            <a-col flex="auto">
              <a-button class="login_button" html-type="submit">登录</a-button>
            </a-col>
            <a-col flex="130px">
              <a-row style="align-items: center"
                >没有账号？
                <a-button class="register_button" @click="goRegisterPage"
                  >注册
                </a-button>
              </a-row>
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

const layout = ref("vertical");
const form = reactive({
  userAccount: "",
  userPassword: "",
}) as API.UserLoginRequest;
const router = useRouter();
const loginUserStore = useLoginUserStore();

/**
 * 提交表单
 */
const handleSubmit = async () => {
  const res = await userLoginUsingPost(form);
  if (res.data.code === 0) {
    await loginUserStore.fetchLoginUser();
    message.success("登录成功");
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("登陆失败：" + res.data.message);
  }
};
const checkBox = false;
/**
 * 跳转到注册界面
 */
const goRegisterPage = () => {
  router.push({
    path: "/user/register",
    replace: true,
  });
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
