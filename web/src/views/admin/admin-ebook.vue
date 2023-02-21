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
                       <a-button type="primary" @click="add()" size="large">
                           新增
                       </a-button>
                   </a-form-item>
               </a-form>
           </p>
            <a-table
                    :columns="columns"
                    :loading="loading"
                    :data-source="ebooks"
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
            title="电子书表单"
            v-model:visible="modalVisible"
            :confirm-loading="modalLoading"
            @ok="handleModalOk"
    >
        <a-form :model="ebook" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
            <a-form-item label="封面">
                <a-input v-model:value="ebook.cover" />
            </a-form-item>
            <a-form-item label="名称">
                <a-input v-model:value="ebook.name" />
            </a-form-item>
            <a-form-item label="分类一">
                <a-input v-model:value="ebook.category1Id" />
            </a-form-item>
            <a-form-item label="分类二">
                <a-input v-model:value="ebook.category2Id" />
            </a-form-item>
            <a-form-item label="描述">
                <a-input v-model:value="ebook.description" type="textarea" />
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
        name: 'AdminEbook',
        setup() {
            const param =ref();
            param.value={};
            const ebooks = ref();
            const loading = ref(false);
            const pagination = ref({
                current: 1,
                pageSize: 10,
                total: 0
            });
            const columns = [
                {
                    title: '封面',
                    dataIndex: 'cover',
                    key:'cover',
                    slots: { customRender: 'cover' }
                },
                {
                    title: '名称',
                    dataIndex: 'name',
                    key:'name'
                },
                {
                    title: '分类1',
                    dataIndex: 'category1Id',
                    key:'category1Id',
                },
                {
                    title: '分类2',
                    dataIndex: 'category2Id',
                    key:'category2Id',
                },
                {
                    title: '文档数',
                    dataIndex: 'docCount',
                    key:'docCount'
                },
                {
                    title: '阅读数',
                    dataIndex: 'viewCount',
                    key:'viewCount'
                },
                {
                    title: '点赞数',
                    dataIndex: 'voteCount',
                    key:'voteCount'
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
                axios.get("/ebook/list", {
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
                    ebooks.value = data.content.list;
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
            const ebook = ref({});//绑定表单的ebook
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            const handleModalOk = () => {
                modalLoading.value = true;
                //传入的ebook.value的值是前端数据（表单上的值）
                axios.post("/ebook/save", ebook.value).then((response) => {
                    const data = response.data;//data=commonResp
                    if (data.success){
                        modalLoading.value=false;
                        modalVisible.value=false;
                        //调用handleQuery函数，并传入一个对象参数，重新加载列表
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
                ebook.value=Tool.copy(record);
            };

            /**
             * 新增
             */
            const add = () => {
                modalVisible.value = true;
                //将ebook的值清空并给它赋一个空对象
                ebook.value={}
            };
            /**
             * 删除
             */
            const handleDelete = (id:number) => {
                axios.delete("/ebook/delete/"+id).then((response) => {
                    const data = response.data;//data=commonResp
                    if (data.success){
                        //调用handleQuery函数，并传入一个对象参数，重新加载列表
                        handleQuery({
                            page:pagination.value.current,
                            size:pagination.value.pageSize
                        });
                    }else {
                        message.error(data.message);
                    }
                });
            };

            onMounted(() => {
                handleQuery({
                    page:1,
                    size:pagination.value.pageSize
                });
            });
            //这些东西需要给html调用，所以需要return出去
            return {
                //表格
                param,
                ebooks,
                pagination,
                columns,
                loading,
                handleTableChange,
                handleQuery,
                handleDelete,

                edit,
                add,

                //表单
                ebook,
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