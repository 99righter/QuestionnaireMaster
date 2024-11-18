<script setup lang="ts">
import NavigationBar from "@/components/NavigationBar.vue";
import { useLoginUserStore } from "@/store/userStore";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import AdminAppPage from "@/views/admin/AdminAppPage.vue";
import UserAppPage from "@/views/user/UserAppPage.vue";
import { computed, ref } from "vue";
import UserEditCompont from "@/components/UserEditCompont.vue";
import API from "@/api";
import { getLoginUserUsingGet } from "@/api/userController";

const loginUser = useLoginUserStore().loginUser;
// todo
//如何使得数据自动刷新？我可以用一个变量先接受用户登录态，然后再改变这个登录态，再写回？
// todo 如何自动更新用户全局状态
const currentUser = ref<API.LoginUserVO>({
  ...loginUser,
});
let refreshDom = 0;
const refreshUser = async () => {
  const res = await getLoginUserUsingGet();
  if (res.data.code === 0) {
    currentUser.value = res.data.data as any;
    refreshDom++;
  }
  console.log("执行了父组件方法");
  console.log("currentUser" + JSON.stringify(currentUser.value));
};

//采用计算属性，将 data 改为计算属性，这样它会根据 currentUser 的变化自动重新计算。
const data = computed(() => [
  {
    label: "id",
    value: currentUser.value.id,
  },
  {
    label: "账号",
    value: currentUser.value.userAccount,
  },
  {
    label: "用户名",
    value: currentUser.value.userName,
  },
  // {
  //   label: "用户头像",
  //   value: currentUser.value.userAvatar,
  // },
  {
    label: "用户简介",
    value: currentUser.value.userProfile,
  },
  {
    label: "注册时间",
    value: currentUser.value.createTime,
  },
]);

const openOrClose = ref();
const openEditDraw = () => {
  if (openOrClose.value) {
    openOrClose.value.handleClick();
  }
};
const editUserInfo = ref<API.UserUpdateRequest>({
  ...currentUser.value,
});
</script>

<template>
  <a-layout>
    <a-layout-header>
      <navigation-bar />
    </a-layout-header>
    <a-layout-content class="user-center-main">
      <a-row
        class="grid-demo"
        style="
          margin-bottom: 16px;
          margin-left: 40px;
          margin-right: 40px;
          background-color: #efefef;
        "
      >
        <a-col flex="160px" style="padding: 20px; margin-top: 30px">
          <!--          <img-->
          <!--            :src="loginUser.userAvatar"-->
          <!--            style="width: 100px; height: 100px"-->
          <!--          />-->
          <a-avatar
            :image-url="currentUser.userAvatar"
            style="width: 100px; height: 100px"
          />
        </a-col>
        <a-col flex="auto">
          <a-descriptions
            style="margin-top: 20px"
            :size="'large'"
            title="用户信息"
            :column="1"
            :key="refreshDom"
          >
            <a-descriptions-item
              v-for="item of data"
              :label="item.label"
              :key="item.label"
            >
              <div v-if="item.label === '注册时间'">
                {{ dayjs(item.value).format("YYYY-MM-DD HH:mm:ss") }}
              </div>
              <div v-else>
                {{ item.value }}
              </div>
            </a-descriptions-item>
          </a-descriptions>
        </a-col>
        <a-col flex="100px">
          <a-button
            type="primary"
            @click="openEditDraw"
            style="margin-top: 20px"
            >编辑
          </a-button>
        </a-col>
        <UserEditCompont
          ref="openOrClose"
          :editUser="editUserInfo"
          :refreshData="refreshUser"
        />
      </a-row>
    </a-layout-content>
    <div style="margin-top: 20px; margin-left: 40px; margin-right: 40px">
      <h3>我创建的应用</h3>
      <a-divider />
      <UserAppPage :userId="loginUser.id" />
    </div>
  </a-layout>
</template>

<style scoped>
.user-center-main {
  width: 100%;
  height: 100%;
}
</style>
