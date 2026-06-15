<template>
  <div class="page-container">
    <div class="page-header">
      <h2>课程广场</h2>
    </div>
    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6">
        <el-select v-model="categoryId" placeholder="分类筛选" clearable style="width:100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-col>
      <el-col :span="8">
        <el-input v-model="keyword" placeholder="搜索课程" clearable @keyup.enter="fetchData" />
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </el-col>
    </el-row>
    <div class="card-grid">
      <el-card v-for="c in list" :key="c.id" shadow="hover" class="course-card" @click="$router.push(`/courses/${c.id}`)">
        <img :src="c.cover || 'https://placeholder.pics/svg/300x180'" style="width:100%;height:140px;object-fit:cover;border-radius:4px" />
        <h3 style="margin:12px 0 8px">{{ c.title }}</h3>
        <p style="color:#909399;font-size:13px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">{{ c.description }}</p>
        <div style="display:flex;justify-content:space-between;align-items:center;margin-top:12px">
          <span style="color:#409EFF;font-weight:bold">¥{{ c.price || '免费' }}</span>
          <span style="color:#909399;font-size:12px">{{ c.viewCount }} 次浏览</span>
        </div>
      </el-card>
    </div>
    <el-pagination
      v-if="total > 0"
      style="margin-top:20px;justify-content:center"
      v-model:current-page="page"
      :page-size="size"
      :total="total"
      layout="prev, pager, next"
      @current-change="fetchData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCourses, getCategories } from '@/api/course'

const list = ref<any[]>([])
const categories = ref<any[]>([])
const page = ref(1)
const size = ref(9)
const total = ref(0)
const keyword = ref('')
const categoryId = ref<number | null>(null)

async function fetchData() {
  const res: any = await getCourses({ page: page.value, size: size.value, keyword: keyword.value, categoryId: categoryId.value })
  list.value = res.data.records
  total.value = res.data.total
}

onMounted(async () => {
  fetchData()
  const catRes: any = await getCategories()
  categories.value = catRes.data || []
})
</script>

<style scoped>
.course-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.course-card:hover {
  transform: translateY(-4px);
}
</style>
