<script setup lang="ts"></script>

<template>
  <div class="userLayout">
    <a-layout style="height: 100vh">
      <a-layout-header class="userLayout_head">
        <a-space>
          <img src="../assets/logo.png" class="logo" />
          <div>问卷通-AI问卷生成答题平台</div>
        </a-space>
      </a-layout-header>
      <a-layout-content class="userLayout_content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a
          href="https://blog.csdn.net/L1817966?spm=1000.2115.3001.5343"
          style="text-decoration: none"
          target="_blank"
        >
          关于作者
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
.userLayout_head {
  display: flex;
  justify-content: center;
  margin-top: 16px;
  font-size: 21px;
  text-align: center;
}

.userLayout_content {
  margin-bottom: 28px;
  padding: 20px;
  background: url("https://gw.alipayobjects.com/zos/rmsportal/FfdJeJRQWjEeGTpqgBKj.png")
    0% 0% / 100% 100%;
}

.logo {
  height: 40px;
  padding: 10px;
}

.footer {
  padding: 16px;
  text-align: center;
  background: #efefef;
}
</style>
