<template>
  <div class="page-container">
    <h2>用户管理</h2>
    <el-row :gutter="12" style="margin:16px 0">
      <el-col :span="8">
        <el-input v-model="keyword" placeholder="搜索用户名" clearable @keyup.enter="fetchData" />
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </el-col>
    </el-row>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'TEACHER' ? 'success' : ''">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="(v:boolean) => toggleStatus(row.id, v)" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="170" />
    </el-table>
    <el-pagination v-if="total>0" style="margin-top:20px;justify-content:center"
      v-model:current-page="page" :page-size="size" :total="total"
      layout="prev,pager,next" @current-change="fetchData" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getUsers, updateUserStatus } from '@/api/user'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')

async function fetchData() {
  loading.value = true
  const res: any = await getUsers(page.value, size.value, keyword.value)
  list.value = res.data.records
  total.value = res.data.total
  loading.value = false
}

async function toggleStatus(id: number, v: boolean) {
  await updateUserStatus(id, v ? 1 : 0)
  ElMessage.success('状态已更新')
  fetchData()
}

onMounted(fetchData)
</script>
