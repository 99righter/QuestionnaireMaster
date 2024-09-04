<template>
  <div id="app">
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>

<style scoped>
#app {
  background: #fafafa;
}
</style>
<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { useRoute } from "vue-router";
import { getLoginUserUsingGet } from "@/api/userController";

const route = useRoute();

getLoginUserUsingGet().then((res) => {
  console.log(res);
});
</script>
