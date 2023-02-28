<template xmlns:v-slot="http://www.w3.org/1999/XSL/Transform">
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
                <template v-slot:category="{ text, record }">
                    <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
                </template>
                <template v-slot:action="{text,record}">
                    <a-space size="small">
                        <router-link :to="'/admin/doc?ebookId=' + record.id">
                            <a-button type="primary">
                                文档管理
                            </a-button>
                        </router-link>
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
                <a-upload
                        v-model:file-list="fileList"
                        name="avatar"
                        list-type="picture-card"
                        class="avatar-uploader"
                        :show-upload-list="false"
                        @change="handleChange"
                        action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                >
                    <img v-if="imageUrl" :src="imageUrl" alt="avatar" />
                    <div v-else>
                        <loading-outlined v-if="loading"></loading-outlined>
                        <plus-outlined v-else></plus-outlined>
                        <div class="ant-upload-text">Upload</div>
                    </div>
                </a-upload>
            </a-form-item>
            <a-form-item label="封面">
                <a-input v-model:value="ebook.cover" />
            </a-form-item>
            <a-form-item label="名称">
                <a-input v-model:value="ebook.name" />
            </a-form-item>
            <a-form-item label="分类">
                <a-cascader
                        placeholder="请选择分类"
                        v-model:value="categoryIds"
                        :field-names="{ label: 'name', value: 'id', children: 'children' }"
                        :options="level1"
                />
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
    import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue';
    import type { UploadChangeParam, UploadProps } from 'ant-design-vue';

    function getBase64(img:any, callback: (base64Url: string) => void) {
        const reader = new FileReader();
        reader.addEventListener('load', () => callback(reader.result as string));
        reader.readAsDataURL(img);
    }

    export default defineComponent({
        name: 'AdminEbook',
        components: {
            LoadingOutlined,
            PlusOutlined,
        },
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
                    title: '分类',
                    slots:{customRender:'category'}
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
             * 数组[100,101]对应：前端开发/Vue
             */
            const categoryIds=ref();
            const ebook = ref();//绑定表单的ebook
            const modalVisible = ref(false);
            const modalLoading = ref(false);

            /**
             * 编辑
             */
            const edit = (record:any) => {
                modalVisible.value = true;

                console.log(categoryIds.value);
                ebook.value=Tool.copy(record);
                // getCategoryName(ebook.value.category1Id)
                categoryIds.value = [getCategoryName(ebook.value.category1Id), getCategoryName(ebook.value.category2Id)]
                console.log(categoryIds.value);
                console.log(categoryIds.value[0]);

            };
            /**
             * 编辑完，点击ok的时候
             */
            const handleModalOk = () => {
                modalLoading.value = true;
                console.log(categoryIds.value[0]);
                console.log(getCategoryName(ebook.value.category1Id));
                ebook.value.cover=imageUrl.value;
                //待优化
                if (getCategoryName(ebook.value.category1Id)!=categoryIds.value[0]){
                    ebook.value.category1Id = categoryIds.value[0];
                    ebook.value.category2Id = categoryIds.value[1];
                }
                // if (getCategoryName(ebook.value.category1Id)==categoryIds.value[0]){
                //     console.log(ebook.value.category1Id);
                // }else {
                //     ebook.value.category1Id = categoryIds.value[0];
                //     ebook.value.category2Id = categoryIds.value[1];
                // }
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


            const level1 =  ref();
            let categorys:any;
            /**
             * 查询所有分类
             **/
            const handleQueryCategory = () => {
                // loading.value = true; loading变量是给电子书表格用的，加不加都可以
                axios.get("/category/all").then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if (data.success) {
                        categorys = data.content;
                        // console.log("原始数组：", categorys);

                        level1.value = [];
                        level1.value = Tool.array2Tree(categorys, 0);
                        // console.log("树形结构：", level1.value);

                        // 加载完分类后，再加载电子书，否则如果分类树加载很慢，则电子书渲染会报错
                        handleQuery({
                            page: 1,
                            size: pagination.value.pageSize,
                        });
                    } else {
                        message.error(data.message);
                    }
                });
            };
            /** 刚开始数据查询的时候**/
            const handleQuery = (params:any) => {
                loading.value = true;
                // // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
                // ebooks.value = [];
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
            const getCategoryName = (cid: number) => {
                let result = "";
               //它自己就调用10次
                categorys.forEach((item: any) => {
                    if (item.id == cid) {
                        // return item.name; // 注意，这里直接return不起作用
                        result = item.name;
                    }

                });
                return result;
            };
            /**
             * 图片上传
             **/
            const imageUrl = ref<string>('');
            const fileList = ref([]);
            const handleChange = (info: UploadChangeParam) => {
                console.log(info);
                console.log(fileList.value);
                if (info.file.status === 'uploading') {
                    loading.value = true;
                    return;
                }
                if (info.file.status === 'done') {
                    // Get this url from response in real world.
                    getBase64(info.file.originFileObj, (base64Url: string) => {
                        imageUrl.value = base64Url;
                        console.log(imageUrl.value);
                        loading.value = false;
                    });
                }
                if (info.file.status === 'error') {
                    loading.value = false;
                    message.error('upload error');
                }
            };
            onMounted(() => {
                handleQueryCategory();
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
                getCategoryName,

                categoryIds,
                level1,

                edit,
                add,
                // value: ref<string[]>(['zhejiang', 'hangzhou', 'xihu']),
                //表单
                ebook,
                modalVisible,
                modalLoading,
                handleModalOk,
                handleChange,
                imageUrl,
                fileList,

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