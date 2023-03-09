<template>
  <headers></headers>
  <div style="background-color: #ececec; padding: 20px">
     <a-row :gutter="24">
        <a-col :span="8">
      <a-card hoverable style="width: 400px">
    <template #cover>
      <img alt="example" src="../assets/logo.png" />
    </template>
    <a-card-meta title="WaBi计算机教学知识云平台">
      <template #description>
        <p>技术栈：Springboot+Vue3+TS+Sass</p>
        <p>项目链接：https://kxs.cmds.run/</p>
        <p>Github：https://github.com/5th-Youth-Training-OOT/kxs-ui</p>
        <p>目前开发成员：1人</p>
        <p>联系邮箱：Kim475@163.com</p>
      </template>
    </a-card-meta>
  </a-card>
    </a-col>
        <a-col :span="8">
         <a-card hoverable style="width: 400px">
           <template #cover>
             <img alt="example" src="../assets/kxslogo.png" />
           </template>
           <a-card-meta title="基于Vue3的Kxs-UI组件库">
             <template #description>
               <p>技术栈：Vue3+TS+Vite+Sass</p>
               <p>项目链接：https://kxs.cmds.run/</p>
               <p>Github：https://github.com/5th-Youth-Training-OOT/kxs-ui</p>
               <p>目前开发成员：5人</p>
               <p>联系邮箱：Kim475@163.com</p>
             </template>
           </a-card-meta>
         </a-card>
       </a-col>
<!--        <a-col :span="8">-->
<!--         <a-card hoverable style="width: 400px">-->
<!--           <template #cover>-->
<!--             <img alt="example" src="../assets/logo.png" />-->
<!--           </template>-->
<!--           <a-card-meta title="Europe Street beat">-->
<!--             <template #description>www.instagram.com</template>-->
<!--           </a-card-meta>-->
<!--         </a-card>-->
<!--       </a-col>-->
     </a-row>
  </div>
  <footers></footers>
</template>
<style scoped>
  .ant-card-bordered {
    /*border: 1px solid #f0f0f0;*/
    border-radius: 20px;
  }
  .ant-card-cover img {
    height: 350px;
    padding: 20px;
    /*border-radius: 2px 2px 0 0;*/
  }
</style>


