<script setup>
defineProps({ name: { type: String, required: true }, size: { type: Number, default: 20 } })
</script>

<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.8"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
  >
    <template v-if="name === 'arrow'">
      <path d="m9 18 6-6-6-6" /><path d="M5 12h10" />
    </template>
    <template v-else-if="name === 'back'">
      <path d="m15 18-6-6 6-6" /><path d="M19 12H9" />
    </template>
    <template v-else-if="name === 'send'">
      <path d="m22 2-7 20-4-9-9-4Z" /><path d="M22 2 11 13" />
    </template>
    <template v-else-if="name === 'heart'">
      <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.7-7.5 1.1-1.1a5.5 5.5 0 0 0 0-7.8Z" />
    </template>
    <template v-else-if="name === 'spark'">
      <path d="m12 3-1.5 4.5L6 9l4.5 1.5L12 15l1.5-4.5L18 9l-4.5-1.5Z" />
      <path d="m19 15-.8 2.2L16 18l2.2.8L19 21l.8-2.2L22 18l-2.2-.8Z" />
    </template>
    <template v-else-if="name === 'copy'">
      <rect x="9" y="9" width="11" height="11" rx="2" /><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
    </template>
    <template v-else-if="name === 'stop'">
      <rect x="6" y="6" width="12" height="12" rx="2" />
    </template>
    <template v-else-if="name === 'refresh'">
      <path d="M20 11a8.1 8.1 0 0 0-15.5-2M4 4v5h5" /><path d="M4 13a8.1 8.1 0 0 0 15.5 2M20 20v-5h-5" />
    </template>
  </svg>
</template>
