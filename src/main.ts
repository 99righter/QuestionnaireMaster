import "@arco-design/web-vue/dist/arco.css";
import { createApp } from "vue";
import { createPinia } from "pinia";
import "@/access";

import ArcoVue from "@arco-design/web-vue";
import App from "./App.vue";
import router from "./router";
import { useLoginUserStore } from "@/store/userStore";

const pinia = createPinia();

createApp(App).use(ArcoVue).use(router).use(pinia).mount("#app");
