<script setup lang="ts">
import NavigationBar from "@/components/NavigationBar.vue";
</script>

<template>
  <div class="basicLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="navigation_head">
        <navigation-bar />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
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
