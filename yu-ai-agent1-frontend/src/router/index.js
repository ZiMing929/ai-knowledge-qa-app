import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { title: 'Yu AI Nexus' },
    },
    {
      path: '/love',
      name: 'love',
      component: () => import('../views/LoveChatView.vue'),
      meta: { title: 'AI 恋爱大师 · Yu AI Nexus' },
    },
    {
      path: '/manus',
      name: 'manus',
      component: () => import('../views/ManusChatView.vue'),
      meta: { title: 'AI 超级智能体 · Yu AI Nexus' },
    },
  ],
  scrollBehavior: () => ({ top: 0 }),
})

router.afterEach((to) => {
  document.title = to.meta.title ?? 'Yu AI Nexus'
})

export default router
