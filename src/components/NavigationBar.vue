<script setup lang="ts">
import { computed, ref } from "vue";
import API from "@/api";

const handleSelect = (v: any) => {
  console.log(v);
};
import { routes } from "@/router/routers";
import { useRoute, useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import checkcAccess from "@/access/checkAccess";
import { userLogoutUsingPost } from "@/api/userController";
import message from "@arco-design/web-vue/es/message";
import AccessEnum from "@/access/accessEnum";

//获取当前用户的登录态
const loginUserStore = useLoginUserStore();

const router = useRouter();
//tab栏中选中菜单项
const selectedKeys = ref(["/"]);

//跳转路由后，更新菜单选项
router.afterEach((to) => {
  selectedKeys.value = [to.path];
});
//路由跳转事件
const doMenuClick = (key: string) => {
  router.push({
    path: key,
  });
};
const goLoginPage = () => {
  router.push({
    path: "/user/login",
  });
};

console.log("头部组件打印：" + loginUserStore.loginUser);
const showRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hidInMenu) {
      return false;
    }
    if (!checkcAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      console.log(
        "权限不匹配的过滤" +
          item.path.toString() +
          loginUserStore.loginUser.userRole?.toString() +
          "登录用户名" +
          loginUserStore.loginUser.userName
      );
      return false;
    }
    return true;
  });
});
const loginOutUser = ref<API.LoginUserVO>({});
const loginOut = async () => {
  console.log("退出登录");
  const res = await userLogoutUsingPost();
  if (res.data.code == 0) {
    router.push({
      path: "/user/login",
    });
  } else {
    message.error("退出登录失败" + res.data.message);
  }
};
const goUserCenter = () => {
  router.push({
    path: "/user/center",
  });
};
// const showLogin=()
</script>

<template>
  <a-row id="navigationBar" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        class="titleInNavigationBar"
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar">
            <img src="../assets/logo.png" class="logo" />
            <div class="title">问卷通</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="route in showRoutes" :key="route.path">
          <div v-if="!route.children || route.children.length == 0">
            <a-menu-item :key="route.path">{{ route.name }}</a-menu-item>
          </div>
          <div v-else>
            <a-sub-menu :key="route.path">
              <template #title>{{ route.name }}</template>
              <a-menu-item v-for="child in route.children" :key="child.path">
                {{ child.name }}
              </a-menu-item>
            </a-sub-menu>
          </div>
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px" class="userInfoBar">
      <div v-if="!loginUserStore.loginUser.id">
        <a-button @click="goLoginPage">登录</a-button>
      </div>
      <div v-else>
        <a-space size="large">
          <a-dropdown @select="handleSelect">
            <a-button>{{ loginUserStore.loginUser.userName }}</a-button>
            <template #content>
              <a-doption @click="goUserCenter">个人空间</a-doption>
              <a-doption @click="loginOut">退出</a-doption>
            </template>
          </a-dropdown>
        </a-space>
      </div>
    </a-col>
  </a-row>
</template>
<style scoped>
.navigationBar {
  box-sizing: border-box;
  width: 100%;
  padding: 40px;
  background-color: var(--color-neutral-2);
}

.title-bar {
  display: flex;
  text-align: center;
}

.title {
  display: flex;
  align-items: center;
  margin-left: 5px;
  color: black;
  font-size: 16px;
  text-align: center;
}

.logo {
  height: 48px;
}

.userInfoBar {
  background-color: #ffffff;
}

.arco-dropdown-open .arco-icon-down {
  transform: rotate(180deg);
}
</style>
