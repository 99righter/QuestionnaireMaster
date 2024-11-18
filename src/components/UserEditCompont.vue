<script setup lang="ts">
import { ref, defineExpose, withDefaults, defineProps } from "vue";
import PictureUpload from "@/components/PictureUpload.vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { updateUserUsingPost } from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";

interface Props {
  editUser: API.UserUpdateRequest;
  refreshData: () => void;
}

const props = withDefaults(defineProps<Props>(), {
  editUser: () => {
    return {};
  },
});
const visible = ref(false);
const formEditUserParams = ref<API.UserUpdateRequest>({
  ...props.editUser,
});
const handleClick = () => {
  visible.value = true;
};
const afterEdit = ref<API.LoginUserVO>({});
const handleBeforeOk = async () => {
  visible.value = false;
};
const resultImageUrl = ref<string>("");
const getUserAvatarUrl = (url: any) => {
  resultImageUrl.value = url;
};
const editAvatarUrl = async () => {
  if (resultImageUrl.value !== "" && resultImageUrl.value !== undefined) {
    formEditUserParams.value.userAvatar = resultImageUrl.value;
  } else {
    formEditUserParams.value.userAvatar = props.editUser.userAvatar;
  }
};
const doEdit = async () => {
  editAvatarUrl();
  // console.log("userAvatarUrl.value：" + userAvatarUrl.value);
  const res = await updateUserUsingPost({
    ...formEditUserParams.value,
  });
  if (res.data.code === 0) {
    // handleOk();
    //提交后需要注意将userAvatarUrl.value置空
    // console.log("编辑成功" + res.data.message);
    props.refreshData();
    visible.value = false;
  } else {
    message.error("编辑失败" + res.data.message);
  }
  formEditUserParams.value.userAvatar = "";
};
defineExpose({
  handleClick,
});
</script>

<template>
  <a-modal
    v-model:visible="visible"
    @before-ok="handleBeforeOk"
    hide-cancel
    ok-text="取消"
  >
    <template #title> 用户编辑</template>
    <div>
      <a-form :model="formEditUserParams" layout="horizontal">
        <a-form-item field="userName" label="用户名">
          <a-input
            v-model="formEditUserParams.userName"
            :default-value="editUser.userName"
          />
        </a-form-item>
        <a-form-item field="userProfile" label="用户简介">
          <a-input
            v-model="formEditUserParams.userProfile"
            :default-value="editUser.userProfile"
          />
        </a-form-item>
        <a-form-item field="userAvatar" label="头像">
          <!--          todo-->
          <PictureUpload
            :onChange="getUserAvatarUrl"
            :imageUrl="formEditUserParams.userAvatar"
          />
        </a-form-item>
        <a-form-item field="userRole" label="用户权限">
          <a-input
            v-model="formEditUserParams.userRole"
            :default-value="editUser.userRole"
          />
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            style="width: 100px"
            @click="doEdit"
            >提交
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </a-modal>
</template>

<style scoped></style>
