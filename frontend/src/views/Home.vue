<template>
  <div class="home">
    <h2>欢迎使用售后退换修管理系统</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>系统功能</span>
          </div>
          <div v-for="item in features" :key="item.name">
            <el-icon :type="item.icon"></el-icon>
            <span style="margin-left: 10px;">{{ item.name }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <div slot="header">使用说明</div>
          <el-timeline>
            <el-timeline-item timestamp="步骤1" placement="top">用户提交售后申请</el-timeline-item>
            <el-timeline-item timestamp="步骤2" placement="top">管理员审核售后单</el-timeline-item>
            <el-timeline-item timestamp="步骤3" placement="top">仓库确认收货</el-timeline-item>
            <el-timeline-item timestamp="步骤4" placement="top">质检人员提交检测结果</el-timeline-item>
            <el-timeline-item timestamp="步骤5" placement="top">根据类型处理：退款/换货/维修</el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      features: [
        { icon: 'el-icon-edit', name: '售后申请' },
        { icon: 'el-icon-s-check', name: '审核管理' },
        { icon: 'el-icon-office-building', name: '仓库收货' },
        { icon: 'el-icon-search', name: '质检处理' },
        { icon: 'el-icon-money', name: '退款处理' },
        { icon: 'el-icon-s-tools', name: '维修管理' },
        { icon: 'el-icon-data-line', name: '统计报表' }
      ]
    }
  }
}
</script>

<style scoped>
.home h2 {
  margin-bottom: 20px;
  color: #409EFF;
}
.box-card div {
  margin: 10px 0;
}
</style>
