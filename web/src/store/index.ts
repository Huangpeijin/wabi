import { createStore } from 'vuex'

declare let SessionStorage: any;
const USER = "USER";
const store = createStore({
  //定义全局变量
  state: {
    //刷新的时候，会加载store，就会去缓存里获取值
    user: SessionStorage.get(USER) || {}
  },
  //对变量进行同步操作
  mutations: {
    setUser (state, user) {
      //登录成功会，除了给state赋值，还会把信息放入缓存中
      state.user = user;
      SessionStorage.set(USER, user);
    }
  },
  //对变量进行异步操作
  actions: {
  },
  modules: {
  }
})
export default store;
