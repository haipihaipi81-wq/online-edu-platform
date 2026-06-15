<template>
  <div class="page-container">
    <div class="page-header">
      <h2>直播中心</h2>
      <el-button v-if="userStore.isTeacher" type="primary" @click="showCreate">创建直播间</el-button>
    </div>
    <div class="card-grid">
      <el-card v-for="room in list" :key="room.id" shadow="hover" class="live-card" @click="$router.push(`/live/${room.id}`)">
        <div class="live-status">
          <el-tag :type="room.status === 'LIVING' ? 'danger' : room.status === 'WAITING' ? 'warning' : 'info'" effect="dark">
            {{ room.status === 'LIVING' ? '直播中' : room.status === 'WAITING' ? '即将开始' : '已结束' }}
          </el-tag>
        </div>
        <h3>{{ room.title }}</h3>
        <p style="color:#909399;font-size:13px;margin-top:8px">创建时间: {{ room.createdAt }}</p>
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" title="创建直播间" width="500px">
      <el-form :model="form" label-position="top">
        <el-form-item label="直播标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="关联课程ID(可选)">
          <el-input-number v-model="form.courseId" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createRoom">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getLiveRooms, createLiveRoom } from '@/api/live'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const list = ref<any[]>([])
const dialogVisible = ref(false)
const form = reactive({ title: '', courseId: null as number | null })

async function fetchData() {
  const res: any = await getLiveRooms(1, 20)
  list.value = res.data.records
}

function showCreate() { dialogVisible.value = true }
async function createRoom() {
  await createLiveRoom({ title: form.title, courseId: form.courseId })
  dialogVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.live-card { cursor: pointer; transition: transform 0.2s; }
.live-card:hover { transform: translateY(-4px); }
.live-status { text-align: right; margin-bottom: 8px; }
</style>
