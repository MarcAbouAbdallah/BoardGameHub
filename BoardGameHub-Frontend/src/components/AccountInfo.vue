<script setup lang="ts">
import { defineProps } from "vue";
import Badge from "./ui/badge/Badge.vue";

const props = defineProps<{
  username: string;
  email: string;
  isGameOwner: boolean;
}>();
</script>

<template>
  <div class="flex gap-10 items-center justify-between p-4">
    <p><strong>Username:</strong> {{ props.username }}</p>
    <p><strong>Email:</strong> {{ props.email }}</p>
    <Badge
      v-if="props.isGameOwner"
      class="mt-2"
    >
      Game Owner
    </Badge>
    <Badge
      v-else
      class="mt-2"
    >
      Player
    </Badge>
  </div>
</template>
