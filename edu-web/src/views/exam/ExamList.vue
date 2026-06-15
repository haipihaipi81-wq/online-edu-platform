<template>
  <div class="page-container">
    <div class="page-header">
      <h2>在线考试</h2>
      <div>
        <el-button v-if="userStore.isTeacher" @click="showGen">自动组卷</el-button>
        <el-button v-if="userStore.isTeacher" type="primary" @click="showCreate">创建试卷</el-button>
      </div>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="试卷名称" />
      <el-table-column prop="duration" label="时长(分)" width="90" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column prop="passScore" label="及格分" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">{{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="goExam(row)">{{ userStore.isTeacher ? '预览' : '参加考试' }}</el-button>
          <el-button v-if="userStore.isTeacher && row.status === 'DRAFT'" size="small" type="success" @click="publish(row.id)">发布</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="创建试卷" width="500px">
      <el-form :model="examForm" label-position="top">
        <el-form-item label="试卷名称"><el-input v-model="examForm.title" /></el-form-item>
        <el-form-item label="课程ID"><el-input-number v-model="examForm.courseId" :min="1" /></el-form-item>
        <el-form-item label="考试时长(分钟)"><el-input-number v-model="examForm.duration" :min="1" /></el-form-item>
        <el-form-item label="及格分数"><el-input-number v-model="examForm.passScore" :min="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="genVisible" title="自动组卷" width="500px">
      <el-form :model="genForm" label-position="top">
        <el-form-item label="试卷名称"><el-input v-model="genForm.title" /></el-form-item>
        <el-form-item label="课程ID"><el-input-number v-model="genForm.courseId" :min="1" /></el-form-item>
        <el-form-item label="单选题数量"><el-input-number v-model="genForm.singleCount" :min="0" /></el-form-item>
        <el-form-item label="多选题数量"><el-input-number v-model="genForm.multiCount" :min="0" /></el-form-item>
        <el-form-item label="判断题数量"><el-input-number v-model="genForm.judgeCount" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="genVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAutoGen">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getExams, createExam, autoGenerateExam, publishExam } from '@/api/exam'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const genVisible = ref(false)
const examForm = reactive({ title: '', courseId: 1, duration: 60, passScore: 60 })
const genForm = reactive({ title: '', courseId: 1, singleCount: 10, multiCount: 5, judgeCount: 10 })

async function fetchData() {
  loading.value = true
  const res: any = await getExams({ page: 1, size: 50 })
  list.value = res.data.records
  loading.value = false
}

function showCreate() { dialogVisible.value = true }
function showGen() { genVisible.value = true }

async function handleCreate() {
  await createExam({ ...examForm, questionItems: [] })
  dialogVisible.value = false
  ElMessage.success('试卷已创建')
  fetchData()
}

async function handleAutoGen() {
  await autoGenerateExam({ courseId: genForm.courseId, title: genForm.title, rules: { singleCount: genForm.singleCount, multiCount: genForm.multiCount, judgeCount: genForm.judgeCount, duration: 60, passScore: 60, difficulty: 3 } })
  genVisible.value = false
  ElMessage.success('组卷完成')
  fetchData()
}

async function publish(id: number) {
  await publishExam(id)
  ElMessage.success('已发布')
  fetchData()
}

function goExam(row: any) {
  router.push(`/exams/${row.id}`)
}

onMounted(fetchData)
</script>
