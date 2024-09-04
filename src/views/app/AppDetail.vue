<script setup lang="ts">
import { computed, defineProps, ref, watchEffect, withDefaults } from "vue";
import { useRouter } from "vue-router";
import API from "@/api";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "@/store/userStore";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";

/**
 * 定义一个接口类型，里面的数据为app，类型为AppVO
 */
interface Props {
  id: string;
}

/**
 * 定义一个初始化函数，如果没有提供值的话则为默认空值
 */
const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});
//获取当前用户的登录态
const loginUserStore = useLoginUserStore();
let loginUserId = loginUserStore.loginUser?.id;
/**
 * 判断当前应用是否为本人创建
 */
const isMine = computed(() => {
  return loginUserId && loginUserId === data.value?.userId;
});
/**
 * 根据Appid获取应用
 */
const data = ref<API.AppVO>({});

/**
 * 获取应用
 */
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id as any,
  });
  if (res.data.code === 0) {
    data.value = res.data.data as any;
  } else {
    message.error("获取应用详情失败," + res.data.message);
  }
};
/**
 * 监听变量
 */
watchEffect(() => {
  loadData();
});
</script>

<template>
  <div class="appDetail">
    <a-card>
      <a-row>
        <a-col flex="auto" class="app_descInfo">
          <h2>{{ data.appName }}</h2>
          <p>{{ data.appDesc }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              作者：
              <div :style="{ display: 'flex', alignItems: 'center' }">
                <a-avatar
                  :size="24"
                  :image-url="data.user?.userAvatar"
                  :style="{ marginRight: '8px' }"
                />
                <a-typography-text
                  >{{ data.user?.userName ?? "无名" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>
          <p>
            创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </p>
          <a-space size="large">
            <a-button type="primary" :href="`/answer/do/${id}`"
              >开始答题</a-button
            >
            <a-button>分享应用</a-button>
            <a-button v-if="isMine" :href="`/add/question/${id}`"
              >设置题目
            </a-button>
            <a-button v-if="isMine" :href="`/add/scoringResult/${id}`"
              >设置评分规范
            </a-button>
            <a-button v-if="isMine" :href="`/add/app/${id}`">修改应用</a-button>
          </a-space>
        </a-col>
        <a-col flex="340px">
          <a-image width="100%" :src="data.appIcon" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<style scoped>
.app_descInfo > * {
  margin-bottom: 20px;
}
</style>
