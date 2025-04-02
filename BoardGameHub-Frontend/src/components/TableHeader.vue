<script setup lang="ts">
import { defineProps } from "vue";
import { Button } from "./ui/button";
import { FilterIcon, RefreshCcw, Plus } from "lucide-vue-next";
import { useRouter } from "vue-router";
import CreateEventModal from "./popups/CreateEventModal.vue";

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
});

const router = useRouter();

const isGameHeader = props.title.toString().includes("My Games");
const isEventHeader = props.title.toString().includes("Events");
</script>

<template>
  <div class="flex justify-between items-center">
    <h1 class="text-2xl font-bold">{{ props.title }}</h1>
    <div class="flex-1 mx-10"></div>
    <Button v-if="isGameHeader" @click="router.push('/games')">
      <Plus class="w-4 h-4" />
      Add Games to your Collection
    </Button>
    <CreateEventModal v-if="isEventHeader" />
    <Button variant="outline" class="flex items-center mx-3 border-black">
      <FilterIcon class="w-4 h-4" />Filters
    </Button>
    <Button @click="">
      <RefreshCcw class="h-4 w-4" />
      Refresh
    </Button>
  </div>
</template>
