<script lang="ts" setup>
import Header from "../../components/Header.vue";
import MyEventsTable from "@/components/MyEventsTable.vue";
import GamesTable from "@/components/GamesTable.vue";
import GameReviewTable from "@/components/GameReviewTable.vue";
import Toaster from "@/components/ui/toast/Toaster.vue";
import { useAuthStore } from "@/stores/authStore";
import BorrowReqTabs from "@/components/BorrowReqTabs.vue";
import { onMounted } from "vue";
import router from "@/router";

const authStore = useAuthStore();
const isLoggedIn = authStore.user;

console.log("isLoggedIn", isLoggedIn);

onMounted(() => {
  if (!isLoggedIn.userEmail) {
    router.push("/");
  }
});
</script>

<template>
  <Header />
  <div class="flex flex-col items-start my-24 mx-10">
    <GamesTable />
    <MyEventsTable />
    <BorrowReqTabs />
    <GameReviewTable />
  </div>
  <Toaster position="top-right" />
</template>
