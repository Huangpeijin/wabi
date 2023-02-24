import { createStore } from 'vuex'

const store = createStore({
  //定义全局变量
  state: {
    user:{}
  },
  //对变量进行同步操作
  mutations: {
    setUser (state, user) {
      state.user = user;
    }
  },
  //对变量进行异步操作
  actions: {
  },
  modules: {
  }
})
export default store;
