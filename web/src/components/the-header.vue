<template>
    <a-layout-header class="header">
        <div >
            <img class="logo" src="../assets/logo.png" alt="wabi" />
        </div>
        <div class="logo_text">
            WaBi
        </div>

        <a class="login-menu" @click="showLoginModal" v-if="!user.id">
            <span>登录</span>
        </a>
        <a-popconfirm
                title="确认退出登录?"
                ok-text="是"
                cancel-text="否"
                @confirm="logout()"
                class="login-menu"
        >
            <a  class="login-menu" v-show="user.id">
                <span>退出登录</span>
            </a>
            <a class="login-menu" v-if="user.id">
                <span>您好:{{user.name}}</span>
            </a>
        </a-popconfirm>
        <a-menu
                v-model:selectedKeys="selectedKeys"
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px' }"
        >
            <a-menu-item key="1">
                <router-link to="/">首页</router-link>
            </a-menu-item>
            <a-menu-item key="2"  v-if="user.tokenAdmin">
                <router-link to="/admin/user">用户管理</router-link>
            </a-menu-item>
            <a-menu-item key="2"  v-if="user.tokenTeacher">
                <router-link to="/teacher/admin">账号管理</router-link>
            </a-menu-item>
            <a-menu-item key="2"  v-if="user.tokenStudent">
                <router-link to="/student/admin">账号管理</router-link>
            </a-menu-item>
            <a-menu-item key="3" v-if="user.tokenAdmin">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="4" v-if="user.tokenAdmin">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>
            <a-menu-item key="3" v-if="user.tokenTeacher">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="4"  v-if="user.tokenTeacher">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>

            <a-sub-menu key="sub1" v-if="user.tokenAdmin">
                <template #icon>
