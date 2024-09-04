/**
 * 全集校验核心文件
 * 编写逻辑
 * 1.首先判断当前页面是否需要登录权限，如果不需要，直接放行
 * 2.当前页面需要登陆权限
 *  2.1如果当前用户未登录则跳转到登录界面
 *  2.2如果当前用户以登录，则判断用户的权限是否满足要求，否则则跳转到无权限界面
 */
import router from "@/router";
import { useLoginUserStore } from "@/store/userStore";
import AccessEnum from "@/access/accessEnum";
import checkcAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  //先获取当前用户登录信息
  const loginUserStore = useLoginUserStore();
  let catchLoginUser = loginUserStore.loginUser;
  if (!catchLoginUser || !catchLoginUser.userRole) {
    await loginUserStore.fetchLoginUser();
    catchLoginUser = loginUserStore.loginUser;
  }
  //获取待跳转页面是否需要权限
  const needAccess = (to.meta?.access as string) ?? AccessEnum.NOT_LOGIN;
  //如果要跳转到页面需要登陆
  if (needAccess !== AccessEnum.NOT_LOGIN) {
    //如果用户未登录，则跳转到登陆界面
    if (
      !catchLoginUser ||
      !catchLoginUser.userRole ||
      catchLoginUser.userRole === AccessEnum.NOT_LOGIN
    ) {
      next(`/user/login?redirect=${to.fullPath}`);
    }
    //如果用户已经登陆,需要判断用户权限是否足够，如果不足，跳转到401页面
    if (!checkcAccess(catchLoginUser, needAccess)) {
      next("/noAuth");
      return;
    }
  }
  next();
});
