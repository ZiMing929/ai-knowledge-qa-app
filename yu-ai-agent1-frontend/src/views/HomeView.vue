<script setup>
import AppCard from '../components/AppCard.vue'
import AppLogo from '../components/AppLogo.vue'
</script>

<template>
  <div class="home-page">
    <div class="ambient ambient--one" aria-hidden="true" />
    <div class="ambient ambient--two" aria-hidden="true" />
    <header class="home-header">
      <AppLogo />
      <div class="home-header__status"><i /> Local AI Network</div>
    </header>

    <main id="main-content" class="home-main">
      <section class="hero">
        <div class="hero__eyebrow"><span>02</span> INTELLIGENT EXPERIENCES</div>
        <h1>今天，想和哪一种<br /><em>智能</em>并肩？</h1>
        <p>一个懂你的关系顾问，一个替你执行复杂任务的超级智能体。选择频道，开始实时对话。</p>
        <div class="hero__signal" aria-hidden="true">
          <span v-for="index in 18" :key="index" :style="{ '--i': index }" />
        </div>
      </section>

      <section class="app-grid" aria-label="AI 应用列表">
        <AppCard
          to="/love"
          variant="love"
          eyebrow="CHANNEL 01"
          title="AI 恋爱大师"
          description="把难以开口的问题交给它。理解关系脉络，给出有边界、可执行的建议。"
          :features="['多轮上下文', '实时回答', '独立会话']"
        />
        <AppCard
          to="/manus"
          variant="manus"
          eyebrow="CHANNEL 02"
          title="AI 超级智能体"
          description="从问题到行动。自主拆解任务、调用工具，并把执行过程实时呈现给你。"
          :features="['任务规划', '工具调用', '过程可见']"
        />
      </section>
    </main>

    <footer class="home-footer">
      <span>YU AI AGENT PLATFORM</span>
      <span>SPRING BOOT · SSE STREAM</span>
    </footer>
  </div>
</template>
