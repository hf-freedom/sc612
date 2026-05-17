<template>
  <div class="apply">
    <h2>售后申请</h2>
    <el-card>
      <el-form :model="form" label-width="120px" ref="form">
        <el-form-item label="选择订单">
          <el-select v-model="form.orderId" placeholder="请选择订单" style="width: 100%;" @change="onOrderChange">
            <el-option v-for="order in orders" :key="order.orderId" :label="getOrderLabel(order)" :value="order.orderId" :disabled="order.isExpired">
            </el-option>
          </el-select>
          <div v-if="selectedOrder" class="order-status" :class="{ 'expired': selectedOrder.isExpired }">
            <i :class="selectedOrder.isExpired ? 'el-icon-error' : 'el-icon-success'"></i>
            <span v-if="selectedOrder.isExpired">该订单已超过30天售后期，无法申请售后</span>
            <span v-else>售后期剩余 {{ selectedOrder.remainingDays }} 天，可正常申请售后</span>
          </div>
        </el-form-item>
        <el-form-item label="售后类型" v-if="selectedOrder && !selectedOrder.isExpired && selectedProduct">
          <el-radio-group v-model="form.type">
            <el-radio label="退款" :disabled="!selectedProduct.supportReturn">
              退款
              <span v-if="!selectedProduct.supportReturn" class="disabled-tip">（该商品不支持）</span>
            </el-radio>
            <el-radio label="换货" :disabled="!selectedProduct.supportExchange">
              换货
              <span v-if="!selectedProduct.supportExchange" class="disabled-tip">（该商品不支持）</span>
            </el-radio>
            <el-radio label="维修" :disabled="!selectedProduct.supportRepair">
              维修
              <span v-if="!selectedProduct.supportRepair" class="disabled-tip">（该商品不支持）</span>
            </el-radio>
          </el-radio-group>
          <div class="type-tip" v-if="selectedProduct">
            <i class="el-icon-info"></i>
            <span>该商品支持：{{ getSupportTypes() }}</span>
          </div>
        </el-form-item>
        <el-form-item label="售后原因" v-if="selectedOrder && !selectedOrder.isExpired">
          <el-input v-model="form.reason" placeholder="请输入售后原因"></el-input>
        </el-form-item>
        <el-form-item label="详细描述" v-if="selectedOrder && !selectedOrder.isExpired">
          <el-input type="textarea" v-model="form.description" :rows="4" placeholder="请详细描述问题"></el-input>
        </el-form-item>
        <el-form-item v-if="selectedOrder && !selectedOrder.isExpired">
          <el-button type="primary" @click="submitForm">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Apply',
  data() {
    return {
      form: {
        orderId: '',
        type: '退款',
        reason: '',
        description: ''
      },
      orders: [],
      products: [],
      selectedOrder: null,
      selectedProduct: null
    }
  },
  mounted() {
    this.loadOrders()
    this.loadProducts()
  },
  methods: {
    loadOrders() {
      this.$axios.get('/api/aftersale/orders')
        .then(res => {
          this.orders = res.data
        })
    },
    loadProducts() {
      this.$axios.get('/api/aftersale/products')
        .then(res => {
          this.products = res.data
        })
    },
    getOrderLabel(order) {
      let label = `${order.orderId} - ${order.productName}`
      if (order.isExpired) {
        label += ' (已超期)'
      } else {
        label += ` (剩余${order.remainingDays}天)`
      }
      return label
    },
    onOrderChange(orderId) {
      this.selectedOrder = this.orders.find(o => o.orderId === orderId)
      if (this.selectedOrder) {
        this.selectedProduct = this.products.find(p => p.productId === this.selectedOrder.productId)
        this.autoSelectFirstSupportedType()
      }
      if (this.selectedOrder && this.selectedOrder.isExpired) {
        this.$message.error('该订单已超过30天售后期，无法申请售后')
      }
    },
    autoSelectFirstSupportedType() {
      if (!this.selectedProduct) return
      const types = [
        { name: '退款', support: this.selectedProduct.supportReturn },
        { name: '换货', support: this.selectedProduct.supportExchange },
        { name: '维修', support: this.selectedProduct.supportRepair }
      ]
      const firstSupported = types.find(t => t.support)
      if (firstSupported) {
        this.form.type = firstSupported.name
      }
    },
    getSupportTypes() {
      if (!this.selectedProduct) return ''
      const types = []
      if (this.selectedProduct.supportReturn) types.push('退款')
      if (this.selectedProduct.supportExchange) types.push('换货')
      if (this.selectedProduct.supportRepair) types.push('维修')
      return types.join('、') || '无'
    },
    submitForm() {
      if (this.selectedOrder && this.selectedOrder.isExpired) {
        this.$message.error('该订单已超过30天售后期，无法申请售后')
        return
      }
      if (!this.checkTypeSupport()) {
        this.$message.error('该商品不支持此售后类型，请重新选择')
        return
      }
      this.$axios.post('/api/aftersale/create', this.form)
        .then(res => {
          if (res.data.success) {
            this.$message.success(res.data.message)
            this.resetForm()
          } else {
            this.$message.error(res.data.message)
          }
        })
    },
    checkTypeSupport() {
      if (!this.selectedProduct) return false
      const typeMap = {
        '退款': 'supportReturn',
        '换货': 'supportExchange',
        '维修': 'supportRepair'
      }
      return this.selectedProduct[typeMap[this.form.type]]
    },
    resetForm() {
      this.$refs.form.resetFields()
      this.selectedOrder = null
      this.selectedProduct = null
    }
  }
}
</script>

<style scoped>
.apply h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
.order-status {
  margin-top: 10px;
  padding: 10px 15px;
  border-radius: 4px;
  background-color: #f0f9eb;
  color: #67c23a;
  display: flex;
  align-items: center;
}
.order-status i {
  margin-right: 8px;
  font-size: 18px;
}
.order-status.expired {
  background-color: #fef0f0;
  color: #f56c6c;
}
.disabled-tip {
  color: #c0c4cc;
  font-size: 12px;
  margin-left: 4px;
}
.type-tip {
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #f4f4f5;
  border-radius: 4px;
  color: #909399;
  font-size: 13px;
  display: flex;
  align-items: center;
}
.type-tip i {
  margin-right: 6px;
}
</style>
