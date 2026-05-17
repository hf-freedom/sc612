<template>
  <div class="audit">
    <h2>审核管理</h2>
    <el-card>
      <el-table :data="pendingList" border stripe>
        <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
        <el-table-column prop="orderId" label="订单号" width="120"></el-table-column>
        <el-table-column prop="productName" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" width="150"></el-table-column>
        <el-table-column prop="description" label="描述" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="success" size="small" @click="approve(scope.row.afterSaleId)">通过</el-button>
            <el-button type="danger" size="small" @click="openRejectDialog(scope.row.afterSaleId)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="拒绝原因" :visible.sync="rejectDialogVisible" width="400px">
      <el-input type="textarea" v-model="rejectReason" :rows="4" placeholder="请输入拒绝原因"></el-input>
      <span slot="footer">
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Audit',
  data() {
    return {
      pendingList: [],
      rejectDialogVisible: false,
      rejectReason: '',
      currentAfterSaleId: ''
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/list')
        .then(res => {
          this.pendingList = res.data.filter(item => item.status === '待审核')
        })
    },
    approve(id) {
      this.$axios.post('/api/aftersale/approve/' + id)
        .then(res => {
          if (res.data.success) {
            this.$message.success(res.data.message)
            this.loadData()
          } else {
            this.$message.error(res.data.message)
          }
        })
    },
    openRejectDialog(id) {
      this.currentAfterSaleId = id
      this.rejectDialogVisible = true
    },
    confirmReject() {
      this.$axios.post('/api/aftersale/reject/' + this.currentAfterSaleId, { reason: this.rejectReason })
        .then(res => {
          if (res.data.success) {
            this.$message.success(res.data.message)
            this.rejectDialogVisible = false
            this.loadData()
          }
        })
    },
    getTypeColor(type) {
      const colors = { '退款': 'danger', '换货': 'warning', '维修': 'info' }
      return colors[type] || ''
    }
  }
}
</script>

<style scoped>
.audit h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
</style>
