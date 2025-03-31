<script setup lang="ts">
import { Pen, UserCircle2Icon } from "lucide-vue-next";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { gameCopies } from "@/data/sampleGames";
import { defineProps } from "vue";
import BorrowReqModal from "./popups/BorrowReqModal.vue";
import { Button } from "@/components/ui/button";

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
    <SheetContent class="w-[500px] sm:max-w-none">
      <SheetHeader class="flex items-center mt-7">
        <SheetTitle class="text-2xl">{{ props.game.name }}</SheetTitle>
      </SheetHeader>
      <div class="flex flex-col justify-center items-center mt-6">
        <img
          src="https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzJ4bjU2NWE5YTh1Y3Q1cTVmcHdmOHhrOWo0a3hvN2dwcnNncXhzZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/YORVoIzhBZZaHY7Jp2/giphy.gif"
          alt="Game Image"
          class="w-72 object-cover"
        />
      </div>
      <SheetDescription class="text-center text-ellipsis font-bold text-md my-2">
        {{ props.game.description }}
      </SheetDescription>
      <div class="flex flex-col items-start mt-4">
        <div class="text-lg font-bold">Players who Own the Game:</div>
        <div class="overflow-scroll max-h-[400px] w-full">
          <div v-for="copy in gameCopies" :key="copy.id" class="flex justify-between mt-5 gap-3">
            <div class="flex items-center gap-2">
              <UserCircle2Icon class="h-8 w-8 text-gray-500" />
              <span class="text-lg font-semibold">{{ copy.name }}</span>
            </div>
            <BorrowReqModal />
          </div>
        </div>
      </div>

      <Button variant="outline" class="absolute top-2 left-2 border-1 border-black">
        <Pen class="h-4 w-4" />
        Edit Game
      </Button>
    </SheetContent>
  </Sheet>
</template>
