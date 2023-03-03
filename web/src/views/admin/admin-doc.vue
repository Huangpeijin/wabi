<template>
    <headers></headers>
    <a-layout class="middle">
        <a-layout-content :style="{background:'#fff', padding: '24px', minHeight: '280px' }">
            <a-row :gutter="24">
                <a-col :span="8">
                    <p>
                        <a-form layout="inline" :model="param">
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
                                <a-button type="primary" @click="edit(record)" size="small">
                                    编辑
                                </a-button>
                                <a-popconfirm
                                        title="删除后不可恢复，确认删除?"
                                        ok-text="是"
                                        cancel-text="否"
                                        @confirm="handleDelete(record.id)"
                                >
                                    <a-button type="danger" size="small">
                                        删除
                                    </a-button>
                                </a-popconfirm>
                            </a-space>
                        </template>
                    </a-table>
                </a-col>
                <a-col :span="16">
                    <p>
                        <a-form layout="inline" :model="param">
                            <a-form-item>
                                <a-button type="primary" @click="handleSave()">
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
                                    ref="select"
                                    v-model:value="doc.disabled"
                                    @focus="focus"
                                    @change="handleChange"
                                    placeholder="请选择文档权限"
                            >
                                <a-select-option value="false">公开</a-select-option>
                                <a-select-option value="true">仅学生</a-select-option>
                            </a-select>
                        </a-form-item>
                        <a-form-item>
                            <a-input v-model:value="doc.sort" placeholder="顺序"/>
                        </a-form-item>
                        <a-form-item>
                            <a-button type="primary" @click="handlePreviewContent()">
                                <EyeOutlined /> 内容预览
                            </a-button>
                        </a-form-item>
                        <a-form-item>
                            <div id="content"></div>
                        </a-form-item>
                    </a-form>
                </a-col>
            </a-row>
            <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
                <div class="wangeditor" :innerHTML="previewHtml"></div>
            </a-drawer>
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
    import {createVNode, defineComponent, onMounted, ref,computed} from 'vue';
    import axios from 'axios';
    import {message, Modal} from "ant-design-vue";
    import {Tool} from "@/util/tool";
    import {useRoute} from "vue-router";
    import {ExclamationCircleOutlined} from "@ant-design/icons-vue";
    import E from 'wangeditor'
    import store from "../../store";


    export default defineComponent({
        name: 'AdminDoc',
        setup() {
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
                axios.get("/docin/all/"+ route.query.ebookId).then((response) => {
                    loading.value = false;
                    const data = response.data;
                    if(data.success){
                        //data.content.list是一个数组，数组里每个元素都是一个对象，对象里有很多属性包括（id、name等等）。
                        docins.value = data.content;
                        level1.value = [];
                        //将扁平数组转为树行结构
                        level1.value = Tool.array2Tree(docins.value, 0);
                        // 父文档下拉框初始化，相当于点击新增
                        treeSelectData.value = Tool.copy(level1.value) || [];
                        // 为选择树添加一个"无"
                        treeSelectData.value.unshift({id: 0, name: '无'});
                        console.log("treeSelectData.value：", treeSelectData.value);

                    }else{
                        message.error(data.message)
                    }
                });
                axios.get("/doc/all/"+ route.query.ebookId).then((response) => {
                    const data = response.data;
                    if(data.success){
                        docs.value = data.content;
                        level.value = [];
                        level.value = Tool.array2Tree(docs.value, 0);
                        // // console.log("树形结构：", level1);
                        //
                        // // 父文档下拉框初始化，相当于点击新增
                        // treeSelectData.value = Tool.copy(level1.value) || [];
                        // // 为选择树添加一个"无"
                        // treeSelectData.value.unshift({id: 0, name: '无'});
                        // console.log("treeSelectData.value：", treeSelectData.value);

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
            const editor = new E('#content');//定义富文本
            editor.config.zIndex=0;//让富文本不要遮住下拉框，因为富文本的Index设置的很高，默认是500，这个z-index就是覆盖的层级的大小
            /**
             * 富文档保存
             */
            const handleSave = () => {
                modalLoading.value = true;
                // doc.value={ebookId: route.query.ebookId};
                console.log(doc.value.ebookId)
                doc.value.content = editor.txt.html();//获得富文本的内容
                axios.post("/doc/save", doc.value).then((response) => {
                    modalLoading.value=false;
                    console.log(doc.value);
                    const data = response.data;//data=commonResp
                    if (!data.success){
                        // message.success("保存成功！");
                        //重新加载列表
                        // handleQuery();
                        // message.error(data.message);
                    }
                });
                axios.post("/docin/save", doc.value).then((response) => {
                    modalLoading.value=false;
                    console.log(doc.value);
                    const data = response.data;//data=commonResp
                    if (data.success){
                        message.success("保存成功！");
                        //重新加载列表
                        handleQuery();
                    }else {
                        message.error(data.message);
                    }
                });
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
                        editor.txt.html(data.content)
                    } else {
                        message.error(data.message);
                    }
                });
            };
            /**
             * 编辑
             */
            const edit = (record:any) => {
                // 清空富文本框
                editor.txt.html("");
                modalVisible.value = true;
                doc.value=Tool.copy(record);
                handleQueryContent();

                // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
                treeSelectData.value = Tool.copy(level1.value);
                setDisable(treeSelectData.value, record.id);

                // 为选择树添加一个"无",在数组的前面添加节点，push是往后面添加元素
                treeSelectData.value.unshift({id: 0, name: '无'});
                setTimeout(function () {
                    editor.create();//创建富文本
                },100);
            };

            /**
             * 新增
             */
            const add = () => {
                // 清空富文本框
                editor.txt.html("");
                modalVisible.value = true;
                // handleQueryContent();

                treeSelectData.value = Tool.copy(level1.value) || [];

                // 为选择树添加一个"无"
                treeSelectData.value.unshift({id: 0, name: '无'});
            };
            /**
             * 文档删除按钮
             */
            const handleDelete = (idin: number) => {
                // 清空数组，否则多次删除时，数组会一直增加
                const idinname =getIdName(docins,idin);
                console.log(idinname);
                const id = getNameId(docs,idinname)
                console.log(id)
                // ids.length = 0;
                deleteNames.length = 0;
                const idins = getDeleteIds(level1.value, idin);
                const ids = getDeleteIds(level.value, id);
                getDeleteIds(level1.value, id);
                //二次确认框
                Modal.confirm({
                    title: '重要提醒',
                    icon: createVNode(ExclamationCircleOutlined),
                    content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
                    onOk() {
                        // console.log(ids)
                        axios.delete("/docin/delete/" + idins.join(",")).then((response) => {
                            const data = response.data; // data = commonResp
                            if (data.success) {
                                // 重新加载列表
                                handleQuery();
                            } else {
                                message.error(data.message);
                            }
                        });
                        // // console.log(ids)
                        axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
                            const data = response.data; // data = commonResp
                            if (data.success) {
                                // 重新加载列表
                                handleQuery();
                            } else {
                                message.error(data.message);
                            }
                        });
                    },
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
            const deleteNames: Array<string> = [];//在二次确认框的时候可以显示子文档的名称
            const getDeleteIds = (treeSelectData: any, id: any) => {
                const ids: Array<string> = [];
                // 遍历数组，即遍历某一层节点
                for (let i = 0; i < treeSelectData.length; i++) {
                    const node = treeSelectData[i];
                    if (node.id === id) {
                        // 如果当前节点就是目标节点
                        console.log("delete", node);
                        // 将目标ID放入结果集ids
                        // node.disabled = true;
                        ids.push(id);
                        deleteNames.push(node.name);

                        // 遍历所有子节点
                        const children = node.children;
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
                return ids;
            };

            // ----------------富文本预览--------------
            const drawerVisible = ref(false);
            const previewHtml = ref();
            const handlePreviewContent = () => {
                const html = editor.txt.html();
                previewHtml.value = html;
                drawerVisible.value = true;
            };
            const onDrawerClose = () => {
                drawerVisible.value = false;
            };
            onMounted(() => {
                handleQuery();
                editor.create();//创建富文本
            });
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

                user
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