<template>
  <div class="shipment">
    <h2>发货任务管理</h2>
    <el-card>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待发货任务" name="pending">
          <el-table :data="pendingList" border stripe>
            <el-table-column prop="taskId" label="任务单号" width="180"></el-table-column>
            <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
            <el-table-column prop="type" label="任务类型" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.type === '换货发货' ? 'success' : 'primary'">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column label="操作" width="200">
              <template slot-scope="scope">
                <el-button type="primary" size="small" @click="viewTask(scope.row)">查看详情</el-button>
                <el-button type="success" size="small" @click="confirmShip(scope.row)">确认发货</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="pendingList.length === 0" description="暂无待发货任务"></el-empty>
        </el-tab-pane>
        <el-tab-pane label="已完成发货" name="completed">
          <el-table :data="completedList" border stripe>
            <el-table-column prop="taskId" label="任务单号" width="180"></el-table-column>
            <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
            <el-table-column prop="type" label="任务类型" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.type === '换货发货' ? 'success' : 'primary'">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="收货地址" width="200"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
            <el-table-column prop="receiveTime" label="发货时间" width="180"></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="info" size="small" @click="viewTask(scope.row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="completedList.length === 0" description="暂无已完成发货"></el-empty>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog title="发货任务详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentTask">
        <el-descriptions-item label="任务单号">{{ currentTask.taskId }}</el-descriptions-item>
        <el-descriptions-item label="售后单号">{{ currentTask.afterSaleId }}</el-descriptions-item>
        <el-descriptions-item label="任务类型">{{ currentTask.type }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ currentTask.address }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentTask.createTime }}</el-descriptions-item>
        <el-descriptions-item label="状态" v-if="currentTask.receiveTime">已发货</el-descriptions-item>
        <el-descriptions-item label="发货时间" v-if="currentTask.receiveTime">{{ currentTask.receiveTime }}</el-descriptions-item>
      </el-descriptions>
      <el-form v-if="currentTask && !currentTask.receiveTime" label-width="100px" style="margin-top: 20px;">
        <el-form-item label="物流公司">
          <el-input v-model="logisticsCompany" placeholder="请输入物流公司"></el-input>
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="trackingNumber" placeholder="请输入物流单号"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="confirmShip(currentTask)" v-if="currentTask && !currentTask.receiveTime">确认发货</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Shipment',
  data() {
    return {
      activeTab: 'pending',
      pendingList: [],
      completedList: [],
      detailDialogVisible: false,
      currentTask: null,
      logisticsCompany: '',
      trackingNumber: ''
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/shipments')
        .then(res => {
          const allTasks = res.data || []
          this.pendingList = allTasks.filter(t => !t.receiveTime)
          this.completedList = allTasks.filter(t => t.receiveTime)
        })
    },
    handleTabChange() {
      this.loadData()
    },
    viewTask(row) {
      this.currentTask = row
      this.logisticsCompany = ''
      this.trackingNumber = ''
      this.detailDialogVisible = true
    },
    confirmShip(row) {
      if (!this.logisticsCompany || !this.trackingNumber) {
        this.$message.warning('请填写物流公司和物流单号')
        return
      }
      this.$confirm('确认已发货？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.post('/api/aftersale/confirm-shipment/' + row.taskId, {
          logisticsCompany: this.logisticsCompany,
          trackingNumber: this.trackingNumber
        }).then(res => {
          if (res.data.success) {
            this.$message.success('发货确认成功')
            this.loadData()
            this.detailDialogVisible = false
          } else {
            this.$message.error(res.data.message)
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.shipment h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
</style>
