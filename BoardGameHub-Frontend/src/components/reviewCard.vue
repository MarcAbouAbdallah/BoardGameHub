<script setup lang="ts">
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import rating from "./ui/rating/rating.vue";
import { defineProps } from "vue";

const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <Card class="w-full">
    <CardHeader>
      <CardTitle class="flex items-center justify-between">
        <span class="text-lg font-semibold">{{ props.review.reviewer }}</span>
        <rating :rating-value="props.review.reviewId.toString()" />
      </CardTitle>
      <CardContent class="p-0">
        <CardDescription class="text-sm text-gray-500">
          {{ props.review.reviewText }}
        </CardDescription>
      </CardContent>
    </CardHeader>
  </Card>
</template>
