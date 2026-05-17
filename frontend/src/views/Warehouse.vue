<template>
  <div class="warehouse">
    <h2>仓库收货</h2>
    <el-card>
      <el-table :data="warehouseList" border stripe>
        <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
        <el-table-column prop="orderId" label="订单号" width="120"></el-table-column>
        <el-table-column prop="productName" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentNode" label="当前节点" width="120"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button type="success" size="small" @click="confirmReceive(scope.row.afterSaleId)">确认收货</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Warehouse',
  data() {
    return {
      warehouseList: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/list')
        .then(res => {
          this.warehouseList = res.data.filter(item => item.currentNode === '待用户寄回')
        })
    },
    confirmReceive(id) {
      this.$axios.post('/api/aftersale/receive/' + id)
        .then(res => {
          if (res.data.success) {
            this.$message.success(res.data.message)
            this.loadData()
          } else {
            this.$message.error(res.data.message)
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
.warehouse h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
</style>
