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

import { onMounted, ref } from "vue";
import { gameCopyService } from "@/services/GameCopyService";
import { reviewService } from "@/services/reviewService";
import { borrowRequestService } from "@/services/borrowService";

import { useAuthStore } from "../stores/authStore";
import { useToast } from "@/components/ui/toast/use-toast";

import { defineProps } from "vue";
import BorrowReqModal from "./popups/BorrowReqModal.vue";
import CreateReviewModal from "./popups/CreateReviewModal.vue";
import UpdateGameModal from "./popups/update/UpdateGameModal.vue";
import type { Game } from "@/types/Game";


const fallback = "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzJ4bjU2NWE5YTh1Y3Q1cTVmcHdmOHhrOWo0a3hvN2dwcnNncXhzZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/YORVoIzhBZZaHY7Jp2/giphy.gif";

const { toast } = useToast();
const authStore = useAuthStore();

const props = defineProps<{
  game: Game;
}>();

const emit = defineEmits<{
  (e: "game-updated", updatedGame: Game): void;
}>();

const handleGameUpdated = (updatedGame: Game) => {
  emit("game-updated", updatedGame);
};

interface GameCopy {
  gameCopyId: string;
  ownerName: string;
  ownerId: string;
}

interface GameReview {
  id: number;
  rating: number;
  reviewerId: string;
  comment: string;
  gameName: string;
  reviewDate: string;
  reviewerName: string;
}

const gameCopies = ref<GameCopy[]>([]);
const gameReviews = ref<GameReview[]>([]);
const loadingCopies = ref(true);
const loadingReviews = ref(true);
const error = ref("");

onMounted(async () => {
  try {
    loadingCopies.value = true;
    loadingReviews.value = true;

    // Fetch game copies
    gameCopies.value = await gameCopyService.getGameCopiesForGame(props.game.id);

    // Fetch game reviews
    gameReviews.value = await reviewService.getReviewsByGameName(props.game.name);
  } catch (err: any) {
    error.value = err.message || "Failed to load game data.";
  } finally {
    loadingCopies.value = false;
    loadingReviews.value = false;
  }
});

const handleCreateReview = async (newReview: { rating: number; comment: string }) => {
  try {
    const userId = authStore.user.userId!;
    if (!userId) throw new Error("No user logged in.");

    const res = await reviewService.createReview({
      rating:newReview.rating, 
      comment:newReview.comment,
      gameName:props.game.name,
      reviewerId:userId
  });
    
    gameReviews.value.push(res);

    toast({
      title: "Review Created",
      description: "Your review has been added.",
      variant: "default",
    });
  } catch (err: any) {
    toast({
      title: "Failed to Create Review",
      description: err,
      variant: "destructive",
    });
  }
};

const handleCreateBorrow = async (request: {
  gameCopyId: string;
  requesteeId: string;
  startDate: string;
  endDate: string;
  comment: string;
}) => {
  try {
    const requesterId = authStore.user.userId!;
    await borrowRequestService.createBorrowRequest({
      requesterId,
      ...request,
    });

    toast({
      title: "Request Sent",
      description: "Your borrow request has been submitted.",
      variant: "default",
    });
  } catch (err: any) {
    toast({
      title: "Failed to Create Request",
      description: err,
      variant: "destructive",
    });
  }
};


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
          :src="props.game.photoURL || fallback"
          alt="Game Image"
          class="w-40 object-cover"
        />
      </div>
      <SheetDescription class="text-center text-ellipsis font-bold text-md my-2">
        {{ props.game.description }}
      </SheetDescription>

      <div class="flex flex-col items-start mt-4 w-full">
        <div class="text-lg font-bold">Players who Own the Game:</div>
        <div v-if="loadingCopies" class="text-sm text-gray-500 mt-2">Loading copies...</div>
        <div v-else class="overflow-scroll max-h-[200px] w-full">
          <div v-if="gameCopies.length > 0">
            <div
              v-for="copy in gameCopies"
              :key="copy.gameCopyId"
              class="flex justify-between mt-5 gap-3"
            >
              <div class="flex items-center gap-2">
                <UserCircle2Icon class="h-8 w-8 text-gray-500" />
                <span class="text-lg font-semibold">{{ copy.ownerName }}</span>
              </div>
              <BorrowReqModal 
                :gameCopyId="copy.gameCopyId"
                :requesteeId="copy.ownerId"
                @create="handleCreateBorrow"
              />
            </div>
          </div>
          <div v-else class="text-gray-500 italic mt-2">No one owns this game yet.</div>
        </div>

      </div>

      <div class="flex flex-col items-start mt-4 w-full">
        <div class="flex justify-between items-center w-full">
          <div class="text-lg font-bold">Game Reviews:</div>
          <CreateReviewModal class="w-fit" @create="handleCreateReview" />
        </div>

        <div v-if="loadingReviews" class="text-sm text-gray-500 mt-2">Loading reviews...</div>
        <div v-else class="overflow-scroll max-h-[300px] w-full">
          <div v-if="gameReviews.length > 0">
            <div
              v-for="review in gameReviews"
              :key="review.id"
              class="flex justify-between mt-5 gap-3"
            >
              <reviewCard :review="review" />
            </div>
          </div>
          <div v-else class="text-gray-500 italic mt-2">No reviews yet for this game.</div>
        </div>
      </div>

      <UpdateGameModal :game="props.game" @game-updated="handleGameUpdated" />

    </SheetContent>
  </Sheet>
</template>
