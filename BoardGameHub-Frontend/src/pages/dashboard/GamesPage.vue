<script lang="ts" setup>
import Header from "../../components/Header.vue";
import GameCard from "@/components/GameCard.vue";

// import { games } from "@/data/sampleGames";
import type { Game } from "@/types/Game";
import { ref, onMounted } from "vue";
import gameService from "@/services/gameService";

import { Input } from "@/components/ui/input";
import Pagination from "@/components/ui/pagination/Pagination.vue";
import { Toaster } from "@/components/ui/toast";
import CreateGameModal from "@/components/popups/CreateGameModal.vue";

//variables
const games = ref<Game[]>([]);
const loading = ref(true);
const error = ref("");

onMounted(async () => {
  try {
    const result = await gameService.getAllGames();
    games.value = result;
  } catch (err) {
    error.value = "Failed to load games.";
    console.error(err);
  } finally {
    loading.value = false;
  }
});

const handleGameCreated = (newGame: Game) => {
  games.value.push(newGame); // update list live
};

const handleGameUpdated = (updatedGame: Game) => {
  games.value = games.value.map((g) =>
    g.id === updatedGame.id ? updatedGame : g
  );
};


</script>

<template>
  <Header />

  <div v-if="loading">Loading games...</div>
  <div v-else-if="error" class="text-red-500">{{ error }}</div>

  <div class="mt-24 mb-20 justify-center w-fit mx-auto">
    <Toaster />
    <div class="flex">
      <Input type="search" placeholder="Search games..." class="mb-4 mr-3 border-2" />
      <CreateGameModal @game-created="handleGameCreated" />

    </div>

    <div
      class="container mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"
    >
    <GameSheet
      v-for="game in games"
      :key="game.id"
      :game="game"
      @game-updated="handleGameUpdated"
    >
      <GameCard :game="game" />
    </GameSheet>

    </div>
    <div class="flex justify-center mt-10">
      <Pagination />
    </div>
  </div>
</template>
