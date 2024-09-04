<template>
  <a-card class="appCard" hoverable @click="doCardClick">
    <template #actions>
      <!--      <span class="icon-hover"> <IconThumbUp /> </span>-->
      <span class="icon-hover"> <IconShareInternal /> </span>
    </template>
    <template #cover>
      <div
        :style="{
          height: '204px',
          overflow: 'hidden',
        }"
      >
        <img
          :style="{ width: '100%', transform: 'translateY(-20px)' }"
          :alt="app.appName"
          :src="app.appIcon"
        />
      </div>
    </template>
    <a-card-meta :title="app.appName" :description="app.appDesc">
      <template #avatar>
        <div
          :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
        >
          <a-avatar
            :size="24"
            :image-url="app.user?.userAvatar"
            :style="{ marginRight: '8px' }"
          />
          <a-typography-text
            >{{ app.user?.userName ?? "无名" }}
          </a-typography-text>
        </div>
      </template>
    </a-card-meta>
  </a-card>
</template>

<script setup lang="ts">
import { IconShareInternal } from "@arco-design/web-vue/es/icon";
import API from "@/api";
import { defineProps, withDefaults } from "vue";
import { useRouter } from "vue-router";

/**
 * 定义一个接口类型，里面的数据为app，类型为AppVO
 */
interface Props {
  app: API.AppVO;
}

/**
 * 定义一个初始化函数，如果没有提供值的话则为默认空值
 */
const props = withDefaults(defineProps<Props>(), {
  app: () => {
    return {};
  },
});
/**
 * 点击后跳转至应用详情界面
 */
const router = useRouter();
const doCardClick = () => {
  router.push(`/app/detail/${props.app.id}`);
};
</script>
<style scoped>
.appCard {
  cursor: pointer;
}

.icon-hover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.1s;
}

.icon-hover:hover {
  background-color: rgb(var(--gray-2));
}
</style>
