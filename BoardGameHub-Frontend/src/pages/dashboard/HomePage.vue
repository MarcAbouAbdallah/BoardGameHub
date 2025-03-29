<script lang="ts" setup>
import Header from "../../components/Header.vue";
import { ref } from "vue";
import CreateEventModal from "@/components/popups/CreateEventModal.vue";
import BorrowReqModal from "@/components/popups/BorrowReqModal.vue";

const isCreateEventModalOpen = ref(false);
const isBorrowReqModalOpen = ref(false);

const closeCreateEventModal = () => {
  isCreateEventModalOpen.value = false;
};

const closeBorrowReqModal = () => {
  isBorrowReqModalOpen.value = false;
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
    <button
      @click="isBorrowReqModalOpen = true"
      class="px-4 py-2 bg-green-500 text-white rounded ml-4"
    >
      Request to Borrow
    </button>
  </div>
  <CreateEventModal
    v-if="isCreateEventModalOpen"
    :close="closeCreateEventModal"
  />
  <BorrowReqModal
    v-if="isBorrowReqModalOpen"
    :close="closeBorrowReqModal"
  />
</template>
