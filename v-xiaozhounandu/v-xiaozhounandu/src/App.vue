<template>
  <div>
    <h1>客户管理系统</h1>
    <Customer />
  </div>
</template>

<script>
import Customer from './view/Customer.vue'

export default {
  name: 'App',
  components: { Customer }
}
</script>
