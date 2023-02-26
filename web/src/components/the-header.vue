<template>
    <a-layout-header class="header">
        <div class="logo" />
        <a-menu
                v-model:selectedKeys="selectedKeys1"
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px' }"
        >
            <a-menu-item key="/">
                <router-link to="/">首页</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/user"  :style="user.tokenAdmin? {} : {display:'none'}">
                <router-link to="/admin/user">用户管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/ebook" :style="user.tokenAdmin? {} : {display:'none'}">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/category" :style="user.tokenAdmin? {} : {display:'none'}">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>

            <a-menu-item key="/admin/ebook" :style="user.tokenTeacher? {} : {display:'none'}">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/category" :style="user.tokenTeacher? {} : {display:'none'}">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>
            <a-menu-item key="/about">
                <router-link to="/about">关于我们</router-link>
            </a-menu-item>
            <a-popconfirm
                    title="确认退出登录?"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="logout()"
            >
                <a class="login-menu" v-show="user.id">
                    <span>退出登录</span>
                </a>
            </a-popconfirm>
            <a class="login-menu" v-show="user.id">
                <span>您好:{{user.name}}</span>
            </a>

            <a class="login-menu" @click="showLoginModal" v-show="!user.id">
                <span>登录</span>
            </a>
        </a-menu>
                <a-modal
                        title="登录"
                        v-model:visible="loginModalVisible"
                        :confirm-loading="loginModalLoading"
                        @ok="login"
                >
                    <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
                        <a-form-item
                                label="登录名"
                                :rules="[{ required: true, message: 'Please input your password!' }]"
                                :style="isShowAdminLogin? {} : {display:'none'}"
                        >
                            <a-input v-model:value="loginUser.loginName"  placeholder="请输入管理员用户名">
                                <template #prefix>
                                    <user-outlined type="user" />
                                </template>
                            </a-input>
                        </a-form-item>
                        <a-form-item
                                label="密码"
                                :rules="[{ required: true, message: 'Please input your password!' }]"
                                :style="isShowAdminLogin? {} : {display:'none'}"
                        >
                            <a-input-password v-model:value="loginUser.password" type="password" placeholder="请输入管理员密码">
                                 <template #prefix>
                                     <LockOutlined class="site-form-item-icon" />
                                 </template>
                            </a-input-password>
                        </a-form-item>


                        <a-form-item
                                label="登录名"
                                :rules="[{ required: true, message: 'Please input your password!' }]"
                                :style="isShowTeacherLogin? {} : {display:'none'}"
                        >
                            <a-input v-model:value="loginUser.loginName"  placeholder="请输入教师用户名">
                                <template #prefix>
                                    <user-outlined type="user" />
                                </template>
                            </a-input>
                        </a-form-item>
                        <a-form-item
                                label="密码"
                                :rules="[{ required: true, message: 'Please input your password!' }]"
                                :style="isShowTeacherLogin? {} : {display:'none'}"
                        >
                            <a-input-password v-model:value="loginUser.password" type="password" placeholder="请输入教师密码">
                                <template #prefix>
                                    <LockOutlined class="site-form-item-icon" />
                                </template>
                            </a-input-password>
                        </a-form-item>
                        <a-form-item
                                label="账号类型"
                                :rules="[{ required: true, message: 'Please input your password!' }]"
                        >
                            <a-radio-group v-model:value="loginType">
                                <a-radio value="1" @change="selectAdmin">管理员</a-radio>
                                <a-radio value="2" @change="selectTeacher">教师端</a-radio>
                                <a-radio value="3">学生端</a-radio>
                            </a-radio-group>
                        </a-form-item>
                    </a-form>
                </a-modal>
    </a-layout-header>
</template>

<script lang="ts">
    import { ref, defineComponent,computed } from 'vue';
    import axios from 'axios';
    import {message} from 'ant-design-vue';
    import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
    import store from "../store";
    declare let hexMd5: any;
    declare let KEY: any;

    export default defineComponent({
        name: 'the-header',
        components: {
            UserOutlined,
            LockOutlined,
        },
        setup () {
            const user = computed(() => store.state.user);
            // 用来登录
            const loginUser = ref({
                loginName: "",
                password: ""
            });
            const loginModalVisible = ref(false);
            const loginModalLoading = ref(false);
            const showLoginModal = () => {
                loginModalVisible.value = true;
            };
            const loginType = ref<number>(1);

            // 管理员登录
            const login = () => {
                // console.log("开始登录");
                loginModalLoading.value = true;
                loginUser.value.password = hexMd5(loginUser.value.password + KEY);
                axios.post('/user/login', loginUser.value).then((response) => {
                    loginModalLoading.value = false;
                    const data = response.data;
                    if (data.success) {
                        loginModalVisible.value = false;
                        message.success("登录成功！");
                        //触发setUser方法，把用户的信息传递过去
                        console.log("登录后:"+data.content.id);
                        // user1.value = data.content;
                        // user.value = data.content;
                        store.commit("setUser", data.content);
                    } else {
                        message.error(data.message);
                    }
                });
            };
            // 教师登陆登录
            const loginTeacher = () => {
                loginModalLoading.value = true;
                loginUser.value.password = hexMd5(loginUser.value.password + KEY);
                axios.post('/user/loginTeacher', loginUser.value).then((response) => {
                    loginModalLoading.value = false;
                    const data = response.data;
                    if (data.success) {
                        loginModalVisible.value = false;
                        message.success("登录成功！");
                        //触发setUser方法，把用户的信息传递过去
                        console.log("登录后:"+data.content.id);
                        // user1.value = data.content;
                        // user.value = data.content;
                        store.commit("setUser", data.content);
                    } else {
                        message.error(data.message);
                    }
                });
            };

            //退出登录
            const logout = () => {
                console.log("退出登录开始");
                axios.get('/user/logout/' + user.value.tokenAdmin).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        message.success("退出登录成功！");
                        store.commit("setUser", {});
                    } else {
                        message.error(data.message);
                    }
                });
            };
            //选择教师端
            const isShowTeacherLogin = ref(false);
            const  selectTeacher= () => {
                isShowTeacherLogin.value = true;
                isShowAdminLogin.value = false;
                // message.success("选择教师端成功！");
            };
            //选择管理端
            const isShowAdminLogin = ref(true);
            const  selectAdmin= () => {
                isShowTeacherLogin.value = false;
                isShowAdminLogin.value = true;
                // message.success("选择管理端成功！");
            };
            return {
                loginModalVisible,
                loginModalLoading,
                showLoginModal,
                loginUser,
                login,
                user,
                loginType,
                // user1,
                logout,

                selectTeacher,
                isShowTeacherLogin,
                selectAdmin,
                isShowAdminLogin
            }
        }
    });
</script>
<style>
    .logo {
        width: 120px;
        height: 31px;
        background: rgba(255, 255, 255, 0.2);
        margin: 16px 28px 16px 28px;
        float: left;
        color: white;
        font-size: 18px;
    }
    .login-menu {
        float: right;
        color: white;
        padding-right: 10px;
    }
</style>