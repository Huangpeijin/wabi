<template>
    <a-layout class="middle">
        <a-layout-content :style="{background:'#fff', padding: '24px', minHeight: '280px' }">
           <p>
               <a-form layout="inline" :model="param">
<!--                   <a-form-item>-->
<!--                       <a-input v-model:value="param.name" placeholder="名称">-->
<!--                       </a-input>-->
<!--                   </a-form-item>-->
                   <a-form-item>
                       <a-button type="primary" @click="handleQuery()">
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
                    :data-source="level1"
                    :row-key="record => record.id"
                    :pagination="false"
            >
                <template #cover="{ text: cover }">
                    <img v-if="cover" :src="cover" alt="avatar" />
                </template>
                <template v-slot:action="{text,record}">
                    <a-space size="small">
                        <a-button type="primary" @click="edit(record)">
                            编辑
                        </a-button>
                        <a-popconfirm
                                title="删除后不可恢复，确认删除?"
                                ok-text="是"
                                cancel-text="否"
                                @confirm="handleDelete(record.id)"
                        >
                            <a-button type="danger">
                                删除
                            </a-button>
                        </a-popconfirm>
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
                <a-select v-model:value="category.parent">
                    <a-select-option :value="0">
                        无
                    </a-select-option>
                    <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
                        {{c.name}}
                    </a-select-option>
                </a-select>
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
            /**
             * 一级分类树，children属性就是二级分类
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const level1 = ref(); // 一级分类树，children属性就是二级分类
            /** 数据查询**/
            const handleQuery = () => {
                loading.value = true;
                axios.get("/category/all").then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if(data.success){
                        //data.content.list是一个数组，数组里每个元素都是一个对象，对象里有很多属性包括（id、name等等）。
                        categorys.value = data.content;
                        console.log("原始数组：", categorys.value);

                        level1.value = [];
                        level1.value = Tool.array2Tree(categorys.value, 0);
                        console.log("树形结构：", level1);
                    }else{
                        message.error(data.message)
                    }
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
                        handleQuery();
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
            /**
             * 删除
             */
            const handleDelete = (id:number) => {
                axios.delete("/category/delete/"+id).then((response) => {
                    const data = response.data;//data=commonResp
                    if (data.success){
                        //调用handleQuery函数，并传入一个对象参数，重新加载列表
                        handleQuery();
                    }else {
                        message.error(data.message);
                    }
                });
            };
            onMounted(() => {
                handleQuery();
            });
            return {
                param,
                // categorys,
                columns,
                loading,
                handleQuery,
                handleDelete,
                level1,

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