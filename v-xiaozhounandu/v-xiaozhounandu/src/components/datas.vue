<template>
  <div class="">
    姓名：<input v-model="userName" /> {{ userName }} <br />

    薪水：<input type="number" v-model="salary" /> {{ salary }} <br />

    <button @click="addSalary">点击</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 响应式数据
const userName = ref('roy')
const salary = ref(15000)

// 方法
function addSalary() {
  salary.value += 1000
}
</script>