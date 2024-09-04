import ACCESS_ENUM from "@/access/accessEnum";

const checkcAccess = (loginUser: any, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  //获取当前用户的权限
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  //如果当前页面不需要权限
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }
  //如果需要用户登录才能访问
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false;
    }
  }
  //如果需要管理员权限
  if (needAccess === ACCESS_ENUM.ADMIN) {
    //那么不是管理员权限则不能访问
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false;
    }
  }
  return true;
};

//将该函数导出使得可以直接作为方法调用
export default checkcAccess;
