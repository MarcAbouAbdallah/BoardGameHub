<script setup lang="ts">
import { Skeleton } from "./ui/skeleton";

const props = defineProps({
  isLoading: {
    type: Boolean,
    required: true,
  },
  error: {
    type: String,
    default: "",
  },
});
</script>

<template>
  <div class="border border-gray-200 rounded-md p-4">
    <!-- Simple loading state -->
    <div v-if="props.isLoading" class="w-full flex justify-center">
      <!-- If Skeleton is working, uncomment this: -->
      <Skeleton class="h-80 w-full" />
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="text-red-500 py-20 text-center">
      {{ error }}
    </div>

    <!-- Content state -->
    <div v-else>
      <slot></slot>
    </div>
  </div>
</template>
