<script setup lang="ts">
import Header from "../../components/Header.vue";
import { FilterIcon } from "lucide-vue-next";
import { ref } from "vue";
import CreateEventModal from "@/components/popups/CreateEventModal.vue";
import { Button } from "@/components/ui/button";
import { sampleEvents } from "@/data/sampleEvents";

const events = ref(sampleEvents);
const loading = ref(false);
const error = ref("");

const isCreateEventModalOpen = ref(false);

const closeCreateEventModal = () => {
  isCreateEventModalOpen.value = false;
};

const fetchEvents = async () => {
  try {
    loading.value = true;
    events.value = await fetch("/api/events").then((res) => res.json());
  } catch (err) {
    error.value = "Failed to load events.";
    console.error(err);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <Header />
  <div class="p-6 space-y-6 mt-24 w-9/12 mx-auto">
    <div class="flex justify-between items-center">
      <h1 class="text-2xl font-bold">Game Events</h1>
      <EventFilters class="flex-1 mx-10" />
      <Button variant="outline" class="flex items-center mx-3">
        <FilterIcon class="w-4 h-4" />
      </Button>
      <Button class="mr-2" @click="isCreateEventModalOpen = true"> Create Event </Button>
      <Button @click="fetchEvents">Refresh</Button>
    </div>
    <CreateEventModal v-if="isCreateEventModalOpen" :close="closeCreateEventModal" />
  </div>
</template>
