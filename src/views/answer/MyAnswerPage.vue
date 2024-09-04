<template>
  <a-form
    :model="formSearchParams"
    :style="{ marginBottom: '20px' }"
    layout="inline"
    @submit="doSearch"
  >
    <a-form-item field="resultName" label="结果名称">
      <a-input
        v-model="formSearchParams.resultName"
        placeholder="请输入结果名称"
        allow-clear
      />
    </a-form-item>
    <a-form-item field="resultDesc" label="结果描述">
      <a-input
        v-model="formSearchParams.resultDesc"
        placeholder="请输入结果描述"
        allow-clear
      />
    </a-form-item>
    <a-form-item field="appId" label="应用 id">
      <a-input-number
        v-model="formSearchParams.appId"
        placeholder="请输入应用 id"
        allow-clear
      />
    </a-form-item>
    <a-form-item>
      <a-button type="primary" html-type="submit" style="width: 100px">
        搜索
      </a-button>
    </a-form-item>
  </a-form>
  <a-table
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
    @page-size-change="onPageSizeChange"
  >
    <template #resultPicture="{ record }">
      <a-image width="64" :src="record.resultPicture" />
    </template>
    <template #appType="{ record }">
      {{ APP_TYPE_MAP[record.appType] }}
    </template>
    <template #scoringStrategy="{ record }">
      {{ APP_SCORING_STRATEGY_MAP[record.scoringStrategy] }}
    </template>
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #optional="{ record }">
      <a-space>
        <div class="showAnswerPage">
          <a-button @click="handleClick(record.id)">查看结果</a-button>
        </div>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
      </a-space>
    </template>
  </a-table>
  <a-modal
    width="800px"
    v-model:visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    :hide-cancel="true"
    :mask-style="{
      backgroundColor: 'rgba(5,5,5,0.5)',
    }"
  >
    <template #title>答题详情</template>
    <AnswerResult :id="inputId" />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import {
  deleteUserAnswerUsingPost,
  listMyUserAnswerVoByPageUsingPost,
} from "@/api/userAnswerController";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { useLoginUserStore } from "@/store/userStore";
import AnswerResult from "@/views/answer/AnswerResult.vue";

/**
 * 获取当前登录的用户id
 */
const loginUserStore = useLoginUserStore();
const loginUserId = loginUserStore.loginUser?.id;
const formSearchParams = ref<API.UserAnswerQueryRequest>({});

// 初始化搜索条件（不应该被修改）
const initSearchParams = {
  current: 1,
  pageSize: 10,
};

const searchParams = ref<API.UserAnswerQueryRequest>({
  userId: loginUserId,
  ...initSearchParams,
});
const dataList = ref<API.UserAnswerVO[]>([]);
const total = ref<number>(0);

/**
 * 加载数据
 */
const loadData = async () => {
  const res = await listMyUserAnswerVoByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
const visible = ref(false);
const inputId = ref();
const handleClick = (idInput: number) => {
  visible.value = true;
  inputId.value = idInput;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};
/**
 * 执行搜索
 */
const doSearch = () => {
  searchParams.value = {
    ...initSearchParams,
    ...formSearchParams.value,
  };
};

/**
 * 当分页变化时，改变搜索条件，触发数据加载
 * @param page
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
const doDelete = async (record: API.UserAnswer) => {
  if (!record.id) {
    return;
  }

  const res = await deleteUserAnswerUsingPost({
    id: record.id,
  });
  if (res.data.code === 0) {
    loadData();
  } else {
    message.error("删除失败，" + res.data.message);
  }
};

/**
 * 监听 searchParams 变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  loadData();
});

// 表格列配置
const columns = [
  {
    title: "id",
    dataIndex: "id",
  },
  {
    title: "选项",
    dataIndex: "choices",
  },
  {
    title: "结果 id",
    dataIndex: "resultId",
  },
  {
    title: "名称",
    dataIndex: "resultName",
  },
  {
    title: "描述",
    dataIndex: "resultDesc",
  },
  {
    title: "图片",
    dataIndex: "resultPicture",
    slotName: "resultPicture",
  },
  {
    title: "得分",
    dataIndex: "resultScore",
  },
  {
    title: "应用 id",
    dataIndex: "appId",
  },
  {
    title: "应用类型",
    dataIndex: "appType",
    slotName: "appType",
  },
  {
    title: "评分策略",
    dataIndex: "scoringStrategy",
    slotName: "scoringStrategy",
  },
  // {
  //   title: "用户 id",
  //   dataIndex: "userId",
  // },
  {
    title: "创建时间",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  // {
  //   title: "更新时间",
  //   dataIndex: "updateTime",
  //   slotName: "updateTime",
  // },
  {
    title: "操作",
    slotName: "optional",
  },
];
</script>
<style scoped></style>
