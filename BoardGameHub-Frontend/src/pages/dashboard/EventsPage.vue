<script setup lang="ts">
import Header from "../../components/Header.vue";
import EventsTable from "@/components/EventsTable.vue";
import { Toaster } from "@/components/ui/toast";
import router from "@/router";
import { useAuthStore } from "@/stores/authStore";
import { onMounted } from "vue";

const authStore = useAuthStore();
const isLoggedIn = authStore.user;

onMounted(() => {
  if (!isLoggedIn.userEmail) {
    router.push("/");
  }
});
</script>

<template>
  <Header />
  <Toaster />
  <div class="mt-24">
    <EventsTable />
  </div>
</template>
