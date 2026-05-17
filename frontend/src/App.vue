<template>
  <div id="app">
    <el-container>
      <el-header style="background: #409EFF; color: white; font-size: 24px; line-height: 60px;">
        售后退换修管理系统
      </el-header>
      <el-container>
        <el-aside width="200px" style="background: #f5f5f5;">
          <el-menu router default-active="0" class="el-menu-vertical">
            <el-menu-item index="/">
              <i class="el-icon-s-home"></i>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/apply">
              <i class="el-icon-edit"></i>
              <span>售后申请</span>
            </el-menu-item>
            <el-menu-item index="/list">
              <i class="el-icon-document"></i>
              <span>售后列表</span>
            </el-menu-item>
            <el-menu-item index="/audit">
              <i class="el-icon-s-check"></i>
              <span>审核管理</span>
            </el-menu-item>
            <el-menu-item index="/warehouse">
              <i class="el-icon-office-building"></i>
              <span>仓库收货</span>
            </el-menu-item>
            <el-menu-item index="/inspection">
              <i class="el-icon-search"></i>
              <span>质检处理</span>
            </el-menu-item>
            <el-menu-item index="/refund">
              <i class="el-icon-money"></i>
              <span>退款处理</span>
            </el-menu-item>
            <el-menu-item index="/exchange">
              <i class="el-icon-s-goods"></i>
              <span>换货处理</span>
            </el-menu-item>
            <el-menu-item index="/shipment">
              <i class="el-icon-truck"></i>
              <span>发货任务</span>
            </el-menu-item>
            <el-menu-item index="/repair">
              <i class="el-icon-s-tools"></i>
              <span>维修管理</span>
            </el-menu-item>
            <el-menu-item index="/statistics">
              <i class="el-icon-data-line"></i>
              <span>统计报表</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.el-header {
  padding: 0 20px;
}
.el-main {
  padding: 20px;
}
</style>
