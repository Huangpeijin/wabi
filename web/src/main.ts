import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import headers from "./components/the-header.vue"
import footers from "./components/the-footer.vue"
import welcome from "./components/the-welcome.vue"
//把所有的图标库导入进来
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';
import {Tool} from "@/util/tool";
import './style.scss';

axios.defaults.baseURL=process.env.VUE_APP_SERVER;
/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
    console.log('请求参数：', config);
    //去store把token拿出来
    const token = store.state.user.token;
    if (Tool.isNotEmpty(token)) {
        config.headers.token = token;
        console.log("请求headers增加token:", token);
    }
    return config;
}, error => {
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    console.log('返回结果：', response);
    return response;
}, error => {
    console.log('返回错误：', error);
    return Promise.reject(error);
});
const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');
app.component('headers',headers);
app.component('footers',footers);
app.component('welcomes',welcome);
//全局使用图标
const  icons:any=Icons;
for(const i in  icons){
    app.component(i,icons[i]);
}

console.log('环境：',process.env.NODE_ENV);
console.log('服务端：',process.env.VUE_APP_SERVER);