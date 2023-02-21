<template>
    <a-layout class="middle">
        <a-layout-content :style="{background:'#fff', padding: '24px', minHeight: '280px' }">
           <p>
               <a-form layout="inline" :model="param">
                   <a-form-item>
                       <a-input v-model:value="param.name" placeholder="名称">
                       </a-input>
                   </a-form-item>
                   <a-form-item>
                       <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
                           查询
                       </a-button>
                   </a-form-item>
                   <a-form-item>
                       <a-button type="primary" @click="add()">
                           新增
                       </a-button>
                   </a-form-item>
               </a-form>
           </p>
            <a-table
                    :columns="columns"
                    :loading="loading"
                    :data-source="categorys"
                    :pagination="pagination"
                    @change="handleTableChange"
                    :row-key="record => record.id"
            >
                <template #cover="{ text: cover }">
                    <img v-if="cover" :src="cover" alt="avatar" />
                </template>
                <template v-slot:action="{text,record}">
                    <a-space size="small">
                        <a-button type="primary" @click="edit(record)">
                            编辑
                        </a-button>
                        <a-button type="danger">
                            删除
                        </a-button>
                    </a-space>
                </template>
            </a-table>
        </a-layout-content>
    </a-layout>
    <a-modal
            title="分类表单"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >
        <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="名称">
                <a-input v-model:value="category.name" />
            </a-form-item>
            <a-form-item label="父分类">
                <a-input v-model:value="category.parent" />
            </a-form-item>
            <a-form-item label="顺序">
                <a-input v-model:value="category.sort" />
            </a-form-item>

        </a-form>
    </a-modal>
</template>
<script lang="ts">
    import { defineComponent, onMounted, ref } from 'vue';
    import axios from 'axios';
    import {message} from 'ant-design-vue';
    import {Tool} from "@/util/tool";

    export default defineComponent({
        name: 'AdminCategory',
        setup() {
            const param =ref();
            param.value={};
            const categorys = ref();
            const loading = ref(false);
            const pagination = ref({
                current: 1,
                pageSize: 10,
                total: 0
            });
            const columns = [
                {
                    title: '名称',
                    dataIndex: 'name',
                    // key:'name'
                },
                {
                    title: '父分类',
                    dataIndex: 'parent',
                    key:'parent',
                },
                {
                    title: '顺序',
                    dataIndex: 'sort'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: { customRender: 'action' }
                }
            ];
            /** 数据查询**/
            const handleQuery = (params:any) => {
                loading.value = true;
                axios.get("/category/list", {
                    params:{
                        page:params.page,
                        size:params.size,
                        name:param.value.name
                    }
                }).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if(data.success){
                    //data.content.list是一个数组，数组里每个元素都是一个对象，对象里有很多属性包括（id、name等等）。
                    categorys.value = data.content.list;
                    console.log(categorys.value);
                    // 重置分页按钮
                    pagination.value.current = params.page;
                    pagination.value.total = data.content.total;
                    }else{
                        message.error(data.message)
                    }
                });
            };
            /**
             * 表格点击页码时触发
             */
            const handleTableChange = (pagination:any) => {
                handleQuery({
                    page: pagination.current,
                    size: pagination.pageSize
                });
            };
            // -------- 表单 ---------
            /**
             * 数组，[100, 101]对应：前端开发 / Vue
             */
            const category = ref({});
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                console.log(category.value);
                modalLoading.value = true;
                axios.post("/category/save", category.value).then((response) => {
                    modalLoading.value=false;
                    console.log(category.value);
                    const data = response.data;//data=commonResp
                    if (data.success){
                        modalVisible.value=false;
                        //重新加载列表
                        handleQuery({
                            page:pagination.value.current,
                            size:pagination.value.pageSize
                        });
                    }else {
                        message.error(data.message);
                    }
                });
            };

            /**
             * 编辑
             */
            const edit = (record:any) => {
                modalVisible.value = true;
                category.value=Tool.copy(record);
                // console.log(record);
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;
                category.value={}
            };

            onMounted(() => {
                handleQuery({
                    page:1,
                    size:pagination.value.pageSize
                });
            });
            return {
                param,
                categorys,
                pagination,
                columns,
                loading,
                handleTableChange,
                handleQuery,

                edit,
                add,

                category,
                modalVisible,
                modalLoading,
                handleModalOk
            }
        }
    });
</script>
<style scoped>
    img {
        width: 50px;
        height: 50px;
    }
</style>