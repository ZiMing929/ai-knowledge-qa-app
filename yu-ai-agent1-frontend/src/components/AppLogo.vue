<script setup>
defineProps({ compact: Boolean })
</script>

<template>
  <RouterLink class="brand" to="/" aria-label="返回 Yu AI Nexus 首页">
    <span class="brand__mark" aria-hidden="true">
      <svg viewBox="0 0 40 40" role="img">
        <path d="M7 13.5 20 6l13 7.5v13L20 34 7 26.5z" />
        <circle cx="15" cy="19" r="2.3" />
        <circle cx="25" cy="19" r="2.3" />
        <path d="M14 25c3.5 2.6 8.5 2.6 12 0" />
      </svg>
    </span>
    <span v-if="!compact" class="brand__copy">
      <strong>YU AI</strong>
      <small>NEXUS / 智能体中枢</small>
    </span>
  </RouterLink>
</template>
