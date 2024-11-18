<script setup lang="ts">
import { onBeforeRouteUpdate, useRouter } from "vue-router";
import NavigationBar from "@/components/NavigationBar.vue";
import { ref, watch } from "vue";

// 使用ref创建响应式数据，用于控制router-view的显示
const showOrNot = ref(true);
const router = useRouter();

// 监听路由变化
watch(
  () => router.currentRoute.value,
  (newRoute, oldRoute) => {
    const meta = newRoute.meta;

    if ("showFather" in meta) {
      showOrNot.value = meta.showFather as boolean;
    } else {
      // 如果没有showFather属性，默认显示子路由（这里可根据实际需求调整）
      showOrNot.value = true;
    }
  },
  { immediate: true }
);
</script>

<template>
  <div class="basicLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="navigation_head">
        <navigation-bar />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footer">
        <a
          href="https://blog.csdn.net/L1817966?spm=1000.2115.3001.5343"
          style="text-decoration: none"
          target="_blank"
        >
          关于我
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
.content {
  box-sizing: border-box;
  width: 100%;
  max-width: 1400px;
  margin: 0 auto 28px;
  padding: 20px;
  background: linear-gradient(to right, #ffffff, #fafafa);
}

.footer {
  padding: 16px;
  text-align: center;
  background: #efefef;
}
</style>
