<template>
  <div class="page-container">
    <div class="page-header">
      <h2>作业管理</h2>
      <el-button v-if="userStore.isTeacher" type="primary" @click="showCreate">布置作业</el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="作业标题" />
      <el-table-column prop="content" label="要求" show-overflow-tooltip />
      <el-table-column prop="deadline" label="截止时间" width="170" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button v-if="userStore.isTeacher" size="small" @click="viewSubmits(row)">查看提交</el-button>
          <el-button v-else size="small" type="primary" @click="openSubmit(row)">提交作业</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="布置作业" width="500px">
      <el-form :model="form" label-position="top">
        <el-form-item label="作业标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="作业要求"><el-input v-model="form.content" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="课程ID"><el-input-number v-model="form.courseId" :min="1" /></el-form-item>
        <el-form-item label="截止时间"><el-date-picker v-model="form.deadline" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="submitVisible" title="提交作业" width="500px">
      <el-form :model="submitForm" label-position="top">
        <el-form-item label="作业内容"><el-input v-model="submitForm.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="附件URL(可选)"><el-input v-model="submitForm.fileUrl" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" @click="doSubmit">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="submitsVisible" title="提交列表" width="600px">
      <div v-for="s in submits" :key="s.id" style="padding:12px;background:#f5f7fa;border-radius:4px;margin-bottom:12px">
        <p><strong>学生ID:</strong> {{ s.userId }}</p>
        <p>{{ s.content }}</p>
        <p v-if="s.fileUrl"><a :href="s.fileUrl" target="_blank">附件</a></p>
        <p style="color:#909399;font-size:12px">提交时间: {{ s.submitTime }}</p>
        <div v-if="s.status === 'GRADED'" style="color:#67C23A">得分: {{ s.score }} | 评语: {{ s.comment }}</div>
        <div v-else style="margin-top:8px;display:flex;gap:8px">
          <el-input-number v-model="gradeScores[s.id]" :min="0" :max="100" size="small" placeholder="分数" />
          <el-input v-model="gradeComments[s.id]" size="small" placeholder="评语" />
          <el-button size="small" type="primary" @click="doGrade(s.id)">评分</el-button>
        </div>
      </div>
      <div v-if="submits.length === 0" style="color:#909399;text-align:center">暂无提交</div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getHomeworkList, createHomework, submitHomework, getHomeworkSubmits, gradeHomework } from '@/api/exam'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitVisible = ref(false)
const submitsVisible = ref(false)
const submits = ref<any[]>([])
const selectedHwId = ref(0)
const form = reactive({ title: '', content: '', courseId: 1, deadline: null as string | null })
const submitForm = reactive({ content: '', fileUrl: '' })
const gradeScores = reactive<Record<number, number>>({})
const gradeComments = reactive<Record<number, string>>({})

async function fetchData() {
  loading.value = true
  const res: any = await getHomeworkList({ page: 1, size: 50 })
  list.value = res.data.records
  loading.value = false
}

function showCreate() { dialogVisible.value = true }

async function handleCreate() {
  try {
    await createHomework({
      title: form.title,
      content: form.content,
      courseId: form.courseId,
      deadline: form.deadline ? form.deadline : undefined
    })
    dialogVisible.value = false
    ElMessage.success('作业已布置')
    form.title = ''
    form.content = ''
    form.deadline = null
    fetchData()
  } catch {
    // error already shown by interceptor
  }
}

function openSubmit(row: any) {
  selectedHwId.value = row.id
  submitVisible.value = true
}

async function doSubmit() {
  try {
    await submitHomework(selectedHwId.value, { content: submitForm.content, fileUrl: submitForm.fileUrl })
    submitVisible.value = false
    ElMessage.success('提交成功')
    submitForm.content = ''
    submitForm.fileUrl = ''
  } catch {
    // error already shown by interceptor
  }
}

async function viewSubmits(row: any) {
  selectedHwId.value = row.id
  const res: any = await getHomeworkSubmits(row.id)
  submits.value = res.data || []
  submitsVisible.value = true
}

async function doGrade(submitId: number) {
  await gradeHomework(submitId, { score: gradeScores[submitId] || 0, comment: gradeComments[submitId] || '' })
  ElMessage.success('评分完成')
  viewSubmits({ id: selectedHwId.value })
}

onMounted(fetchData)
</script>
