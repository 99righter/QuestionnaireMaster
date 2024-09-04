import { defineStore } from "pinia";
import { ref } from "vue";
import { getLoginUserUsingGet } from "@/api/userController";
import AccessEnum from "@/access/accessEnum";

// 你可以任意命名 `defineStore()` 的返回值，但最好使用 store 的名字，同时以 `use` 开头且以 `Store` 结尾。
// (比如 `useUserStore`，`useCartStore`，`useProductStore`)
// 第一个参数是你的应用中 Store 的唯一 ID。
export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: "未登录",
  });

  // 其他配置...
  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet();
    //获取当前登录的用户
    // console.log("res:" + res.data.data);
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data;
    } else {
      //哪怕没有登录过用户，也会把用户的状态设置为not——login
      loginUser.value = { userRole: AccessEnum.NOT_LOGIN };
    }
  }

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  return { loginUser, setLoginUser, fetchLoginUser };
});
