<script setup lang="ts">
import highlight from "@bytemd/plugin-highlight";
import gfm from "@bytemd/plugin-gfm";

/**
 * 定义组件类型
 */
interface Props {
  value: string;
}

const plugins = [gfm(), highlight()];
/**
 * 给组件指定初始值
 */
// eslint-disable-next-line no-undef
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
});
</script>

<template></template>

<style scoped></style>
