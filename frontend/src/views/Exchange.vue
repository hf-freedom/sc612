<template>
  <div class="exchange">
    <h2>换货处理</h2>
    <el-card>
      <el-table :data="exchangeList" border stripe>
        <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
        <el-table-column prop="orderId" label="订单号" width="120"></el-table-column>
        <el-table-column prop="productName" label="原商品" width="150"></el-table-column>
        <el-table-column prop="newProductId" label="新商品ID" width="120">
          <template slot-scope="scope">
            <el-tag type="success">{{ scope.row.newProductId }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="换货原因" width="150"></el-table-column>
        <el-table-column prop="currentNode" label="当前节点" width="120">
          <template slot-scope="scope">
            <el-tag :type="getNodeTagType(scope.row.currentNode)">{{ scope.row.currentNode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button type="success" size="small" @click="confirmExchange(scope.row)" v-if="scope.row.currentNode === '待发货'">确认发货</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="exchangeList.length === 0" description="暂无换货任务"></el-empty>
    </el-card>

    <el-dialog title="换货详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="售后单号">{{ currentOrder.afterSaleId }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentOrder.orderId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentOrder.productName }}</el-descriptions-item>
        <el-descriptions-item label="换货原因">{{ currentOrder.reason }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ currentOrder.description }}</el-descriptions-item>
        <el-descriptions-item label="新商品ID">{{ currentOrder.newProductId }}</el-descriptions-item>
        <el-descriptions-item label="当前节点">{{ currentOrder.currentNode }}</el-descriptions-item>
      </el-descriptions>
      <span slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="confirmExchange(currentOrder)" v-if="currentOrder && currentOrder.currentNode === '待发货'">确认发货</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Exchange',
  data() {
    return {
      exchangeList: [],
      shipmentTasks: [],
      detailDialogVisible: false,
      currentOrder: null
    }
  },
  mounted() {
    this.loadData()
    this.loadShipmentTasks()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/list')
        .then(res => {
          this.exchangeList = res.data.filter(item => item.type === '换货')
        })
    },
    getNodeTagType(node) {
      const types = {
        '待审核': 'warning',
        '待用户寄回': 'info',
        '待质检': 'primary',
        '待发货': 'success',
        '已完成': 'success',
        '已结束': 'info'
      }
      return types[node] || ''
    },
    viewDetail(row) {
      this.currentOrder = row
      this.detailDialogVisible = true
    },
    confirmExchange(row) {
      this.$prompt('请输入物流单号', '确认发货', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '物流单号不能为空'
      }).then(({ value }) => {
        const task = this.shipmentTasks.find(t => t.afterSaleId === row.afterSaleId)
        if (task) {
          this.$axios.post('/api/aftersale/confirm-shipment/' + task.taskId, {
            logisticsCompany: '默认物流',
            trackingNumber: value
          }).then(res => {
            if (res.data.success) {
              this.$message.success('发货确认成功，换货流程已完成')
              this.loadData()
              this.detailDialogVisible = false
            } else {
              this.$message.error(res.data.message)
            }
          })
        }
      }).catch(() => {})
    },
    loadShipmentTasks() {
      this.$axios.get('/api/aftersale/shipments')
        .then(res => {
          this.shipmentTasks = res.data
        })
    }
  }
}
</script>

<style scoped>
.exchange h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
</style>
