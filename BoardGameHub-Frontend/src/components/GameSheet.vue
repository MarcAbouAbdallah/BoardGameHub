<script setup lang="ts">
import { UserCircle2Icon } from "lucide-vue-next";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import reviewCard from "./reviewCard.vue";
import { gameCopies } from "@/data/sampleGames";
import { gameReviews } from "@/data/sampleGames";

import { defineProps } from "vue";
import BorrowReqModal from "./popups/BorrowReqModal.vue";
import CreateReviewModal from "./popups/CreateReviewModal.vue";
import UpdateGameModal from "./popups/update/UpdateGameModal.vue";

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
    <SheetContent class="w-[600px] sm:max-w-none">
      <SheetHeader class="flex items-center mt-7">
        <SheetTitle class="text-2xl">{{ props.game.name }}</SheetTitle>
      </SheetHeader>
      <div class="flex flex-col justify-center items-center mt-6">
        <img
          src="https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzJ4bjU2NWE5YTh1Y3Q1cTVmcHdmOHhrOWo0a3hvN2dwcnNncXhzZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/YORVoIzhBZZaHY7Jp2/giphy.gif"
          alt="Game Image"
          class="w-40 object-cover"
        />
      </div>
      <SheetDescription class="text-center text-ellipsis font-bold text-md my-2">
        {{ props.game.description }}
      </SheetDescription>
      <div class="flex flex-col items-start mt-4">
        <div class="text-lg font-bold">Players who Own the Game:</div>
        <div class="overflow-scroll max-h-[200px] w-full">
          <div v-for="copy in gameCopies" :key="copy.id" class="flex justify-between mt-5 gap-3">
            <div class="flex items-center gap-2">
              <UserCircle2Icon class="h-8 w-8 text-gray-500" />
              <span class="text-lg font-semibold">{{ copy.name }}</span>
            </div>
            <BorrowReqModal />
          </div>
        </div>
      </div>
      <div class="flex flex-col items-start mt-4">
        <div class="flex justify-between items-center w-full">
          <div class="text-lg font-bold">Game Reviews:</div>
          <CreateReviewModal class="w-fit" />
        </div>

        <div class="overflow-scroll max-h-[300px] w-full">
          <div
            v-for="review in gameReviews"
            :key="review.reviewId"
            class="flex justify-between mt-5 gap-3"
          >
            <reviewCard :review="review" />
          </div>
        </div>
      </div>

      <UpdateGameModal />
    </SheetContent>
  </Sheet>
</template>
