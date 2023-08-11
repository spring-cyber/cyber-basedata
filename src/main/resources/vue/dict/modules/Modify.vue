<template>
  <c-modal
    ref="modalRef"
    v-model:visible="modalState.visible"
    width="600px"
    :title="modalState.title"
    :okText="modalState.okText"
    @ok="methods.onSubmit"
  >
    <a-form
      ref="formRef"
      name="formName"
      :model="formState"
      :rules="rules"
      autocomplete="off" layout="vertical"
    >
      <div class="grid grid-cols-2 gap-x-20px">
        <a-form-item label="主键ID" name="id">
          <a-input v-model:value="formState.id" placeholder="请输入主键ID..."></a-input>
        </a-form-item>
        <a-form-item label="父ID" name="parentId">
          <a-input v-model:value="formState.parentId" placeholder="请输入父ID..."></a-input>
        </a-form-item>
        <a-form-item label="字典类型（0字典组 1字典类型 3字典值）" name="type">
          <a-input v-model:value="formState.type" placeholder="请输入字典类型（0字典组 1字典类型 3字典值）..."></a-input>
        </a-form-item>
        <a-form-item label="字典组名称" name="name">
          <a-input v-model:value="formState.name" placeholder="请输入字典组名称..."></a-input>
        </a-form-item>
        <a-form-item label="字典组编码" name="code">
          <a-input v-model:value="formState.code" placeholder="请输入字典组编码..."></a-input>
        </a-form-item>
        <a-form-item label="显示顺序" name="orderNum">
          <a-input v-model:value="formState.orderNum" placeholder="请输入显示顺序..."></a-input>
        </a-form-item>
        <a-form-item label="颜色" name="color">
          <a-input v-model:value="formState.color" placeholder="请输入颜色..."></a-input>
        </a-form-item>
        <a-form-item label="图标" name="icon">
          <a-input v-model:value="formState.icon" placeholder="请输入图标..."></a-input>
        </a-form-item>
        <a-form-item label="负责人" name="leader">
          <a-input v-model:value="formState.leader" placeholder="请输入负责人..."></a-input>
        </a-form-item>
        <a-form-item label="字典组描述" name="description">
          <a-input v-model:value="formState.description" placeholder="请输入字典组描述..."></a-input>
        </a-form-item>
        <a-form-item label="删除标志（0正常 1删除）" name="deleted">
          <a-input v-model:value="formState.deleted" placeholder="请输入删除标志（0正常 1删除）..."></a-input>
        </a-form-item>
        <a-form-item label="创建者" name="creator">
          <a-input v-model:value="formState.creator" placeholder="请输入创建者..."></a-input>
        </a-form-item>
        <a-form-item label="创建时间" name="createTime">
          <a-input v-model:value="formState.createTime" placeholder="请输入创建时间..."></a-input>
        </a-form-item>
        <a-form-item label="更新者" name="updator">
          <a-input v-model:value="formState.updator" placeholder="请输入更新者..."></a-input>
        </a-form-item>
        <a-form-item label="更新时间" name="updateTime">
          <a-input v-model:value="formState.updateTime" placeholder="请输入更新时间..."></a-input>
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-input v-model:value="formState.remark" placeholder="请输入备注..."></a-input>
        </a-form-item>
      </div>
    </a-form>
  </c-modal>
</template>

<script setup>
import axios, {queryDetail} from '@/api';
import {message} from 'ant-design-vue';
import {required} from 'cyber-web-ui';

const formRef = ref(); // 表单ref
// 弹窗信息
const modalState = reactive({
  visible: false,
  isCreate: true,
  title: computed(() => modalState.isCreate ? '新建' : '编辑'),
  okText: computed(() => modalState.isCreate ? '新建' : '确定'),
});
// 表单信息
const formState = reactive({
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
});
// 表单校验规则
const rules = {
  id: required(),
  parentId: required(),
  type: required(),
  name: required(),
  code: required(),
  orderNum: required(),
  color: required(),
  icon: required(),
  leader: required(),
  description: required(),
  deleted: required(),
  creator: required(),
  createTime: required(),
  updator: required(),
  updateTime: required(),
  remark: required(),
};
const $emit = defineEmits(['ok']);
const methods = {
  async showModal(record) {
    modalState.visible = true;
    modalState.isCreate = !record?.id;
    let detail = await queryDetail('dict', record);
    Object.keys(formState).forEach(key => {
      formState[key] = detail[key];
    });
    nextTick(unref(formRef)?.clearValidate);
  },
  onSubmit() {
    return new Promise(async (resolve, reject) => {
      try {
        // 校验表单
        await unref(formRef).validate();
        // 请求添加/修改接口
        let res = await axios.request({
          url: 'dict',
          method: modalState.isCreate ? 'post' : 'put',
          data: formState
        });
        message.success(res.message);
        $emit('ok');
        resolve();
      } catch (error) {
        console.log('error', error);
        reject();
      }
    })
  },
};

defineExpose({
  showModal: methods.showModal,
});
</script>

<style lang="less" scoped>
</style>
