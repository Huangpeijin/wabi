<template>
    <headers></headers>
    <a-layout class="middle">
<!--        {{html}}-->
        <a-layout-content :style="{background:'#fff', padding: '24px', minHeight: '280px' }">
            <a-row :gutter="24">
                <a-col :span="5">
                    <p>
                        <a-form layout="inline" :model="param">
<!--                            <a-form-item>-->
<!--                                <a-button type="primary" @click="handleQuery()">-->
<!--                                    查询-->
<!--                                </a-button>-->
<!--                            </a-form-item>-->
                            <a-form-item>
                                <a-button type="primary" @click="add()" shape="round">
                                    新增
                                </a-button>
                            </a-form-item>
                        </a-form>
                    </p>
                    <a-table
                            v-if="level1.length > 0"
                            :columns="columns"
                            :loading="loading"
                            :data-source="level1"
                            :row-key="record => record.id"
                            :pagination="false"
                            size="small"
                            :defaultExpandAllRows="true"
                    >
                        <!--                        <template #name="{ text, record }">-->
                        <!--                            {{record.sort}} {{text}}-->
                        <!--                        </template>-->
                        <template v-slot:action="{text,record}">
                            <a-space size="small">
                                <a-button type="primary" @click="edit(record)" size="small" shape="round">
                                    编辑
                                </a-button>
                                <a-popconfirm
                                        :title="`将删除 ${deleteNames.join(', ')}文档，是否删除？`"
                                        ok-text="是"
                                        cancel-text="否"
                                        @confirm="handleDelete(record.id)"
                                >
                                    <a-button type="danger" size="small" @click="getDeNames(level1,record.id)" shape="round">
                                        删除
                                    </a-button>
                                </a-popconfirm>
                            </a-space>
                        </template>
                    </a-table>
                </a-col>
                <a-col :span="19">
                    <p>
                        <a-form layout="inline" :model="param">
                            <a-form-item>
                                <a-button type="primary" @click="handleSave()" shape="round">
                                    保存
                                </a-button>
                            </a-form-item>
                        </a-form>
                    </p>
                    <a-form :model="doc" layout="vertical">
                        <a-form-item>
                            <a-input v-model:value="doc.name" placeholder="名称"/>
                        </a-form-item>
                        <a-form-item>
                            <a-tree-select
                                    v-model:value="doc.parent"
                                    style="width: 100%"
                                    :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                                    :tree-data="treeSelectData"
                                    placeholder="请选择父文档"
                                    tree-default-expand-all
                                    :field-names="{label: 'name', key: 'id', value: 'id'}"
                            >
                            </a-tree-select>
                        </a-form-item>
                        <a-form-item>
                            <a-select
                                    v-model:value="doc.disabled"
                                    @focus="focus"
                                    @change="handleChange"
                                    placeholder="请选择文档权限"
                            >
                                <a-select-option value="false">公开</a-select-option>
                                <a-select-option value="true">私密</a-select-option>
                            </a-select>
                        </a-form-item>
                        <a-form-item>
                            <a-input v-model:value="doc.sort" placeholder="顺序"/>
                        </a-form-item>
                        <a-form-item>
                            <div style="border: 1px solid #ccc">
                                <MdEditor v-model="text"
                                          @onUploadImg="handleUploadImage"
                                          @onSave="onSave"
                                          @onHtmlChanged="onHtmlChanged"
                                          placeholder="请输入内容......"
                                />
                            </div>
                        </a-form-item>
                    </a-form>
                </a-col>
            </a-row>
        </a-layout-content>
    </a-layout>
    <!--    <a-modal
                title="文档表单"
                v-model:visible="modalVisible"
                :confirm-loading="modalLoading"
                @ok="handleModalOk"
        >
        </a-modal>-->
