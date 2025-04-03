<script setup lang="ts">
import { defineProps } from "vue";
import { PlusSquare, CheckSquare } from "lucide-vue-next";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "./ui/badge";
import GameSheet from "@/components/GameSheet.vue";
import { addGameToCollection } from "@/services/personalCollectionService";
import { useToast } from "@/components/ui/toast";
import type { Game } from "@/types/Game";
import { ref, onMounted } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { getPlayerCollection } from "@/services/personalCollectionService";

const { toast } = useToast();
const authStore = useAuthStore();
const isAdded = ref(false);

onMounted(async () => {
  if (!authStore.user || !authStore.user.userId) return;

  try {
    const collection = await getPlayerCollection(authStore.user.userId);
    const alreadyOwned = collection.some(
      (copy: any) => copy.game.id === props.game.id
    );

    if (alreadyOwned) {
      isAdded.value = true;
    }
  } catch (err) {
    console.error("Failed to fetch player collection:", err);
  }
});

const props = defineProps<{
  game: Game;
  isBorrowed?: boolean;
}>();


const handleAddToCollectionClick = async (gameId: number) => {
  const playerId = authStore.user.userId;

  console.log("Sending to backend:", {
    playerId: authStore.user.userId,
    gameId,
  });

  if (!playerId) {
    toast({
      title: "Not Logged In",
      description: "You must be logged in to add games to your collection.",
      variant: "destructive",
    });
    return;
  }

  try {
    await addGameToCollection(playerId, gameId);
    isAdded.value = true;
    toast({
      title: "Game Added!",
      description: "This game is now in your collection.",
    });

  } catch (err) {
    console.error("Failed to add game to collection:", err);
    toast({
      title: "Failed to Add",
      description: "There was an error adding this game.",
      variant: "destructive",
    });
  }
};
</script>

<template>
  <GameSheet :game="props.game">
    <Card class="max-w-[18rem] py-6 hover:cursor-pointer hover:bg-gray-100 transition-all duration-200 relative">
      <CardHeader class="p-2">
        <img
          :src="props.game.photoURL || 'https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzJ4bjU2NWE5YTh1Y3Q1cTVmcHdmOHhrOWo0a3hvN2dwcnNncXhzZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/YORVoIzhBZZaHY7Jp2/giphy.gif'"
          alt="Game Image" class="w-full h-48 object-cover rounded-md" />
      </CardHeader>
      <CardContent class="p-2">
        <div class="flex gap-2 flex-wrap ml-4 mb-8">
          <Badge v-if="isBorrowed">Borrowed</Badge>
          <Badge> {{ props.game.minPlayers }} - {{ props.game.maxPlayers }} Players</Badge>
        </div>

        <CardTitle class="text-lg text-center mb-1 font-bold">{{ props.game.name }}</CardTitle>
        <CardDescription class="text-sm text-gray-500">{{
          props.game.description
          }}</CardDescription>
      </CardContent>
      <div class="absolute top-3 right-3 group">
        <CheckSquare v-if="isAdded" class="w-6 h-6 text-black" />
        <PlusSquare v-else class="w-6 h-6 hover:text-gray-400"
          @click.stop="handleAddToCollectionClick(props.game.id)" />
        <span
          class="absolute top-full mt-1 right-0 bg-gray-800 text-white text-xs rounded py-1 px-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
          Add to your collection
        </span>
      </div>
    </Card>
  </GameSheet>
</template>
