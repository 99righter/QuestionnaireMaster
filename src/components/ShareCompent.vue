<script setup lang="ts">
import QRCode from "qrcode";
import { withDefaults, ref, defineProps, defineExpose } from "vue";

/**
 * 定义标题和url
 */
interface Props {
  title: string;
  link: string;
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  title: () => "分享",
  link: () => "https://github.com/99righter/",
});

const codeImg = ref("");
const visible = ref(false);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};
QRCode.toDataURL(props.link)
  .then((url) => {
    console.log(url);
    codeImg.value = url;
  })
  .catch((err) => {
    console.error(err);
  });
/**
 * 将组件的handleClick方法暴露给父组件
 */
defineExpose({
  handleClick,
});
</script>

<template>
  <!--  <a-button @click="handleClick">分享问卷</a-button>-->
  <a-modal v-model:visible="visible" @ok="handleOk" @cancel="handleCancel">
    <template #title>分享</template>
    <h4 style="margin-top: 0">复制分享链接</h4>
    <a-typography-paragraph copyable>{{ link }}</a-typography-paragraph>
    <h4>手机扫码查看</h4>
    <div>{{ link }}</div>
    <img :src="codeImg" />
  </a-modal>
</template>

<style scoped></style>
