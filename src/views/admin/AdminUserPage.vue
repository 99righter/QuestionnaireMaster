<template>
  <a-space direction="vertical" size="large" fill>
    <div class="AdminUserPage-formWapper">
      <a-form
        :model="formSearchUserParams"
        :style="{ marginTop: '10px' }"
        layout="inline"
        @submit="doSearch"
      >
        <a-form-item field="userName" label="用户名">
          <a-input
            v-model="formSearchUserParams.userName"
            placeholder="请输入用户名"
          />
        </a-form-item>
        <a-form-item field="userProfile" label="用户简介">
          <a-input
            v-model="formSearchUserParams.userProfile"
            placeholder="请输入用户简介"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100px">
            搜索
          </a-button>
        </a-form-item>
      </a-form>
    </div>
    <a-table
      row-key="name"
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
        showPageSize: true,
      }"
      @page-change="onPageChange"
      @pageSizeChange="onPageSizeChange"
    >
      <template #userAvatar="{ record }">
        <a-image :width="64" :src="record.userAvatar" />
      </template>
      <template #createTime="{ record }">
        {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #updateTime="{ record }">
        {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #optional="{ record }">
        <a-button status="normal" @click="loadEditUser(record)">编辑</a-button>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
      </template>
    </a-table>
  </a-space>
  <a-drawer
    :width="500"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title> 用户编辑</template>
    <div>
      <a-form :model="formEditUserParams" layout="horizontal" @submit="doEdit">
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
          <a>图片上传预留</a>
        </a-form-item>
        <a-form-item field="userRole" label="用户权限">
          <a-input
            v-model="formEditUserParams.userRole"
            :default-value="editUser.userRole"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100px"
            >提交</a-button
          >
        </a-form-item>
      </a-form>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import API from "@/api";
import {
  deleteUserUsingPost,
  getUserByIdUsingGet,
  listUserByPageUsingPost,
  updateUserUsingPost,
} from "@/api/userController";
import message from "@arco-design/web-vue/es/message";
import PictureUpload from "@/components/PictureUpload.vue";

// eslint-disable-next-line @typescript-eslint/no-var-requires
const dayjs = require("dayjs");
const selectedKeys = ref(["Jane Doe", "Alisa Ross"]);
const rowSelection = reactive({
  type: "checkbox",
  showCheckedAll: true,
  onlyCurrent: false,
});
/*
  type UserQueryRequest = {
    current?: number;
    id?: number;
    mpOpenId?: string;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    unionId?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };
 */
//定义来自搜索用户的信息
const formSearchUserParams = ref<API.UserQueryRequest>({});
//初始化搜索条件
const initSearchUserParams = {
  currentPage: 1,
  pageSize: 10,
};

const searchParams = ref<API.UserQueryRequest>({
  ...initSearchUserParams,
});

const dataList = ref<API.User[]>([]);
const total = ref<number>(0);
/**
 * 加载数据
 */
const lodData = async () => {
  if (!searchParams.value.userName || searchParams.value.userName === "") {
    console.log("加载数据时搜索条件为空");
  }
  console.log("加载数据的条件：" + searchParams.value.userName);
  const res = await listUserByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};
/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...initSearchUserParams,
    ...formSearchUserParams.value,
  };
};
/**
 * 当分页发生变化时
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};
/**
 * 当每页显示的项目数发生变化时
 */
const onPageSizeChange = (intPutPageSize: number) => {
  searchParams.value = {
    ...searchParams.value,
    pageSize: intPutPageSize,
  };
};
/**
 * 删除
 * @param record
 */
const doDelete = async (record: API.User) => {
  if (!record.id) {
    return;
  }

  const res = await deleteUserUsingPost({
    id: record.id,
  });
  if (res.data.code === 0) {
    lodData();
  } else {
    message.error("删除失败，" + res.data.message);
  }
};
/**
 * 监听searchParms变量，改变时会触发数据的加载
 */
watchEffect(() => {
  lodData();
});
/**
 * 抽屉是否打开
 */
const visible = ref(false);
/**
 * 打开抽屉
 */
const openDrawer = () => {
  visible.value = true;
};
/**
 * 确认关闭抽屉
 */
const handleOk = () => {
  doEdit();
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};
/**
 * 需要编辑的话首先需要获取用户的信息
 */

//初始化用户
const initEditUser = {
  userName: "",
};
//需要编辑的用户
const editUser = ref<API.User>({
  ...initEditUser,
});
//加载编辑用户
const loadEditUser = async (record: API.User) => {
  if (!record.id) {
    return;
  }
  const res = await getUserByIdUsingGet({
    id: record.id,
  });
  if (res.data.code === 0) {
    editUser.value = {
      ...res.data.data,
    };
    openDrawer();
  } else {
    message.error("获取用户信息失败" + res.data.message);
  }
};

//编辑用户输入的信息
const formEditUserParams = ref<API.UserUpdateRequest>({});
//将编辑用户的信息传给新用户
const EditUserToNewUser = () => {
  // formEditUserParams.value = {
  //   ...editUser.value,
  // };
  formEditUserParams.value.id = editUser.value.id;
  console.log(
    "将编辑用户的信息传给新用户" + formEditUserParams.value.userProfile
  );
};
const doEdit = async () => {
  EditUserToNewUser();
  const res = await updateUserUsingPost({ ...formEditUserParams.value });
  if (res.data.code === 0) {
    // handleOk();
    // lodData();
    console.log("编辑成功" + res.data.message);
  } else {
    message.error("编辑失败" + res.data.message);
  }
};
// 表格列配置
const columns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "账号",
    dataIndex: "userAccount",
  },
  {
    title: "用户名",
    dataIndex: "userName",
  },
  {
    title: "用户头像",
    dataIndex: "userAvatar",
    slotName: "userAvatar",
  },
  {
    title: "用户简介",
    dataIndex: "userProfile",
  },
  {
    title: "权限",
    dataIndex: "userRole",
  },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  {
    title: "更新时间",
    dataIndex: "updateTime",
    slotName: "updateTime",
  },
  {
    title: "操作",
    slotName: "optional",
  },
];
</script>
<style scoped>
.AdminUserPage-formWapper {
  display: flex;
  justify-content: center;
}
</style>
