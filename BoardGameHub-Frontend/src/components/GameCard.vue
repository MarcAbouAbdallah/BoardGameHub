<script setup lang="ts">
import { defineProps } from "vue";
import { Edit } from "lucide-vue-next";
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
  <Card
    class="max-w-[18rem] py-6 hover:cursor-pointer hover:bg-gray-100 transition-all duration-200 relative"
  >
    <CardHeader class="p-2">
      <img
        src="https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzJ4bjU2NWE5YTh1Y3Q1cTVmcHdmOHhrOWo0a3hvN2dwcnNncXhzZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/YORVoIzhBZZaHY7Jp2/giphy.gif"
        alt="Game Image"
        class="w-full object-cover"
      />
    </CardHeader>
    <CardContent class="p-2">
      <div class="flex gap-2 flex-wrap ml-4 mb-8">
        <Badge v-if="isBorrowed">Borrowed</Badge>
        <Badge>Min Players: {{ props.game.minPlayers }}</Badge>
        <Badge>Max Players: {{ props.game.maxPlayers }}</Badge>
      </div>

      <CardTitle class="text-lg text-center mb-1 font-bold">{{
        props.game.name
      }}</CardTitle>
      <CardDescription class="text-sm text-gray-500">{{
        props.game.description
      }}</CardDescription>
    </CardContent>
    <Edit class="absolute top-3 right-3 w-6 h-6 hover:text-gray-400" />
  </Card>
</template>
