<template>
    <headers></headers>
    <a-layout>
        <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
            <h3 v-if="level1.length === 0">对不起，找不到相关文档！</h3>
            <a-row>
                <a-col :span="4">
                    <a-tree
                            v-if="level1.length > 0"
                            :tree-data="level1"
                            @select="onSelect"
                            :replaceFields="{title: 'name', key: 'id', value:'id'}"
                            :defaultExpandAll="true"
                            :defaultSelectedKeys="defaultSelectedKeys"
                    >
                    </a-tree>
                </a-col>
                <a-col :span="20">
                    <div>
                        <h2>{{doc.name}}</h2>
                        <div>
                            <span>阅读数：{{doc.viewCount}}</span> &nbsp; &nbsp;
                            <span>点赞数：{{doc.voteCount}}</span>
                        </div>
                        <a-divider style="height: 2px; background-color: #9999cc"/>
                    </div>
                    <div :innerHTML="html" class="wangeditor"></div>
                    <div class="vote-div">
                        <a-button type="primary" shape="round" :size="'large'" @click="vote">
                            <template #icon><LikeOutlined /> &nbsp;点赞：{{doc.voteCount}} </template>
                        </a-button>
                    </div>
                </a-col>
            </a-row>
        </a-layout-content>
    </a-layout>
    <footers></footers>
</template>

<script lang="ts">
    import { defineComponent, onMounted, ref, createVNode ,computed} from 'vue';
    import axios from 'axios';
    import {message} from 'ant-design-vue';
    import {Tool} from "../util/tool";
    import {useRoute} from "vue-router";
    import store from "../store"

    export default defineComponent({
        name: 'Doc',
        setup() {
            const route = useRoute();
            const docs = ref();
            const html = ref();
            const user = computed(() => store.state.user);
            const defaultSelectedKeys = ref();
            defaultSelectedKeys.value = [];
            // 当前选中的文档
            const doc = ref();
            doc.value = {};

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
            level1.value = [];


            /**
             * 数据查询
             **/
            const handleQuery = () => {
                if(user.value.id){
                    axios.get("/docin/all/" + route.query.ebookId).then((response) => {
                        const data = response.data;
                        if (data.success) {
                            docs.value = data.content;
                            // console.log("未转换后的扁平数组"+docs.value);
                            console.log("原始数组：", docs.value);
                            level1.value = [];
                            level1.value = Tool.array2Tree(docs.value, 0);
                            // console.log("转换后的树结构"+level1.value);
                            console.log("树形结构：", level1);

                            if (Tool.isNotEmpty(level1)) {
                                //将该节点设置成选中状态
                                defaultSelectedKeys.value = [level1.value[0].id];
                                //根据这个节点去查内容
                                handleQueryContent(level1.value[0].id);
                                // 初始显示文档信息
                                doc.value = level1.value[0];
                            }
                        } else {
                            message.error(data.message);
                        }
                    });
                }else {
                    axios.get("/doc/all/" + route.query.ebookId).then((response) => {
                        const data = response.data;
                        if (data.success) {
                            docs.value = data.content;
                            // console.log("未转换后的扁平数组"+docs.value);
                            console.log("原始数组：", docs.value);
                            level1.value = [];
                            level1.value = Tool.array2Tree(docs.value, 0);
                            // console.log("转换后的树结构"+level1.value);
                            console.log("树形结构：", level1);

                            if (Tool.isNotEmpty(level1)) {
                                //将该节点设置成选中状态
                                defaultSelectedKeys.value = [level1.value[0].id];
                                //根据这个节点去查内容
                                handleQueryContent(level1.value[0].id);
                                // 初始显示文档信息
                                doc.value = level1.value[0];
                            }
                        } else {
                            message.error(data.message);
                        }
                    });
                }

            };
            /**
             * 内容查询,跟文档管理是一样的
             **/
            const handleQueryContent = (id: number) => {
                if(user.value.id){
                    axios.get("/docin/find-content/" + id).then((response) => {
                        const datain = response.data;
                        if (datain.success) {
                            console.log(datain)
                        } else {
                            message.error(datain.message);
                        }
                    });
                }else {
                    axios.get("/doc/find-content/" + id).then((response) => {
                        const data = response.data;
                        if (data.success) {
                            // console.log(data.content);
                            html.value = data.content;
                            // test.value=doc.value.name;
                            // console.log(html.value);
                        } else {
                            message.error(data.message);
                        }
                    });
                }


            };
            const onSelect = (selectedKeys: any, info: any) => {
                if (Tool.isNotEmpty(selectedKeys)) {
                    // 选中某一节点时，加载该节点的文档信息
                    console.log(info)
                    doc.value = info.selectedNodes[0];
                    // 加载内容
                    handleQueryContent(selectedKeys[0]);
                }
            };
            // 点赞
            const vote = () => {
                axios.get('/doc/vote/' + doc.value.id).then((response) => {
                    const data = response.data;
                    if (data.success) {
                        doc.value.voteCount++;
                    } else {
                        message.error(data.message);
                    }
                });
                axios.get('/docin/vote/' + doc.value.id).then((response) => {
                    const datain = response.data;
                    if (datain.success) {
                        console.log(datain)
                    } else {
                        // message.error(datain.message);
                    }
                });
            };

            onMounted(() => {
                handleQuery();
            });
            return {
                level1,
                html,
                onSelect,
                defaultSelectedKeys,
                doc,
                vote,

                user
            }
        }
    });
