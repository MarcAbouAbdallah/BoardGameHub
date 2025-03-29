<script lang="ts" setup>
import Header from "../../components/Header.vue";
import { ref } from "vue";
import CreateEventModal from "@/components/CreateEventModal.vue";

const isCreateEventModalOpen = ref(false);

const closeCreateEventModal = () => {
  console.log("Modal closed");
  isCreateEventModalOpen.value = false;
};
</script>

<template>
  <Header />
  <div class="container mx-auto mt-4">
    <h1 class="text-2xl font-bold">Home Page</h1>
    <p>Welcome to the home page!</p>
  </div>
  <div class="container mx-auto mt-4">
    <button
      @click="isCreateEventModalOpen = true"
      class="px-4 py-2 bg-blue-500 text-white rounded"
    >
      Create Event
    </button>
  </div>
  <CreateEventModal
    v-if="isCreateEventModalOpen"
    :close="closeCreateEventModal"
  />
</template>
