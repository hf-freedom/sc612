<template>
  <div class="repair">
    <h2>维修管理</h2>
    <el-card>
      <el-table :data="repairList" border stripe>
        <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
        <el-table-column prop="orderId" label="订单号" width="120"></el-table-column>
        <el-table-column prop="productName" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="repairPerson" label="维修人员" width="120">
          <template slot-scope="scope">
            <el-tag :type="getLevelTagType(scope.row.upgradeLevel)">{{ scope.row.repairPerson }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="upgradeLevel" label="升级级别" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.upgradeLevel && scope.row.upgradeLevel > 0" type="danger" size="small">
              Lv.{{ scope.row.upgradeLevel }}
            </el-tag>
            <span v-else class="normal-level">正常</span>
          </template>
        </el-table-column>
        <el-table-column prop="repairDeadline" label="维修期限" width="180">
          <template slot-scope="scope">
            <span :class="{ 'timeout': isTimeout(scope.row.repairDeadline) }">
              {{ scope.row.repairDeadline }}
            </span>
            <el-tag v-if="isTimeout(scope.row.repairDeadline)" type="danger" size="small" style="margin-left: 5px;">
              已超时
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="viewRecords(scope.row)">升级记录</el-button>
            <el-button type="warning" size="small" @click="triggerUpgrade(scope.row.afterSaleId)">手动升级</el-button>
            <el-button type="success" size="small" @click="completeRepair(scope.row.afterSaleId)">完成维修</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="repairList.length === 0" description="暂无维修任务"></el-empty>
    </el-card>

    <el-dialog title="升级记录" :visible.sync="recordDialogVisible" width="700px">
      <el-descriptions :column="2" border v-if="currentRepair">
        <el-descriptions-item label="售后单号">{{ currentRepair.afterSaleId }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentRepair.productName }}</el-descriptions-item>
        <el-descriptions-item label="当前维修人员">{{ currentRepair.repairPerson }}</el-descriptions-item>
        <el-descriptions-item label="当前升级级别">
          <el-tag v-if="currentRepair.upgradeLevel && currentRepair.upgradeLevel > 0" type="danger">
            Lv.{{ currentRepair.upgradeLevel }}
          </el-tag>
          <span v-else>正常</span>
        </el-descriptions-item>
      </el-descriptions>
      
      <h4 style="margin: 20px 0 10px 0;">升级历史</h4>
      <el-table :data="upgradeRecords" border stripe>
        <el-table-column prop="upgradeLevel" label="升级级别" width="100">
          <template slot-scope="scope">
            <el-tag type="danger" size="small">Lv.{{ scope.row.upgradeLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="oldRepairPerson" label="原负责人" width="120"></el-table-column>
        <el-table-column prop="newRepairPerson" label="新负责人" width="120"></el-table-column>
        <el-table-column prop="reason" label="升级原因" width="150"></el-table-column>
        <el-table-column prop="upgradeTime" label="升级时间" width="180"></el-table-column>
      </el-table>
      <el-empty v-if="upgradeRecords.length === 0" description="暂无升级记录"></el-empty>

      <span slot="footer">
        <el-button @click="recordDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Repair',
  data() {
    return {
      repairList: [],
      recordDialogVisible: false,
      currentRepair: null,
      upgradeRecords: []
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/aftersale/list')
        .then(res => {
          this.repairList = res.data.filter(item => item.currentNode === '维修中')
        })
    },
    getLevelTagType(level) {
      if (!level || level === 0) return 'success'
      if (level === 1) return 'warning'
      return 'danger'
    },
    isTimeout(deadline) {
      if (!deadline) return false
      return new Date(deadline) < new Date()
    },
    viewRecords(row) {
      this.currentRepair = row
      this.$axios.get('/api/aftersale/upgrade-records/' + row.afterSaleId)
        .then(res => {
          this.upgradeRecords = res.data
          this.recordDialogVisible = true
        })
    },
    triggerUpgrade(id) {
      this.$confirm('确认要手动升级负责人吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.post('/api/aftersale/trigger-upgrade/' + id)
          .then(res => {
            if (res.data.success) {
              this.$message.success('升级成功')
              this.loadData()
            } else {
              this.$message.error(res.data.message)
            }
          })
      }).catch(() => {})
    },
    completeRepair(id) {
      this.$axios.post('/api/aftersale/complete-repair/' + id)
        .then(res => {
          if (res.data.success) {
            this.$message.success(res.data.message)
            this.loadData()
          } else {
            this.$message.error(res.data.message)
          }
        })
    }
  }
}
</script>

<style scoped>
.repair h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
.timeout {
  color: #F56C6C;
  font-weight: bold;
}
.normal-level {
  color: #67C23A;
  font-weight: bold;
}
</style>
