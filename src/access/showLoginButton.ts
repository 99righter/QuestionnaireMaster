const showLoginButton = (loginUser: any) => {
  //如果用户未登录
  if (!loginUser) {
    return false;
  }
  return true;
};
export default showLoginButton;
