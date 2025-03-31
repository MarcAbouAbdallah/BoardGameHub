<script lang="ts" setup>
import Header from "../../components/Header.vue";
import { ref } from "vue";
import EventsTable from "@/components/EventsTable.vue";
import GamesTable from "@/components/GamesTable.vue";
import BorrowRequestTable from "@/components/BorrowRequestTable.vue";
import GameReviewTable from "@/components/GameReviewTable.vue";
import { sampleEvents } from "@/data/sampleEvents";
import { games } from "@/data/sampleGames";

import BorrowReqModal from "@/components/popups/BorrowReqModal.vue";

const isBorrowReqModalOpen = ref(false);

const closeBorrowReqModal = () => {
  isBorrowReqModalOpen.value = false;
};
</script>

<template>
  <Header />
  <div class="flex flex-col items-start my-24 mx-10">
    <GamesTable :games="games" />
    <EventsTable :events="sampleEvents" :is-home-page="true" :title="'My Game Events'" />
    <BorrowRequestTable />
    <GameReviewTable />
  </div>

  <BorrowReqModal v-if="isBorrowReqModalOpen" :close="closeBorrowReqModal" />
</template>
