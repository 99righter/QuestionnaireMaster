<template>
  <h2>当前应用id:{{ id }}</h2>
  <div class="inputScoringResultModal">
    <a-button @click="handleClick">创建一条新的评分结果</a-button>
    <a-modal
      v-model:visible="visible"
      title="评分结果"
      @before-ok="handleBeforeOk"
      hide-cancel
      ok-text="取消"
    >
      <EditOrAddScoringResult/>
      <!--        {{ formScori ngResult.resultPicture }}-->
    </a-modal>
  </div>
  <a-form
    :model="formSearchParams"
    :style="{ marginBottom: '20px', marginTop: '20px' }"
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
    <template #createTime="{ record }">
      {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #updateTime="{ record }">
      {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
    </template>
    <template #optional="{ record }">
      <a-space>
        <a-button status="danger" @click="doDelete(record)">删除</a-button>
        <a-button status="normal" @click="doUpdateScoringResult(record)"
          >修改
        </a-button>
      </a-space>
    </template>
  </a-table>
  <a-modal
    v-model:visible="updateVisable"
    title="评分结果"
    @before-ok="handleBeforeOk"
    hide-cancel
    ok-text="取消"
  >
    <EditOrAddScoringResult :doScoringResultUpdate="updateScoringResultParam" />
    <!--        {{ formScoringResult.resultPicture }}-->
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watchEffect, withDefaults, defineProps, defineExpose } from "vue";
import {
  deleteScoringResultUsingPost,
  listScoringResultByPageUsingPost,
} from "@/api/scoringResultController";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import EditOrAddScoringResult from "@/components/EditOrAddScoringResult.vue";

interface Props {
  id: string;
  // doUpdate: (scoringResult: API.ScoringResultVO) => void;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});
const formSearchParams = ref<API.ScoringResultQueryRequest>({});

// 初始化搜索条件（不应该被修改）
const initSearchParams = {
  current: 1,
  pageSize: 10,
  sortField: "createTime",
  sortOrder: "descend",
};
//更新变量
const updateVisable = ref(false);
/**
 * 定义传递参数变量
 */
const updateScoringResultParam = ref<API.ScoringResultVO>({
  id: 0,
  resultDesc: "",
  resultName: "",
  resultPicture: "",
});
/**
 * 点击修改按钮事件
 */
const doUpdateScoringResult = (record: API.ScoringResult) => {
  updateVisable.value = true;
  updateId.value = record.id;
  imageUrl.value = record.resultPicture as any;
  updateScoringResultParam.value = record as any;
};
const visible = ref(false);
/*const doUpdate = (record: API.ScoringResult) => {
  // console.log("执行更新方法");
  updateVisable.value = true;
  updateId.value = record.id;
  imageUrl.value = record.resultPicture as any;
  formScoringResult.value = {
    ...record,
  };
  console.log(
    "formScoringResult.value:" + JSON.stringify(formScoringResult.value)
  );
};*/
const intiScoringResult = {
  resultDesc: "",
  resultName: "",
  resultPicture: "",
};
const formScoringResult = ref({
  resultDesc: "",
  resultName: "",
  resultPicture: "",
} as API.ScoringResultAddRequest);
const handleClick = () => {
  //如果是添加的话就需要初始化formScoringResult;
  formScoringResult.value = {};
  visible.value = true;
};
const updateId = ref<any>();
const imageUrl = ref("");
const handleBeforeOk = async () => {
  console.log("执行了打开弹窗方法");
  formScoringResult.value = {
    ...intiScoringResult,
  };
  console.log("是否为更新方法" + updateId.value);
  visible.value = false;
};
const searchParams = ref<API.ScoringResultQueryRequest>({
  ...initSearchParams,
});
const dataList = ref<API.ScoringResult[]>([]);
const total = ref<number>(0);

/**
 * 加载数据
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const params = {
    ...searchParams.value,
    appId: props.id as any,
  };
  const res = await listScoringResultByPageUsingPost(params);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败，" + res.data.message);
  }
};
defineExpose({
  loadData,
});

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
const doDelete = async (record: API.ScoringResult) => {
  if (!record.id) {
    return;
  }

  const res = await deleteScoringResultUsingPost({
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
    title: "结果属性",
    dataIndex: "resultProp",
  },
  {
    title: "评分范围",
    dataIndex: "resultScoreRange",
  },
  {
    title: "应用 id",
    dataIndex: "appId",
  },
  {
    title: "用户 id",
    dataIndex: "userId",
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
