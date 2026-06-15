<template>
  <div class="page-container">
    <div class="page-header">
      <h2>课程管理</h2>
      <el-button type="primary" @click="showCreateDialog">创建课程</el-button>
    </div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="课程名称" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PUBLISHED' ? 'success' : row.status === 'DRAFT' ? 'info' : 'danger'">
            {{ row.status === 'PUBLISHED' ? '已发布' : row.status === 'DRAFT' ? '草稿' : '已关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="80" />
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="editCourse(row)">编辑</el-button>
          <el-button size="small" type="primary" @click="manageChapters(row)">章节</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="editing ? '编辑课程' : '创建课程'" width="600px">
      <el-form :model="courseForm" label-position="top">
        <el-form-item label="课程名称">
          <el-input v-model="courseForm.title" />
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input v-model="courseForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="封面URL">
          <el-input v-model="courseForm.cover" />
        </el-form-item>
        <el-form-item label="分类ID">
          <el-input-number v-model="courseForm.categoryId" :min="1" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="courseForm.price" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">保存</el-button>
      </template>
    </el-dialog>

    <!-- Chapter Dialog -->
    <el-dialog v-model="chapterVisible" title="章节管理" width="700px">
      <div v-for="(ch, idx) in chapters" :key="ch.id || idx" style="margin-bottom:16px;padding:12px;background:#f5f7fa;border-radius:4px">
        <div style="display:flex;justify-content:space-between;margin-bottom:8px">
          <strong>{{ ch.title }}</strong>
          <el-button size="small" type="primary" @click="addSection(ch)">+课时</el-button>
        </div>
        <div v-for="sec in ch.sections" :key="sec.id" style="font-size:13px;padding:4px 8px;margin:4px 0">
          [{{ sec.contentType }}] {{ sec.title }} - {{ sec.duration }}s
        </div>
      </div>
      <el-button @click="addChapter">添加章节</el-button>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTeacherCourses, createCourse, updateCourse, deleteCourse, getChapters, addChapter as addChapterApi, addSection as addSectionApi } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const chapterVisible = ref(false)
const editing = ref(false)
const selectedCourseId = ref<number>(0)
const chapters = ref<any[]>([])

const courseForm = ref({ title: '', description: '', cover: '', categoryId: 1, price: 0, id: 0 })

async function fetchData() {
  loading.value = true
  const res: any = await getTeacherCourses(1, 50)
  list.value = res.data.records
  loading.value = false
}

function showCreateDialog() {
  editing.value = false
  courseForm.value = { title: '', description: '', cover: '', categoryId: 1, price: 0, id: 0 }
  dialogVisible.value = true
}

function editCourse(row: any) {
  editing.value = true
  courseForm.value = { ...row }
  dialogVisible.value = true
}

async function saveCourse() {
  if (editing.value) {
    await updateCourse(courseForm.value.id, courseForm.value)
  } else {
    await createCourse(courseForm.value)
  }
  dialogVisible.value = false
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  await deleteCourse(id)
  ElMessage.success('已删除')
  fetchData()
}

async function manageChapters(row: any) {
  selectedCourseId.value = row.id
  const res: any = await getChapters(row.id)
  chapters.value = res.data || []
  chapterVisible.value = true
}

async function addChapter() {
  const title = prompt('请输入章节名称')
  if (title) {
    await addChapterApi(selectedCourseId.value, { title, sort: chapters.value.length + 1 })
    await manageChapters({ id: selectedCourseId.value })
  }
}

async function addSection(ch: any) {
  const title = prompt('请输入课时名称')
  if (title) {
    await addSectionApi(selectedCourseId.value, ch.id, { title, contentType: 'VIDEO', sort: (ch.sections?.length || 0) + 1, duration: 0 })
    await manageChapters({ id: selectedCourseId.value })
  }
}

onMounted(fetchData)
</script>
