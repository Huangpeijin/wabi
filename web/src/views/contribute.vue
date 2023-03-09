<template>
  <headers></headers>
  <div style="background-color: #ececec; padding: 20px">
     <a-row :gutter="24">
        <a-col :span="8">
      <a-card hoverable style="width: 300px">
    <template #cover>
      <img alt="example" src="../assets/logo.png" />
    </template>
    <a-card-meta title="WaBi计算机教学知识云平台">
      <template #description>联系邮箱：Kim475@163.com</template>
    </a-card-meta>
  </a-card>
    </a-col>
        <a-col :span="8">
         <a-card hoverable style="width: 300px">
           <template #cover>
             <img alt="example" src="../assets/logo.png" />
           </template>
           <a-card-meta title="Europe Street beat">
             <template #description>www.instagram.com</template>
           </a-card-meta>
         </a-card>
       </a-col>
        <a-col :span="8">
         <a-card hoverable style="width: 300px">
           <template #cover>
             <img alt="example" src="../assets/logo.png" />
           </template>
           <a-card-meta title="Europe Street beat">
             <template #description>www.instagram.com</template>
           </a-card-meta>
         </a-card>
       </a-col>
     </a-row>
  </div>
  <footers></footers>
</template>
<style scoped>
  .ant-card-bordered {
    /*border: 1px solid #f0f0f0;*/
    border-radius: 20px;
  }
</style>


