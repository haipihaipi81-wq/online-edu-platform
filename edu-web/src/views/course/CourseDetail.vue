<template>
  <div class="page-container" v-loading="loading">
    <div v-if="course" style="display:flex;gap:24px">
      <div style="flex:1">
        <img :src="course.cover || 'https://placeholder.pics/svg/600x300'" style="width:100%;height:300px;object-fit:cover;border-radius:8px" />
        <h1 style="margin:20px 0 10px">{{ course.title }}</h1>
        <p style="color:#606266;line-height:1.8">{{ course.description }}</p>
        <el-button type="success" size="large" style="margin-top:20px" @click="enroll">立即学习</el-button>
      </div>
      <div style="width:320px">
        <el-card>
          <template #header>课程章节</template>
          <div v-if="chapters.length === 0" style="color:#909399">暂无章节</div>
          <div v-for="ch in chapters" :key="ch.id" style="margin-bottom:12px">
            <h4 style="margin-bottom:8px">{{ ch.title }}</h4>
            <div v-for="sec in ch.sections" :key="sec.id" style="padding:6px 12px;background:#f5f7fa;margin-bottom:4px;border-radius:4px;font-size:13px;display:flex;justify-content:space-between">
              <span>{{ sec.title }}</span>
              <span style="color:#909399">{{ sec.duration ? Math.floor(sec.duration/60)+':'+(sec.duration%60).toString().padStart(2,'0') : '' }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourseDetail, enrollCourse } from '@/api/course'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const course = ref<any>(null)
const chapters = ref<any[]>([])

async function enroll() {
  try {
    await enrollCourse(Number(route.params.id))
    ElMessage.success('选课成功')
  } catch { /* handled */ }
}

onMounted(async () => {
  loading.value = true
  const res: any = await getCourseDetail(Number(route.params.id))
  course.value = res.data.course
  chapters.value = res.data.chapters || []
  loading.value = false
})
</script>
