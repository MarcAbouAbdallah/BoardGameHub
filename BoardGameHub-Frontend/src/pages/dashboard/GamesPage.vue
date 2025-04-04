<script lang="ts" setup>
import Header from "../../components/Header.vue";
import GameCard from "@/components/GameCard.vue";

// import { games } from "@/data/sampleGames";
import type { Game } from "@/types/Game";
import { ref, onMounted } from "vue";
import gameService from "@/services/gameService";
import { computed, watch } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { getPlayerCollection } from "@/services/personalCollectionService";


import { Input } from "@/components/ui/input";
import Pagination from "@/components/ui/pagination/Pagination.vue";
import { Toaster } from "@/components/ui/toast";
import CreateGameModal from "@/components/popups/CreateGameModal.vue";
import router from "@/router";

//refs
const games = ref<Game[]>([]);
const loading = ref(true);
const error = ref("");
const searchTerm = ref("");
const currentPage = ref(1);
const itemsPerPage = 8;
const playerCollectionGameIds = ref<number[]>([]);
const authStore = useAuthStore();


onMounted(async () => {

  if (!authStore.user?.userEmail) {
    router.push("/");
    return;
  }

  try {
    const result = await gameService.getAllGames();
    games.value = result.sort((a: Game, b: Game) => a.name.localeCompare(b.name));


    if (authStore.user?.userId) {
      const collection = await getPlayerCollection(authStore.user.userId);

      console.log("Fetched player collection:", collection);

      playerCollectionGameIds.value = collection.map((copy: any) => copy.gameId);


    }
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


const filteredGames = computed(() => {
  if (!searchTerm.value.trim()) return games.value;

  return games.value.filter((game) =>
    game.name.toLowerCase().includes(searchTerm.value.toLowerCase())
  );
});

const paginatedGames = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return filteredGames.value.slice(start, start + itemsPerPage);
});

watch(searchTerm, () => {
  currentPage.value = 1;
});


</script>

<template>
  <Header />

  <div v-if="loading">Loading games...</div>
  <div v-else-if="error" class="text-red-500">{{ error }}</div>

  <div class="mt-24 mb-20 justify-center w-fit mx-auto">
    <Toaster />
    <div class="flex">
      <Input v-model="searchTerm" type="search" placeholder="Search games..." class="mb-4 mr-3 border-2" />
      <CreateGameModal @game-created="handleGameCreated" />

    </div>

    <div class="container mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <GameCard v-for="game in paginatedGames" :key="game.id" :game="game"
        :is-added="playerCollectionGameIds.includes(game.id)" @game-updated="handleGameUpdated" />


    </div>
    <div class="flex justify-center mt-10">
      <Pagination :items-per-page="itemsPerPage" :total="filteredGames.length" :default-page="1"
        v-model:page="currentPage" />
    </div>
  </div>
</template>
