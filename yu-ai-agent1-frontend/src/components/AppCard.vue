<script setup>
import IconBase from './IconBase.vue'

defineProps({
  to: { type: String, required: true },
  variant: { type: String, required: true },
  eyebrow: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, required: true },
  features: { type: Array, default: () => [] },
})
</script>

<template>
  <RouterLink class="app-card" :class="`app-card--${variant}`" :to="to">
    <div class="app-card__topline">
      <span class="app-card__number">{{ eyebrow }}</span>
      <span class="app-card__status"><i /> 服务就绪</span>
    </div>
    <div class="app-card__orb" aria-hidden="true">
      <IconBase :name="variant === 'love' ? 'heart' : 'spark'" :size="38" />
    </div>
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
    <ul aria-label="应用能力">
      <li v-for="feature in features" :key="feature">{{ feature }}</li>
    </ul>
    <span class="app-card__action">
      开始对话 <IconBase name="arrow" />
    </span>
  </RouterLink>
</template>
