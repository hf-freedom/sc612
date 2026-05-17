<template>
  <div class="inspection">
    <h2>质检处理</h2>
    <el-card>
      <el-table :data="inspectionList" border stripe>
        <el-table-column prop="afterSaleId" label="售后单号" width="150"></el-table-column>
        <el-table-column prop="orderId" label="订单号" width="120"></el-table-column>
        <el-table-column prop="productName" label="商品名称" width="150"></el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.type)">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="openInspectionDialog(scope.row.afterSaleId)">提交检测结果</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="质检信息" :visible.sync="inspectionDialogVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="质检人员">
          <el-input v-model="inspector"></el-input>
        </el-form-item>
        <el-form-item label="检测结果">
          <el-input type="textarea" v-model="inspectionResult" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="inspectionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInspection">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Inspection',
  data() {
    return {
      inspectionList: [],
      inspectionDialogVisible: false,
      inspector: '',
      inspectionResult: '',
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
          this.inspectionList = res.data.filter(item => item.currentNode === '待质检')
        })
    },
    openInspectionDialog(id) {
      this.currentAfterSaleId = id
      this.inspectionDialogVisible = true
    },
    submitInspection() {
      this.$axios.post('/api/aftersale/inspection/' + this.currentAfterSaleId, {
        inspector: this.inspector,
        result: this.inspectionResult
      })
        .then(res => {
          if (res.data.success) {
            this.$message.success(res.data.message)
            this.inspectionDialogVisible = false
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
.inspection h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
</style>
