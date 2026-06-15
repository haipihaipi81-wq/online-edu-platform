<template>
  <div class="page-container" v-loading="loading">
    <div v-if="!submitted && !started">
      <h2>{{ exam?.title }}</h2>
      <el-descriptions :column="2" border style="margin:20px 0">
        <el-descriptions-item label="考试时长">{{ exam?.duration }} 分钟</el-descriptions-item>
        <el-descriptions-item label="总分">{{ exam?.totalScore }} 分</el-descriptions-item>
        <el-descriptions-item label="及格分数">{{ exam?.passScore }} 分</el-descriptions-item>
      </el-descriptions>
      <el-button type="primary" size="large" @click="beginExam">开始考试</el-button>
    </div>

    <div v-if="started && !submitted">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
        <h2>{{ exam?.title }}</h2>
        <el-tag type="danger" effect="dark">剩余时间: {{ formatTime(remaining) }}</el-tag>
      </div>
      <div v-for="(q, idx) in questions" :key="q.id" style="background:#fff;padding:20px;border-radius:8px;margin-bottom:16px;box-shadow:0 1px 4px rgba(0,0,0,0.08)">
        <h4>{{ idx + 1 }}. [{{ q.type }}] {{ q.content }} ({{ q.score }}分)</h4>
        <div v-if="q.type === 'SINGLE' || q.type === 'MULTI'" style="margin-top:12px">
          <el-radio-group v-model="answers[q.id]" v-if="q.type === 'SINGLE'">
            <el-radio v-for="(opt, oi) in (q.options || [])" :key="oi" :value="opt">{{ opt }}</el-radio>
          </el-radio-group>
          <el-checkbox-group v-model="multiAnswers[q.id]" v-else>
            <el-checkbox v-for="(opt, oi) in (q.options || [])" :key="oi" :label="opt" :value="opt" />
          </el-checkbox-group>
        </div>
        <div v-else-if="q.type === 'JUDGE'" style="margin-top:12px">
          <el-radio-group v-model="answers[q.id]">
            <el-radio value="正确">正确</el-radio>
            <el-radio value="错误">错误</el-radio>
          </el-radio-group>
        </div>
        <div v-else style="margin-top:12px">
          <el-input v-model="answers[q.id]" type="textarea" :rows="3" placeholder="请输入答案" />
        </div>
      </div>
      <el-button type="primary" size="large" @click="submitAnswers" :loading="submitting">提交答卷</el-button>
    </div>

    <div v-if="submitted && result">
      <el-result icon="success" title="提交成功">
        <template #sub-title>
          <p>得分: {{ result.score }} / {{ exam?.totalScore }}</p>
          <p>{{ result.score >= (exam?.passScore || 60) ? '恭喜通过!' : '未达到及格线' }}</p>
        </template>
        <template #extra>
          <el-button type="primary" @click="$router.push('/exams')">返回列表</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { getExamDetail, startExam, submitExam, getExamResult } from '@/api/exam'

const route = useRoute()
const loading = ref(false)
const exam = ref<any>(null)
const questions = ref<any[]>([])
const started = ref(false)
const submitted = ref(false)
const submitting = ref(false)
const result = ref<any>(null)
const recordId = ref<number>(0)
const answers = reactive<Record<number, string>>({})
const multiAnswers = reactive<Record<number, string[]>>({})

let timer: number | null = null
const remaining = ref(0)

function formatTime(s: number) {
  const m = Math.floor(s / 60), sec = s % 60
  return `${m.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
}

async function beginExam() {
  const res: any = await startExam(Number(route.params.id))
  recordId.value = res.data.id
  started.value = true
  remaining.value = (exam.value?.duration || 60) * 60
  timer = window.setInterval(() => {
    remaining.value--
    if (remaining.value <= 0) submitAnswers()
  }, 1000)
}

async function submitAnswers() {
  submitting.value = true
  if (timer) { clearInterval(timer); timer = null }
  const ansList = questions.value.map(q => {
    let answer = answers[q.id] || ''
    if (q.type === 'MULTI') answer = (multiAnswers[q.id] || []).join(',')
    return { questionId: q.id, answer }
  })
  const res: any = await submitExam(recordId.value, ansList)
  result.value = res.data
  submitted.value = true
  submitting.value = false
}

onMounted(async () => {
  loading.value = true
  const res: any = await getExamDetail(Number(route.params.id))
  exam.value = res.data.exam
  questions.value = res.data.questions || []
  loading.value = false
})

onUnmounted(() => { if (timer) clearInterval(timer) })
</script>
