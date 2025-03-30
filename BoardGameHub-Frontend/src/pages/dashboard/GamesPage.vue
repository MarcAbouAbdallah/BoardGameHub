<script lang="ts" setup>
import Header from "../../components/Header.vue";
import GameCard from "@/components/GameCard.vue";
import { games } from "@/data/sampleGames";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import Pagination from "@/components/ui/pagination/Pagination.vue";
</script>

<template>
  <Header />
  <div class="mt-24 mb-20 justify-center w-fit mx-auto">
    <div class="flex">
      <Input
        type="search"
        placeholder="Search games..."
        class="mb-4 border-2"
      />
      <Button class="ml-4">Add a new game</Button>
    </div>

    <div
      class="container mx-auto grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"
    >
      <GameCard
        v-for="game in games"
        :key="game.id"
        :game="game"
      />
    </div>
    <div class="flex justify-center mt-10">
      <Pagination />
    </div>
  </div>
</template>
