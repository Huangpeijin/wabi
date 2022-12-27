<template>
    <a-layout>
      <a-layout-header class="header">
        <div class="logo" />
        <a-menu
          theme="dark"
          mode="horizontal"
          v-model:selectedKeys="selectedKeys1"
          :style="{ lineHeight: '64px' }"
        >
          <a-menu-item key="1">电子书管理</a-menu-item>
          <a-menu-item key="2">文档管理</a-menu-item>
          <a-menu-item key="3">分类管理</a-menu-item>
        </a-menu>
      </a-layout-header>
        <a-layout-sider class="sider" width="200" style="background: #fff">
          <a-menu
            mode="inline"
            v-model:selectedKeys="selectedKeys2"
            v-model:openKeys="openKeys"
            :style="{ height: '100%', borderRight: 0 }"
          >
            <a-sub-menu key="sub1">
              <template #title>
                <span>
                  <user-outlined />
                  计算机
                </span>
              </template>
              <a-menu-item key="1">Java</a-menu-item>
              <a-menu-item key="2">SprinBoot</a-menu-item>
              <a-menu-item key="3">Vue</a-menu-item>
              <a-menu-item key="4">VueCLI</a-menu-item>
            </a-sub-menu>
            <a-sub-menu key="sub2">
              <template #title>
                <span>
                  <laptop-outlined />
                  高等数学
                </span>
              </template>
              <a-menu-item key="5">option5</a-menu-item>
              <a-menu-item key="6">option6</a-menu-item>
              <a-menu-item key="7">option7</a-menu-item>
              <a-menu-item key="8">option8</a-menu-item>
            </a-sub-menu>
            <a-sub-menu key="sub3">
              <template #title>
                <span>
                  <notification-outlined />
                  脑科学
                </span>
              </template>
              <a-menu-item key="9">option9</a-menu-item>
              <a-menu-item key="10">option10</a-menu-item>
              <a-menu-item key="11">option11</a-menu-item>
              <a-menu-item key="12">option12</a-menu-item>
            </a-sub-menu>
          </a-menu>
        </a-layout-sider>
        <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
            Content
        </a-layout-content>
      <a-layout-footer style="text-align: center">
       Ant Design ©2018 Created by Ant UED
      </a-layout-footer>
    </a-layout>
</template>

<style>
 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 24px;
  background: rgba(255, 255, 255, 0.3);
}
.sider{
  float: left;
}
.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}
</style>