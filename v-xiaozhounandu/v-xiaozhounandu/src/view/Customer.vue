<template>
  <div style="padding: 20px; font-family: Arial, sans-serif;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2>客户管理系统</h2>
      <div>
        <span>当前用户：{{ currentUser?.username }} ({{ currentUser?.role }})</span>
        <button @click="handleLogout" style="margin-left: 20px; padding: 5px 15px; background-color: #dc3545; color: white; border: none; border-radius: 4px; cursor: pointer;">
          退出登录
        </button>
      </div>
    </div>

    <!-- 新增 / 编辑区 -->
    <div style="margin-bottom: 20px; border: 1px solid #ccc; padding: 15px; border-radius: 8px;">
      <h3>{{ editingId ? `编辑客户 ID: ${editingId}` : '新增客户' }}</h3>

      <div style="display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 10px;">
        <input v-model="form.name" placeholder="姓名" style="flex: 1; padding: 5px;" />
        <input v-model="form.phone" placeholder="电话" style="flex: 1; padding: 5px;" />
        <input v-model="form.email" placeholder="邮箱" style="flex: 1; padding: 5px;" />
        <input v-model="form.industry" placeholder="行业" style="flex: 1; padding: 5px;" />
        <input v-model="form.address" placeholder="地址" style="flex: 1; padding: 5px;" />
        <input v-model.number="form.ownerId" placeholder="归属人ID" style="flex: 1; padding: 5px;" />
      </div>

      <div>
        <button @click="saveCustomer" style="padding: 6px 12px; background-color:#007bff;color:white;border:none;border-radius:4px; cursor:pointer;">
          {{ editingId ? '更新客户' : '新增客户' }}
        </button>
        <button @click="resetForm" style="padding: 6px 12px; margin-left: 10px;">取消/清空</button>
      </div>
    </div>

    <!-- 客户列表 -->
    <div style="border: 1px solid #ccc; padding: 15px; border-radius: 8px;">
      <h3>客户列表</h3>

      <button @click="loadCustomers" style="margin-bottom:10px; padding:5px 10px;">刷新列表</button>

      <table border="1" cellpadding="8" cellspacing="0" width="100%" style="border-collapse: collapse;">
        <thead>
        <tr style="background-color:#007bff; color:white;">
          <th>ID</th>
          <th>姓名</th>
          <th>电话</th>
          <th>邮箱</th>
          <th>行业</th>
          <th>地址</th>
          <th>归属人ID</th>
          <th>创建时间</th>
          <th>更新时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="c in customers" :key="c.id">
          <td>{{ c.id }}</td>
          <td>{{ c.name }}</td>
          <td>{{ c.phone }}</td>
          <td>{{ c.email }}</td>
          <td>{{ c.industry }}</td>
          <td>{{ c.address }}</td>
          <td>{{ c.ownerId }}</td>
          <td>{{ c.createTime }}</td>
          <td>{{ c.updateTime }}</td>
          <td>
            <button @click="editCustomer(c)" style="margin-right:5px;">编辑</button>
            <button @click="deleteCustomer(c.id)">删除</button>
          </td>
        </tr>
        <tr v-if="customers.length === 0">
          <td colspan="10" style="text-align:center;">暂无客户数据</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { getCurrentUser, logout } from '../utils/auth';

export default {
  data() {
    return {
      customers: [],
      currentUser: null,
      form: {
        name: "",
        phone: "",
        email: "",
        industry: "",
        address: "",
        ownerId: null,
        createTime: "",
        updateTime: ""
      },
      editingId: null
    };
  },

  mounted() {
    // 检查用户是否已登录
    this.currentUser = getCurrentUser();
    if (!this.currentUser) {
      this.$router.push('/login');
      return;
    }

    // 设置axios默认headers
    const token = localStorage.getItem('token');
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }

    this.loadCustomers();
  },

  methods: {
    async loadCustomers() {
      try {
        const res = await axios.get("/api/customers/user");
        this.customers = res.data;
      } catch (e) {
        console.error("加载客户数据失败:", e);
        alert("加载客户数据失败，请检查后端是否启动！");
      }
    },

    resetForm() {
      this.form = {
        name: "",
        phone: "",
        email: "",
        industry: "",
        address: "",
        ownerId: null,
        createTime: "",
        updateTime: ""
      };
      this.editingId = null;
    },

    editCustomer(c) {
      this.form = { ...c };
      this.editingId = c.id;
    },

    async saveCustomer() {
      if (!this.form.name || !this.form.ownerId) {
        alert("姓名和归属人ID必填");
        return;
      }

      try {
        if (this.editingId) {
          await axios.post(`/api/customers/id2?id=${this.editingId}`, this.form);
          alert("客户更新成功");
        } else {
          await axios.post("/api/customers/insert", this.form);
          alert("客户新增成功");
        }
        this.resetForm();
        this.loadCustomers();
      } catch (e) {
        console.error("保存客户失败:", e);
        alert("保存客户失败：" + e.message);
      }
    },

    async deleteCustomer(id) {
      if (!confirm(`确认删除客户ID ${id}？`)) return;

      try {
        await axios.delete(`/api/customers/id1?id=${id}`);
        alert("删除成功");
        if (this.editingId === id) this.resetForm();
        this.loadCustomers();
      } catch (e) {
        console.error("删除客户失败:", e);
        alert("删除失败：" + e.message);
      }
    },

    handleLogout() {
      if (confirm('确认退出登录？')) {
        logout();
      }
    }
  }
};
</script>
