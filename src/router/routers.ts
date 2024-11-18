import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import UserLayout from "@/layouts/UserLayout.vue";
import UserRegister from "@/views/user/UserRegister.vue";
import UserLogin from "@/views/user/UserLogin.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import AdminUserPage from "@/views/admin/AdminUserPage.vue";
import AdminAppPage from "@/views/admin/AdminAppPage.vue";
import AdminQuestionPage from "@/views/admin/AdminQuestionPage.vue";
import AdminUserAnswerPage from "@/views/admin/AdminUserAnswerPage.vue";
import AdminScoreResultPage from "@/views/admin/AdminScoreResultPage.vue";
import AppDetail from "@/views/app/AppDetail.vue";
import AddAppPage from "@/views/app/AddAppPage.vue";
import AddQuestionPage from "@/views/question/AddQuestionPage.vue";
import DoAnswerPage from "@/views/answer/DoAnswerPage.vue";
import AnswerResult from "@/views/answer/AnswerResult.vue";
import MyAnswerPage from "@/views/answer/MyAnswerPage.vue";
import UserCenter from "@/views/user/UserCenter.vue";
import AdminBasePage from "@/views/admin/AdminBasePage.vue";
import SocringResultOfApp from "@/views/app/SocringResultOfApp.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "主页",
    component: HomeView,
  },
  //设置一个不会在主页中显示的路由
  {
    path: "/user",
    name: "user",
    component: HomeView,
    meta: {
      hidInMenu: true,
    },
  },
  {
    path: "/add/app",
    name: "创建应用",
    component: AddAppPage,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/MyAnswer",
    name: "我的回答",
    component: MyAnswerPage,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/app/:id",
    name: "应用详情",
    component: AppDetail,
    meta: {
      hidInMenu: true,
    },
  },
  {
    path: "/admin",
    name: "管理员",
    meta: {
      access: ACCESS_ENUM.ADMIN,
      showFather: true,
    },
    children: [
      {
        path: "/admin/statistics",
        name: "应用统计",
        component: AdminBasePage,
        meta: {
          access: ACCESS_ENUM.ADMIN,
          showFather: false,
          hidInMenu: true,
        },
      },
      {
        path: "/admin/user",
        name: "用户管理",
        component: AdminUserPage,
        meta: {
          access: ACCESS_ENUM.ADMIN,
          showFather: false,
        },
      },
      {
        path: "/admin/app",
        name: "应用管理",
        component: AdminAppPage,
        meta: {
          access: ACCESS_ENUM.ADMIN,
          showFather: false,
        },
      },
      {
        path: "/admin/question",
        name: "题目管理",
        component: AdminQuestionPage,
        meta: {
          access: ACCESS_ENUM.ADMIN,
          showFather: false,
        },
      },
      {
        path: "/admin/user_answer",
        name: "用户答案管理",
        component: AdminUserAnswerPage,
        meta: {
          access: ACCESS_ENUM.ADMIN,
          showFather: false,
        },
      },
      {
        path: "/admin/score_result",
        name: "评分管理",
        component: AdminScoreResultPage,
        meta: {
          access: ACCESS_ENUM.ADMIN,
          showFather: false,
        },
      },
    ],
  },
  // {
  //   path: "/admin/statistics",
  //   name: "应用统计",
  //   component: AdminBasePage,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //     showFather: false,
  //   },
  // },
  // {
  //   path: "/admin/user",
  //   name: "用户管理",
  //   component: AdminUserPage,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //     showFather: false,
  //   },
  // },
  // {
  //   path: "/admin/app",
  //   name: "应用管理",
  //   component: AdminAppPage,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //     showFather: false,
  //   },
  // },
  // {
  //   path: "/admin/question",
  //   name: "题目管理",
  //   component: AdminQuestionPage,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //     showFather: false,
  //   },
  // },
  // {
  //   path: "/admin/user_answer",
  //   name: "用户答案管理",
  //   component: AdminUserAnswerPage,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //     showFather: false,
  //   },
  // },
  // {
  //   path: "/admin/score_result",
  //   name: "评分管理",
  //   component: AdminScoreResultPage,
  //   meta: {
  //     access: ACCESS_ENUM.ADMIN,
  //     showFather: false,
  //   },
  // },
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    meta: {
      hidInMenu: true,
    },
    children: [
      {
        path: "/user/login",
        name: "登录",
        component: UserLogin,
      },
      {
        path: "/user/register",
        name: "注册",
        component: UserRegister,
      },
    ],
  },
  {
    path: "/app/detail/:id",
    name: "应用详情",
    component: AppDetail,
    props: true,
    meta: {
      hidInMenu: true,
    },
  },
  {
    path: "/answer/do/:appId",
    name: "答题界面",
    component: DoAnswerPage,
    props: true,
    meta: {
      hidInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/user/center",
    name: "用户中心",
    component: UserCenter,
    meta: {
      hidInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/answer/result/:id",
    name: "结果界面",
    component: AnswerResult,
    props: true,
    meta: {
      hidInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/add/question/:appId",
    name: "设置题目",
    component: AddQuestionPage,
    props: true,
    meta: {
      hidInMenu: true,
    },
  },
  {
    path: "/add/scoringResult/:id",
    name: "设置评分规则",
    component: SocringResultOfApp,
    props: true,
    meta: {
      hidInMenu: true,
    },
  },
  {
    path: "/update/app/:appId",
    name: "修改应用",
    component: AddAppPage,
    props: true,
    meta: {
      hidInMenu: true,
    },
  },
];
