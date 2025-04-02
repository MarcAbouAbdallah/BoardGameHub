<script lang="ts" setup>
import Header from "../../components/Header.vue";
import EventsTable from "@/components/EventsTable.vue";
import GamesTable from "@/components/GamesTable.vue";
import BorrowRequestTable from "@/components/BorrowRequestTable.vue";
import GameReviewTable from "@/components/GameReviewTable.vue";
import { sampleEvents } from "@/data/sampleEvents";
import { sampleGameCollection } from "@/data/sampleGames";
import Toaster from "@/components/ui/toast/Toaster.vue";
import RecievedBorrowReqTable from "@/components/ReceivedBorrowReqTable.vue";
</script>

<template>
  <Header />
  <div class="flex flex-col items-start my-24 mx-10">
    <GamesTable :games="sampleGameCollection" />
    <RecievedBorrowReqTable />
    <EventsTable :events="sampleEvents" :is-home-page="true" :title="'My Game Events'" />
    <!--<BorrowRequestTable :-borrow-requests="sampleBorrowRequests" />-->
    <BorrowRequestTable />
    <!--<GameReviewTable :game-reviews="gameReviews" />-->
    <GameReviewTable />
  </div>
  <Toaster position="top-right" />
</template>
