import Vue from 'vue'
import Router from 'vue-router'
import Home from '../views/Home.vue'
import Apply from '../views/Apply.vue'
import List from '../views/List.vue'
import Audit from '../views/Audit.vue'
import Warehouse from '../views/Warehouse.vue'
import Inspection from '../views/Inspection.vue'
import Refund from '../views/Refund.vue'
import Exchange from '../views/Exchange.vue'
import Shipment from '../views/Shipment.vue'
import Repair from '../views/Repair.vue'
import Statistics from '../views/Statistics.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/apply',
      name: 'Apply',
      component: Apply
    },
    {
      path: '/list',
      name: 'List',
      component: List
    },
    {
      path: '/audit',
      name: 'Audit',
      component: Audit
    },
    {
      path: '/warehouse',
      name: 'Warehouse',
      component: Warehouse
    },
    {
      path: '/inspection',
      name: 'Inspection',
      component: Inspection
    },
    {
      path: '/refund',
      name: 'Refund',
      component: Refund
    },
    {
      path: '/exchange',
      name: 'Exchange',
      component: Exchange
    },
    {
      path: '/shipment',
      name: 'Shipment',
      component: Shipment
    },
    {
      path: '/repair',
      name: 'Repair',
      component: Repair
    },
    {
      path: '/statistics',
      name: 'Statistics',
      component: Statistics
    }
  ]
})
