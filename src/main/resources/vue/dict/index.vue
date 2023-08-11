<template>
  <c-page-label title="标题" icon="cyber-yingyong" document-link="javascript:;">
    <template #tips>提示内容。</template>
  </c-page-label>

  <c-table-wrapper
    rowKey="id"
    ref="tableRef"
    v-model:loading="tableState.loading"
    :columns="tableState.columns"
    :overlayMenu="tableState.overlayMenu"
    @search="methods.searchQuery"
  >
    <template #collapse>
      <!-- 主键ID搜索 -->
      <a-input
        v-model:value="queryState.id"
        placeholder="请输入主键ID搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 父ID搜索 -->
      <a-input
        v-model:value="queryState.parentId"
        placeholder="请输入父ID搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 字典类型（0字典组 1字典类型 3字典值）搜索 -->
      <a-input
        v-model:value="queryState.type"
        placeholder="请输入字典类型（0字典组 1字典类型 3字典值）搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 字典组名称搜索 -->
      <a-input
        v-model:value="queryState.name"
        placeholder="请输入字典组名称搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 字典组编码搜索 -->
      <a-input
        v-model:value="queryState.code"
        placeholder="请输入字典组编码搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 显示顺序搜索 -->
      <a-input
        v-model:value="queryState.orderNum"
        placeholder="请输入显示顺序搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 颜色搜索 -->
      <a-input
        v-model:value="queryState.color"
        placeholder="请输入颜色搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 图标搜索 -->
      <a-input
        v-model:value="queryState.icon"
        placeholder="请输入图标搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 负责人搜索 -->
      <a-input
        v-model:value="queryState.leader"
        placeholder="请输入负责人搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 字典组描述搜索 -->
      <a-input
        v-model:value="queryState.description"
        placeholder="请输入字典组描述搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 删除标志（0正常 1删除）搜索 -->
      <a-input
        v-model:value="queryState.deleted"
        placeholder="请输入删除标志（0正常 1删除）搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 创建者搜索 -->
      <a-input
        v-model:value="queryState.creator"
        placeholder="请输入创建者搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 创建时间搜索 -->
      <a-input
        v-model:value="queryState.createTime"
        placeholder="请输入创建时间搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 更新者搜索 -->
      <a-input
        v-model:value="queryState.updator"
        placeholder="请输入更新者搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 更新时间搜索 -->
      <a-input
        v-model:value="queryState.updateTime"
        placeholder="请输入更新时间搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
      <!-- 备注搜索 -->
      <a-input
        v-model:value="queryState.remark"
        placeholder="请输入备注搜索..."
        style="width: 256px"
        @keydown.enter="methods.searchQuery()"
      >
        <template #suffix><c-icon icon="cyber-sousuo" size="16" color="#BDBDBD" /></template>
      </a-input>
    </template>
    <template #right>
      <a-button type="primary" @click="methods.showModify()">新建</a-button>
    </template>
  </c-table-wrapper>

  <Modify ref="modifyRef" @ok="methods.searchQuery()"></Modify>
</template>

<script setup>
import {deleteConfrim} from '@/api';
import Modify from './modules/Modify.vue';
import {changeHistoryState, initHistoryState} from 'cyber-web-ui';

const tableRef = ref(); // 表格ref
const modifyRef = ref(); // 弹窗ref
// 表格请求参数
const queryState = reactive({
  ...initHistoryState({
    id: undefined,
    parentId: undefined,
    type: undefined,
    name: undefined,
    code: undefined,
    orderNum: undefined,
    color: undefined,
    icon: undefined,
    leader: undefined,
    description: undefined,
    deleted: undefined,
    creator: undefined,
    createTime: undefined,
    updator: undefined,
    updateTime: undefined,
    remark: undefined,
  }),
});
// 表格信息
const tableState = reactive({
  loading: false,
  columns: [
    { title: '主键ID', dataIndex: "id" },
    { title: '父ID', dataIndex: "parentId" },
    { title: '字典类型（0字典组 1字典类型 3字典值）', dataIndex: "type" },
    { title: '字典组名称', dataIndex: "name" },
    { title: '字典组编码', dataIndex: "code" },
    { title: '显示顺序', dataIndex: "orderNum" },
    { title: '颜色', dataIndex: "color" },
    { title: '图标', dataIndex: "icon" },
    { title: '负责人', dataIndex: "leader" },
    { title: '字典组描述', dataIndex: "description" },
    { title: '删除标志（0正常 1删除）', dataIndex: "deleted" },
    { title: '创建者', dataIndex: "creator" },
    { title: '创建时间', dataIndex: "createTime" },
    { title: '更新者', dataIndex: "updator" },
    { title: '更新时间', dataIndex: "updateTime" },
    { title: '备注', dataIndex: "remark" },
  ],
  overlayMenu: [
    {
      label: "编辑",
      handler: (record) => methods.showModify(record),
    },
    {
      label: "删除",
      handler: (record) => methods.delete(record),
    },
  ],
});

const methods = {
  // 搜索表格
  searchQuery() {
    changeHistoryState(queryState);
    unref(tableRef).searchQuery({
      url: 'dict/search',
      method: 'get',
      params: queryState,
    });
  },
  // 显示弹窗
  showModify(record) {
    unref(modifyRef).showModal(record);
  },
  // 删除
  delete(record) {
    deleteConfrim({
      content: `是否确认删除“${record.name}（${record.code}）”？`,
      value: record.code,
    }, {
      url: 'dict',
      method: 'delete',
      params: {
        id: record.id,
      }
    }).then(() => {
      methods.searchQuery();
    });
  },
};

onMounted(() => {
  methods.searchQuery();
});
</script>

<style lang="less" scoped>
</style>
