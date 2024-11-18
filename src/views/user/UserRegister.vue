<template>
  <div class="register_page">
    <a-space
      direction="vertical"
      size="large"
      :style="{ width: '600px' }"
      class="login_box"
    >
      <a-form :model="form" @submit="handleSubmit" layout="horizontal">
        <a-form-item field="userAccount" label="账号">
          <a-input v-model="form.userAccount" placeholder="请输入你的账户" />
        </a-form-item>
        <a-form-item field="post" label="密码">
          <a-input
            type="password"
            v-model="form.userPassword"
            placeholder="请输入你的密码"
          />
        </a-form-item>
        <a-form-item field="post" label="确认密码">
          <a-input
            type="password"
            v-model="form.checkPassword"
            placeholder="请确认密码"
          />
        </a-form-item>
        <!--        <a-form-item field="isRead">-->
        <!--          <a-checkbox v-model="form.isRead">-->
        <!--            我已阅读并同意用户协议-->
        <!--          </a-checkbox>-->
        <!--        </a-form-item>-->
        <div>
          <a-form-item class="choose_button">
            <a-col flex="auto">
              <a-button html-type="submit" class="login_button">注册</a-button>
            </a-col>
            <a-col flex="130px">
              <a-row style="align-items: center"
                >已有帐号？
                <a-button class="login_button" @click="goLoginPage"
                  >登录
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
import { userRegisterUsingPost } from "@/api/userController";

const layout = ref("vertical");
const form = reactive({
  userAccount: "",
  userPassword: "",
  checkPassword: "",
}) as API.UserRegisterRequest;
const router = useRouter();

/**
 * 提交表单
 */
const handleSubmit = async () => {
  // console.log("执行注册方法");
  const res = await userRegisterUsingPost(form);
  if (res.data.code === 0) {
    message.success("注册成功");
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    message.error("注册失败：" + res.data.message);
  }
};

/**
 * 跳转到登录界面
 */
const goLoginPage = () => {
  router.push({
    path: "/user/login",
    replace: true,
  });
};
</script>

<style scoped>
.register_page {
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
