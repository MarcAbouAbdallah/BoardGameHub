<script setup lang="ts">
import { defineProps } from "vue";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "./ui/badge";

const props = defineProps({
  game: {
    type: Object,
    required: true,
  },
  isBorrowed: {
    type: Boolean,
    required: false,
  },
});
</script>

<template>
  <Card>
    <CardHeader>
      <img
        :src="props.game.image"
        alt="Game Image"
      />
    </CardHeader>
    <CardContent>
      <Badge
        v-if="isBorrowed"
        class="absolute top-2 right-2"
        >Borrowed</Badge
      >
      <CardTitle class="text-lg text-center mb-1 font-bold">{{
        props.game.name
      }}</CardTitle>
      <CardDescription class="text-sm text-gray-500">{{
        props.game.description
      }}</CardDescription>
    </CardContent>
  </Card>
</template>
