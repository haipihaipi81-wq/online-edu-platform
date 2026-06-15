<template>
  <div class="page-container">
    <h2>欢迎回来，{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</h2>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>快捷入口</template>
          <el-space direction="vertical" :size="12">
            <el-button type="primary" @click="$router.push('/courses')">浏览课程</el-button>
            <el-button type="success" @click="$router.push('/live')">进入直播</el-button>
            <el-button type="warning" @click="$router.push('/exams')">参加考试</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>我的学习</template>
          <p>继续上次的学习进度</p>
          <el-button type="text" @click="$router.push('/courses')">查看我的课程</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>系统公告</template>
          <p>欢迎使用在线教育平台</p>
          <p style="color:#909399;font-size:12px">支持课程学习、直播教学、在线考试等功能</p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
</script>
