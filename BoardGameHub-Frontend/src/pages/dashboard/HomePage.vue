<script lang="ts" setup>
import Header from "../../components/Header.vue";
import { ref } from "vue";
import CreateEventModal from "@/components/popups/CreateEventModal.vue";
import BorrowReqModal from "@/components/popups/BorrowReqModal.vue";
import AccountInfo from "@/components/AccountInfo.vue";

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
  <div class="flex flex-col items-start my-24 mx-10">
    <div
      class="text-4xl border-b-2 border-gray-300 w-full flex justify-start pb-2"
    >
      Account Information
    </div>
    <AccountInfo
      :username="'Mubeen Mohammed'"
      :email="'mubeen@gmail.com'"
      :isGameOwner="true"
    />
    <div
      class="text-4xl border-b-2 border-gray-300 w-full flex justify-start pb-2"
    >
      My Events
    </div>
    <div
      class="text-4xl border-b-2 border-gray-300 w-full flex justify-start pb-2"
    >
      My Requests
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
