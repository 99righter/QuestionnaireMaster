<template>
  <a-space direction="vertical" :style="{ width: '100%' }">
    <a-upload
      :fileList="file ? [file] : []"
      :show-file-list="false"
      :custom-request="customRequest"
    >
      <template #upload-button>
        <div
          :class="`arco-upload-list-item${
            file && file.status === 'error'
              ? ' arco-upload-list-item-error'
              : ''
          }`"
        >
          <div
            class="arco-upload-list-picture custom-upload-avatar"
            v-if="file && file.url"
          >
            <img :src="file.url" />
            <div class="arco-upload-list-picture-mask">
              <IconEdit />
            </div>
            <a-progress
              v-if="file.status === 'uploading' && file.percent < 100"
              :percent="file.percent"
              type="circle"
              size="mini"
              :style="{
                position: 'absolute',
                left: '50%',
                top: '50%',
                transform: 'translateX(-50%) translateY(-50%)',
              }"
            />
          </div>
          <!--          <div-->
          <!--            class="arco-upload-list-picture custom-upload-avatar"-->
          <!--            v-else-if="!file && !file.url && props.imageUrl"-->
          <!--          >-->
          <!--            <img :src="props.imageUrl" />-->
          <!--            <div class="arco-upload-list-picture-mask">-->
          <!--              <IconEdit />-->
          <!--            </div>-->
          <!--            &lt;!&ndash;            <a-progress&ndash;&gt;-->
          <!--            &lt;!&ndash;              v-if="file.status === 'uploading' && file.percent < 100"&ndash;&gt;-->
          <!--            &lt;!&ndash;              :percent="file.percent"&ndash;&gt;-->
          <!--            &lt;!&ndash;              type="circle"&ndash;&gt;-->
          <!--            &lt;!&ndash;              size="mini"&ndash;&gt;-->
          <!--            &lt;!&ndash;              :style="{&ndash;&gt;-->
          <!--            &lt;!&ndash;                position: 'absolute',&ndash;&gt;-->
          <!--            &lt;!&ndash;                left: '50%',&ndash;&gt;-->
          <!--            &lt;!&ndash;                top: '50%',&ndash;&gt;-->
          <!--            &lt;!&ndash;                transform: 'translateX(-50%) translateY(-50%)',&ndash;&gt;-->
          <!--            &lt;!&ndash;              }"&ndash;&gt;-->
          <!--            &lt;!&ndash;            />&ndash;&gt;-->
          <!--          </div>-->
          <div class="arco-upload-picture-card" v-else>
            <!--            <img :src="props.imageUrl" />-->

            <div class="arco-upload-picture-card-text">
              <IconPlus />
              <div style="margin-top: 10px; font-weight: 600">上传</div>
            </div>
          </div>
        </div>
      </template>
    </a-upload>
  </a-space>
</template>

<script setup lang="ts">
import { IconEdit, IconPlus } from "@arco-design/web-vue/es/icon";
import { defineProps, ref, withDefaults } from "vue";
import { uploadFileUsingPost } from "@/api/fileController";
import { Message } from "@arco-design/web-vue";

/**
 * 定义组件属性类型
 */
interface Props {
  // biz: string;
  onChange?: (url: string) => void;
  value?: string;
  imageUrl?: string;
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  imageUrl: () => "",
});

const file = ref();
// if (props.value) {
//   file.value = {
//     url: props.imageUrl,
//     percent: 100,
//     status: "done",
//   };
// }
// if (props.imageUrl && !props.value) {
//   file.value = {
//     url: props.imageUrl,
//     percent: 100,
//     status: "done",
//   };
// }
if (props.value) {
  file.value = {
    url: props.value,
    percent: 100,
    status: "done",
  };
}

// 自定义请求
const customRequest = async (option: any) => {
  const { onError, onSuccess, fileItem } = option;
  let url = "";
  const res: any = await uploadFileUsingPost({}, fileItem.file);
  if (res.data.code === 0 && res.data.data) {
    // const url = res.data.data;
    url = res.data.data;
    file.value = {
      name: fileItem.name,
      file: fileItem.file,
      url,
    };
    props.onChange?.(url);
    // // file.value.url = "";
    // if (props.imageUrl) {
    //   file.value.url = props.imageUrl;
    // }
    onSuccess();
    // console.log(file.value);
    // console.log("链接为" + url);
  } else {
    Message.error("上传失败，" + res.data.message || "");
    onError(new Error(res.data.message));
  }
  // file.value.url = "";
};
</script>