<!--                    <setting-outlined />-->
                </template>
                <template #title>账号管理</template>
                    <a-menu-item key="setting:1">
                        <router-link to="/admin/user">管理员</router-link>
                    </a-menu-item>
                    <a-menu-item key="setting:2">
                        <router-link to="/admin/teacher">教师</router-link>
                    </a-menu-item>
                    <a-menu-item key="setting:3">
                        <router-link to="/admin/student">学生</router-link>
                    </a-menu-item>
            </a-sub-menu>
            <a-menu-item key="5">
                <router-link to="/about">关于我们</router-link>
            </a-menu-item>

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
<!--                        <a-form-item-->
<!--                                label="权限码"-->
<!--                                :rules="[{ required: true, message: '请输入管理员权限码' }]"-->
<!--                                :style="isShowAdminLogin? {} : {display:'none'}"-->
<!--                        >-->
<!--                            <a-input-password v-model:value="loginUser.limitCode" type="password" placeholder="请输入管理员权限码">-->
<!--                                <template #prefix>-->
<!--                                    <LockOutlined class="site-form-item-icon" />-->
<!--                                </template>-->
<!--                            </a-input-password>-->
<!--                        </a-form-item>-->

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
                                label="登录名"
                                :rules="[{ required: true, message: 'Please input your password!' }]"
                                :style="isShowStudentLogin? {} : {display:'none'}"
                        >
                            <a-input v-model:value="loginUser.loginName"  placeholder="请输入学号">
                                <template #prefix>
                                    <user-outlined type="user" />
                                </template>
                            </a-input>
                        </a-form-item>
                        <a-form-item
                                label="密码"
                                :rules="[{ required: true, message: 'Please input your password!' }]"
                                :style="isShowStudentLogin? {} : {display:'none'}"
                        >
                            <a-input-password v-model:value="loginUser.password" type="password" placeholder="请输入密码">
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
                                <a-radio :value="1" @change="selectAdmin">管理员</a-radio>
                                <a-radio :value="2" @change="selectTeacher">教师端</a-radio>
                                <a-radio :value="3" @change="selectStudent">学生端</a-radio>
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
                loginName: "test",
                password: "123456"
            });
            const loginModalVisible = ref(false);
            const loginModalLoading = ref(false);
            const showLoginModal = () => {
                loginModalVisible.value = true;
            };
            const loginType = ref<number>(1);


            const isShowAdminLogin = ref(true);
            const isShowTeacherLogin = ref(false);
            const isShowStudentLogin = ref(false);
            //选择管理端
            const  selectAdmin= () => {
                isShowAdminLogin.value = true;
                isShowTeacherLogin.value = false;
                isShowStudentLogin.value = false;
                // message.success("选择管理端成功！");
            };
            //选择教师端
            const  selectTeacher= () => {
                isShowTeacherLogin.value = true;
                isShowAdminLogin.value = false;
                isShowStudentLogin.value = false;
                // message.success("选择教师端成功！");
            };
            //选择学生端
            const  selectStudent= () => {
                isShowStudentLogin.value = true;
                isShowTeacherLogin.value = false;
                isShowAdminLogin.value = false;
                // message.success("选择教师端成功！");
            };
            // 管理员登录
            const login = () => {
                    loginModalLoading.value = true;
                    loginUser.value.password = hexMd5(loginUser.value.password + KEY);
                if(isShowAdminLogin.value){
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
                }else {
                    if (isShowTeacherLogin.value){
                        axios.post('/teacher/login', loginUser.value).then((response) => {
                            loginModalLoading.value = false;
                            const data = response.data;
                            if (data.success) {
                                loginModalVisible.value = false;
                                message.success("登录成功！",1);
                                //触发setUser方法，把用户的信息传递过去
                                console.log("登录后:"+data.content.id);
                                // user1.value = data.content;
                                // user.value = data.content;
                                store.commit("setUser", data.content);
                            } else {
                                message.error(data.message);
                            }
                        });
                    }else{
                        axios.post('/student/login', loginUser.value).then((response) => {
                            loginModalLoading.value = false;
                            const data = response.data;
                            if (data.success) {
                                loginModalVisible.value = false;
                                message.success("登录成功！",1);
                                //触发setUser方法，把用户的信息传递过去
                                console.log("登录后:"+data.content.id);
                                // user1.value = data.content;
                                // user.value = data.content;
                                store.commit("setUser", data.content);
                            } else {
                                message.error(data.message);
                            }
                        });
                    }
                }
            };

            //退出登录
            const logout = () => {
                if(user.value.tokenAdmin){
                    axios.get('/user/logout/' + user.value.tokenAdmin).then((response) => {
                        const data = response.data;
                        if (data.success) {
                            message.success("退出登录成功！",1);
                            store.commit("setUser", {});
                        } else {
                            message.error(data.message);
                        }
                    });
                }else {
                    if(user.value.tokenTeacher){
                        axios.get('/user/logoutTeacher/' + user.value.tokenTeacher).then((response) => {
                            const data = response.data;
                            if (data.success) {
                                message.success("退出登录成功！");
                                store.commit("setUser", {});
                            } else {
                                message.error(data.message);
                            }
                        });
                    }else {
                        axios.get('/user/logoutStudent/' + user.value.tokenStudent).then((response) => {
                            const data = response.data;
                            if (data.success) {
                                message.success("退出登录成功！");
                                store.commit("setUser", {});
                            } else {
                                message.error(data.message);
                            }
                        });
                    }

                }
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
                isShowAdminLogin,
                selectStudent,
                isShowStudentLogin,

                selectedKeys: ref<string[]>(['']),

            }
        }
    });
</script>
<style>
    .logo {
        width: 50px;
        height: 45px;
        background: rgba(255, 255, 255, 0.2);
        margin: 16px 6px 7px 23px;
        float: left;
    }
    .logo_text{
        float: left;
        color: var(--wabi-font-color);
        font-size: 15px;
        font-family: revert;
        font-weight: 900;
        margin-right: 100px;
        line-height: 90px;
    }
    .login-menu {
        float: right;
        color: var(--wabi-font-color);
        padding-right: 10px;
        height: 45px;
        font-family: revert;
        line-height: 80px;
        font-size: 16px;
    }
    .ant-menu.ant-menu-dark, .ant-menu-dark .ant-menu-sub, .ant-menu.ant-menu-dark .ant-menu-sub {
        color: var(--wabi-font-color) !important;
    }
    .ant-layout-header {
        background: var(--wabi-domain-color) !important;
    }
    .ant-menu.ant-menu-dark, .ant-menu-dark .ant-menu-sub, .ant-menu.ant-menu-dark .ant-menu-sub {
        background: var(--wabi-domain-color) !important;
        height: 80px !important;
        line-height: 80px !important;
        font-size: 17px;
        font-family: inherit;
    }
    .ant-menu-item > span > a {
        color: var(--wabi-font-color) !important;
    }
    .ant-menu-overflow {
        /*justify-content: end;*/
    }
    .ant-layout-header {
        height: 80px !important;
    }
    aside{
        flex: 0 0 275px !important;
        max-width: 275px !important ;
        min-width: 275px !important;
        width: 275px !important;

    }
    ul{
        font-size: 15px !important;
    }
</style>