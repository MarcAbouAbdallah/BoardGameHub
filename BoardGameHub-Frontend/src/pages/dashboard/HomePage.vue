<script lang="ts" setup>
import Header from "../../components/Header.vue";
import { ref } from "vue";

import BorrowReqModal from "@/components/popups/BorrowReqModal.vue";

const isBorrowReqModalOpen = ref(false);

const closeBorrowReqModal = () => {
  isBorrowReqModalOpen.value = false;
};
</script>

<template>
  <Header />
  <div class="flex flex-col items-start my-24 mx-10">
    <div class="text-4xl border-b-2 border-gray-300 w-full flex justify-start pb-2">My Events</div>
    <div class="text-4xl border-b-2 border-gray-300 w-full flex justify-start pb-2">
      My Requests
    </div>
  </div>

  <BorrowReqModal v-if="isBorrowReqModalOpen" :close="closeBorrowReqModal" />
</template>
