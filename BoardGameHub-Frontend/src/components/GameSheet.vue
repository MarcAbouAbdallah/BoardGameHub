<script setup lang="ts">
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { defineProps } from "vue";

const props = defineProps({
  game: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <Sheet>
    <SheetTrigger class="p-0 border-0 h-fit rounded-xl">
      <slot></slot>
    </SheetTrigger>
    <SheetContent class="max-w-2xl">
      <SheetHeader class="flex items-center">
        <SheetTitle class="text-2xl">{{ props.game.name }}</SheetTitle>
      </SheetHeader>
      <div>
        <img
          src="https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzJ4bjU2NWE5YTh1Y3Q1cTVmcHdmOHhrOWo0a3hvN2dwcnNncXhzZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/YORVoIzhBZZaHY7Jp2/giphy.gif"
          alt="Game Image"
          class="w-full object-cover"
        />
      </div>
      <SheetDescription class="text-center text-ellipsis font-bold text-md my-2">
        {{ props.game.description }}
      </SheetDescription>
    </SheetContent>
  </Sheet>
</template>