</template>
<script lang="ts">
    import {defineComponent, onMounted, ref,computed,reactive} from 'vue';
    import axios from 'axios';
    import {message, Modal} from "ant-design-vue";
    import {Tool} from "@/util/tool";
    import {useRoute} from "vue-router";
    import store from "../../store";
    import '@wangeditor/editor/dist/css/style.css' // 引入 css
    import { onBeforeUnmount, shallowRef} from 'vue'
    import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
    import MdEditor from 'md-editor-v3';
    import 'md-editor-v3/lib/style.css';

    export default defineComponent({
        name: 'AdminDoc',
        components: { Editor, Toolbar, MdEditor},
        setup() {
            // 编辑器实例，必须用 shallowRef
            const editorRef = shallowRef()
            // 内容 HTML
            const valueHtml = ref('<p>hello</p>')
            const route =useRoute()
            const param =ref();
            param.value={};
            const docins = ref();
            const docs = ref();
            const loading = ref(false);
            // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
            const treeSelectData = ref();
            const user = computed(() => store.state.user);
            treeSelectData.value = [];
            const columns = [
                {
                    title: '名称',
                    dataIndex: 'name',
                    slots: { customRender: 'name' }
                    // key:'name'
                },
                {
                    title: 'Action',
                    key: 'action',
                    slots: { customRender: 'action' }
                }
            ];
            /**
             * 一级文档树，children属性就是二级文档
             * [{
             *   id: "",
             *   name: "",
             *   children: [{
             *     id: "",
             *     name: "",
             *   }]
             * }]
             */
            const text = ref('');
            function handleUploadImage(files:any, callback:any){
                console.log(files)
                for(let i in files){
                    const formData = new FormData();
                    formData.append('file', files[i]);
                    axios.post("/doc/uploadimg",formData).then(
                        response => {
                            console.log(response.data)
                            const myArray = reactive([response.data])
                            console.log(myArray)
                            callback(myArray);
                        },
                        error => {
                            console.log('请求失败了',error.message)
                        }
                    )
                }
            }

            const html = ref("");
            const onHtmlChanged = (newHtml:any) => {
                html.value = newHtml;
                console.log(html.value)
            };

            const level1 = ref(); // 一级文档树，children属性就是二级文档
            level1.value=[]//初始为空
            const level = ref();
            level.value=[]
            /** 数据查询**/
            const handleQuery = () => {
                loading.value = true;
                // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
                level1.value = [];
                level.value = [];
                // axios.get("/docin/all/"+ route.query.ebookId).then((response) => {
                //     loading.value = false;
                //     const data = response.data;
                //     if(data.success){
                //         docins.value = data.content;
                //         level1.value = [];
                //         //将扁平数组转为树行结构
                //         level1.value = Tool.array2Tree(docins.value, 0);
                //         // 父文档下拉框初始化，相当于点击新增
                //         treeSelectData.value = Tool.copy(level1.value) || [];
                //         // 为选择树添加一个"无"
                //         treeSelectData.value.unshift({id: 0, name: '无'});
                //         console.log("treeSelectData.value：", treeSelectData.value);
                //
                //     }else{
                //         message.error(data.message)
                //     }
                // });
                axios.get("/doc/all/"+ route.query.ebookId).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if(data.success){
                        docs.value = data.content;
                        level1.value = [];
                        //将扁平数组转为树行结构
                        level1.value = Tool.array2Tree(docs.value, 0);
                        // 父文档下拉框初始化
                        treeSelectData.value = Tool.copy(level1.value) || [];
                        // 为选择树添加一个"无"
                        treeSelectData.value.unshift({id: 0, name: '无'});
                    }else{
                        message.error(data.message)
                    }
                });
            };
            // -------- 表单 ---------
            /**
             * 数组，[100, 101]对应：前端开发 / Vue
             */
            const doc = ref();
            doc.value={};
            doc.value={ebookId: route.query.ebookId};
            const modalVisible = ref(false);
            const modalLoading = ref(false);
            // const editor = new E('#content');//定义富文本
            // editor.config.zIndex=0;//让富文本不要遮住下拉框，因为富文本的Index设置的很高，默认是500，这个z-index就是覆盖的层级的大小
            /**
             * 富文档保存
             */
            // const onSave = (v:any, h:any) => {
            //     console.log(v);
            //
            //     h.then((html:any) => {
            //         console.log(html);
            //         doc.value.content = html;
            //         console.log(doc.value.content);
            //         axios.post("/doc/save/"+doc.value.content).then((response) => {
            //             modalLoading.value=false;
            //             // const data = response.data;//data=commonResp
            //             // if (!data.success){
            //             //     // message.success("保存成功！");
            //             //     //重新加载列表
            //             //     // handleQuery();
            //             //     // message.error(data.message);
            //             // }
            //         });
            //     });
            // };
            const handleSave = () => {
                modalLoading.value = true;
                // doc.value={ebookId: route.query.ebookId};
                console.log(doc.value.ebookId)
                doc.value.content = html.value //获取文本html的内容
                doc.value.textvalue = text.value//获取文本md的内容
                axios.post("/doc/save", doc.value).then((response) => {
                    modalLoading.value=false;
                    const data = response.data;//data=commonResp
                    console.log(typeof (data.id));
                    if (data.success){
                        message.success("保存成功！");
                        //重新加载列表
                        handleQuery();
                        axios.post("/docin/save", data).then((response) => {
                            // modalLoading.value=false;
                            // const data = response.data;//data=commonResp
                            //     if (data.success){
                            //         message.success("保存成功！");
                            //         //重新加载列表
                            //         handleQuery();
                            //     }else {
                            //         message.error(data.message);
                            //     }
                        });
                    }else {
                        message.error(data.message);
                    }
                });
            //     axios.post("/docin/save", doc.value).then((response) => {
            //         modalLoading.value=false;
            //         console.log(doc.value);
            //         const data = response.data;//data=commonResp
            //         if (data.success){
            //             message.success("保存成功！");
            //             //重新加载列表
            //             handleQuery();
            //         }else {
            //             message.error(data.message);
            //         }
            //     });
            };
            /**
             * 将某节点及其子孙节点全部置为disabled
             */
            const setDisable = (treeSelectData: any, id: any) => {
                // console.log(treeSelectData, id);
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("disabled", node);
                        // 将目标节点设置为disabled
                        node.disabled = true;

                        // 遍历所有子节点，将所有子节点全部都加上disabled
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            for (let j = 0; j < children.length; j++) {
                                setDisable(children, children[j].id)
                            }
                        }
                    } else {
                        // 如果当前节点不是目标节点，则到其子节点再找找看。
                        const children = node.children;
                        if (Tool.isNotEmpty(children)) {
                            setDisable(children, id);
                        }
                    }
                }
            };


            /**
             * 内容查询
             **/
            const handleQueryContent = () => {
                axios.get("/doc/find-content/" + doc.value.id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        text.value = data.textvalue;
                    } else {
                        message.error(data.message);
                    }
                });
            };
            /**
             * 编辑
             */
            const edit = (record:any) => {
                text.value="" //清空富文本框
                modalVisible.value = true;
                doc.value=Tool.copy(record);
                doc.value.disabled = doc.value.disabled.toString();
                handleQueryContent();
                treeSelectData.value = Tool.copy(level1.value);
                setDisable(treeSelectData.value, record.id);
                treeSelectData.value.unshift({id: 0, name: '无'});
            };

            /**
             * 新增
             */
            const add = () => {
                text.value="";//清空富文本框
                doc.value={};
                doc.value.ebookId = route.query.ebookId;
                modalVisible.value = true;
                treeSelectData.value = Tool.copy(level1.value) || [];
                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});
            };
            /**
             * 文档点击删除按钮，在确认框里点击“确认”的时候就会调用该函数
             */
            const handleDelete = (idin: number) => {
                const idins = getDeIds(level1.value, idin);
                const ids = idins;
                axios.all([
                    axios.delete("/docin/delete/" + idins.join(",")),
                    axios.delete("/doc/delete/" + ids.join(","))
                ]).then(axios.spread((response1, response2) => {
                    const data1 = response1.data;
                    const data2 = response2.data;
                    if (data1.success && data2.success) {
                        handleQuery();
                    } else {
                        message.error(data1.message || data2.message);
                    }
                })).catch((error) => {
                    message.error(error.message);
                });
            };
            /**
             * 在文档数组种获取id对应的name值
             */
            const getIdName = (source:any,id: number) => {
                let result = "";
                //它自己就调用10次
                source.value.forEach((item: any) => {
                    if (item.id == id) {
                        // return item.name; // 注意，这里直接return不起作用
                        result = item.name;
                    }

                });
                return result;
            };
            /**
             * 在文档数组种获取id对应的name值
             */
            const getNameId = (source:any,name:string) => {
                let result = "";
                //它自己就调用10次
                source.value.forEach((item: any) => {
                    if (item.name == name) {
                        // return item.name; // 注意，这里直接return不起作用
                        result = item.id;
                    }

                });
                return result;
            };
            /**
             * 查找整根树枝,用来删除整颗树，把文档的id存起来
             */


            const getDeIds = (treeSelectData: any, id: any) => {
                const ids: Array<string> = [];
                // const getDeleteIds = (treeSelectData: any, id: any) => {
                //     for (let i = 0; i < treeSelectData.length; i++) {
                //         const node = treeSelectData[i];
                //         if (node.id === id) {
                //             ids.push(id);
                //             const children = node.children;
                //             if (Tool.isNotEmpty(children)) {
                //                 for (let j = 0; j < children.length; j++) {
                //                     getDeleteIds(children, children[j].id)
                //                 }
                //             }
                //         } else {
                //             const children = node.children;
                //             if (Tool.isNotEmpty(children)) {
                //                 getDeleteIds(children, id);
                //             }
                //         }
                //     }
                // };
                const getDeleteIds = (treeSelectData: any, id: any) => {
                    const queue = [...treeSelectData];
                    while (queue.length > 0) {
                        const node = queue.shift();
                        if (node.id === id) {
                            ids.push(id);
                            if (node.children) {
                                queue.push(...node.children);
                            }
                        } else if (node.children) {
                            queue.push(...node.children);
                        }
                    }
                };
                getDeleteIds(treeSelectData, id);
                return ids;
            }
            /**
             * 获取文档的名字，包括它的子文档的名字
             */
            const deleteNames: Array<string> = [];
            const getDeNames = (treeSelectData: any, id: any) => {
                //每次调用getNames函数会先把之前的数值清空
                deleteNames.length = 0;
                //定义getDeleteIds函数
                const getDeleteIds = (treeSelectData: any, id: any) => {
                    // 遍历数组，即遍历某一层节点
                    for (let i = 0; i < treeSelectData.length; i++) {
                        const node = treeSelectData[i];
                        if (node.id === id) {
                            // 如果当前节点就是目标节点，将目标id的name值放入结果集deleteNames中
                            deleteNames.push(node.name);
                            // 遍历它的所有子节点
                            const children = node.children;
                            //如果它的孩子不为空，则会递归调用去寻找它孩子的name值
                            if (Tool.isNotEmpty(children)) {
                                for (let j = 0; j < children.length; j++) {
                                    getDeleteIds(children, children[j].id)
                                }
                            }
                        } else {
                            // 如果当前节点不是目标节点，则到其子节点再找找看。
                            const children = node.children;
                            if (Tool.isNotEmpty(children)) {
                                getDeleteIds(children, id);
                            }
                        }
                    }
                };

                //调用getDeleteIds函数
                getDeleteIds(treeSelectData, id);
            }

            // ----------------富文本预览--------------
            const drawerVisible = ref(false);
            const previewHtml = ref();
            const handlePreviewContent = () => {
                // const html = editor.txt.html();
                // previewHtml.value = html;
                drawerVisible.value = true;
            };
            const onDrawerClose = () => {
                drawerVisible.value = false;
            };
            onMounted(() => {
                handleQuery();
                // editor.create();//创建富文本
                // setTimeout(() => {
                //     valueHtml.value = '<p>模拟 Ajax 异步设置内容</p>'
                // }, 1500)
            });
            const toolbarConfig = {}
            const editorConfig = { placeholder: '请输入内容...' }

            // 组件销毁时，也及时销毁编辑器
            onBeforeUnmount(() => {
                const editor = editorRef.value
                if (editor == null) return
                editor.destroy()
            })

            // // 编辑器回调函数
            const handleCreated = (editor:any) => {
                console.log('created', editor);
                editorRef.value = editor; // 记录 editor 实例，重要！
            };


            return {
                param,
                columns,
                loading,
                handleQuery,
                handleDelete,
                level1,

                edit,
                add,

                doc,
                modalVisible,
                modalLoading,
                handleSave,

                treeSelectData,


                drawerVisible,
                previewHtml,
                handlePreviewContent,
                onDrawerClose,

                user,

                editorRef,
                valueHtml,
                mode: 'default', // 或 'simple'
                toolbarConfig,
                editorConfig,
                handleCreated,

                getDeIds,
                getIdName,
                deleteNames,
                getDeNames,

                handleUploadImage,
                text,
                onSave,
                onHtmlChanged,
                html,
            }

        }
    });
</script>


<style>
    /*img {*/
    /*    width: 50px;*/
    /*    height: 50px;*/
    /*}*/
    .ant-table-container table > thead > tr:first-child th:last-child {
        width: 30%;
    }
</style>