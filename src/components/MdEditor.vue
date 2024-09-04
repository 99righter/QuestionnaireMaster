<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm";
import highlight from "@bytemd/plugin-highlight";
import { ref } from "vue";
import { Editor, Viewer } from "@bytemd/vue-next";
import "bytemd/dist/index.css";

const plugins = [gfm(), highlight()];
const value = ref("");

const handleChange = (v: string) => {
  value.value = v;
};

/**
 * 定义组件类型
 */
interface Props {
  value: string;
  mode?: string;
  handleChange: (v: string) => void;
}

/**
 * 指定组件的初始值
 */
// eslint-disable-next-line no-undef
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  mode: () => "split",
  handleChange: () => (v: string) => {
    console.log(v);
  },
});
</script>

<template>
  <Editor :value="value" :plugins="plugins" @change="handleChange" />
</template>

<style scoped>
.bytemd-toolbar-icon.bytemd-tippy.bytemd-tippy-right:last-child {
  display: none;
}
</style>
