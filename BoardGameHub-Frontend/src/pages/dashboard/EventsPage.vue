<script setup lang="ts">
import Header from "../../components/Header.vue";
import { FilterIcon } from "lucide-vue-next";
import { ref, onMounted } from "vue";
import { Button } from "@/components/ui/button";
import Toast from "primevue/toast";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";
import DataTable from "primevue/datatable";
import EventTable from "../../components/events/EventTable.vue";

import { sampleEvents } from "@/data/sampleEvents";

const events = ref(sampleEvents);
const loading = ref(false);
const error = ref("");

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
      <h1 class="text-2xl font-bold">Events</h1>
      <EventFilters class="w-full mx-5" />
      <Button
        variant="outline"
        class="flex items-center mx-5"
      >
        <FilterIcon class="w-4 h-4 mr-2" /> Filters
      </Button>
      <Button @click="fetchEvents">Refresh</Button>
    </div>

    <EventTable
      :events="events"
      :loading="loading"
      :error="error"
    />
  </div>
</template>
