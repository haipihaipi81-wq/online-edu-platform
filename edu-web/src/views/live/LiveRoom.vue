<template>
  <div class="page-container" v-loading="loading">
    <div v-if="room" style="display:flex;gap:20px;height:calc(100vh - 140px)">
      <!-- Video Area -->
      <div style="flex:1;background:#000;border-radius:8px;display:flex;align-items:center;justify-content:center;color:#fff;flex-direction:column">
        <el-icon :size="64"><VideoCamera /></el-icon>
        <p style="margin-top:16px">{{ room.status === 'LIVING' ? '直播进行中' : room.status === 'WAITING' ? '等待开播' : '直播已结束' }}</p>
        <div style="margin-top:16px" v-if="userStore.isTeacher && room.status === 'WAITING'">
          <el-button type="success" @click="startLive">开始直播</el-button>
        </div>
        <div v-if="userStore.isTeacher && room.status === 'LIVING'">
          <el-button type="danger" @click="endLive">结束直播</el-button>
        </div>
        <p style="margin-top:12px;color:#909399;font-size:13px">推流地址: {{ room.pushUrl || '配置后可推流' }}</p>
      </div>

      <!-- Chat Area -->
      <div style="width:340px;background:#fff;border-radius:8px;display:flex;flex-direction:column">
        <div style="padding:12px;border-bottom:1px solid #ebeef5;font-weight:bold">直播聊天</div>
        <div style="flex:1;overflow-y:auto;padding:12px" ref="chatBox">
          <div v-for="msg in chats" :key="msg.id || Math.random()" style="margin-bottom:10px">
            <span style="color:#409EFF;font-weight:bold">{{ msg.username }}: </span>
            <span>{{ msg.content }}</span>
          </div>
        </div>
        <div style="padding:12px;border-top:1px solid #ebeef5;display:flex;gap:8px">
          <el-input v-model="chatText" placeholder="发送消息..." @keyup.enter="sendChat" />
          <el-button type="primary" :disabled="!connected" @click="sendChat">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getLiveRoom, getChatHistory, startLive, endLive } from '@/api/live'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(false)
const room = ref<any>(null)
const chats = ref<any[]>([])
const chatText = ref('')
const chatBox = ref<HTMLElement>()
const connected = ref(false)
let ws: WebSocket | null = null

function connectWS(roomId: number) {
  const token = localStorage.getItem('accessToken')
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const wsUrl = `${protocol}//${window.location.host}/ws/live/${roomId}?token=${token}`
  ws = new WebSocket(wsUrl)

  ws.onopen = () => { connected.value = true }
  ws.onmessage = (e) => {
    try {
      const msg = JSON.parse(e.data)
      if (msg.type === 'chat') {
        chats.value.push(msg)
        nextTick(() => {
          if (chatBox.value) chatBox.value.scrollTop = chatBox.value.scrollHeight
        })
      }
    } catch { /* ignore */ }
  }
  ws.onclose = () => { connected.value = false }
}

function sendChat() {
  if (!chatText.value.trim() || !ws) return
  ws.send(JSON.stringify({ content: chatText.value }))
  chatText.value = ''
}

async function startLiveFn() {
  await startLive(Number(route.params.id))
  room.value.status = 'LIVING'
}

async function endLiveFn() {
  await endLive(Number(route.params.id))
  room.value.status = 'ENDED'
}

onMounted(async () => {
  loading.value = true
  const res: any = await getLiveRoom(Number(route.params.id))
  room.value = res.data
  const chatRes: any = await getChatHistory(Number(route.params.id), 50)
  chats.value = (chatRes.data || []).reverse()
  connectWS(Number(route.params.id))
  loading.value = false
})

onUnmounted(() => { ws?.close() })
</script>