</script>

<style>
    /* wangeditor默认样式, 参照: http://www.wangeditor.com/doc/pages/02-%E5%86%85%E5%AE%B9%E5%A4%84%E7%90%86/03-%E8%8E%B7%E5%8F%96html.html */
    /* table 样式 */
    .wangeditor table {
        border-top: 1px solid #ccc;
        border-left: 1px solid #ccc;
    }
    .wangeditor table td,
    .wangeditor table th {
        border-bottom: 1px solid #ccc;
        border-right: 1px solid #ccc;
        padding: 3px 5px;
    }
    .wangeditor table th {
        border-bottom: 2px solid #ccc;
        text-align: center;
    }

    /* blockquote 样式 */
    .wangeditor blockquote {
        display: block;
        border-left: 8px solid #d0e5f2;
        padding: 5px 10px;
        margin: 10px 0;
        line-height: 1.4;
        font-size: 100%;
        background-color: #f1f1f1;
    }

    /* code 样式 */
    .wangeditor code {
        display: inline-block;
        *display: inline;
        *zoom: 1;
        background-color: #f1f1f1;
        border-radius: 3px;
        padding: 3px 5px;
        margin: 0 3px;
    }
    .wangeditor pre code {
        display: block;
    }

    /* ul ol 样式 */
    .wangeditor ul, ol {
        margin: 10px 0 10px 20px;
    }

    /* 和antdv p冲突，覆盖掉 */
    .wangeditor blockquote p {
        font-family:"YouYuan";
        margin: 20px 10px !important;
        font-size: 16px !important;
        font-weight:600;
    }

    /* 点赞 */
    .vote-div {
        padding: 15px;
        text-align: center;
    }

    .ant-btn-primary {
        background: #8b96a2 !important;
        border-color: #b0bac3 !important;
    }

    /* 图片自适应 */
    .wangeditor img {
        max-width: 100%;
        height: auto;
    }

    /* 视频自适应 */
    .wangeditor iframe {
        width: 100%;
        height: 400px;
    }
    .ant-btn-icon-only > .anticon{
        display: inline-flex !important;
        justify-content: center;
    }
    .ant-col-20 {
        padding: 60px;
    }
    .ant-tree-list-holder-inner {
        overflow: scroll;
    }
    .ant-tree .ant-tree-treenode {
        padding: 10px 0px 5px 10px !important;
    }
</style>