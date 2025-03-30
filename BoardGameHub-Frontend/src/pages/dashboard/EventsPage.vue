<script setup lang="ts">
import Header from "../../components/Header.vue";
import { FilterIcon } from "lucide-vue-next";
import { ref, onMounted } from "vue";
import CreateEventModal from "@/components/popups/CreateEventModal.vue";
import { Button } from "@/components/ui/button";
import { sampleEvents } from "@/data/sampleEvents";

import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from "@/components/ui/dropdown-menu";

import { ChevronDown, ChevronUp, Plus, RefreshCcw, Pencil, Trash } from "lucide-vue-next";

import { Toaster } from "@/components/ui/toast";
import { useToast } from "@/components/ui/toast/use-toast";

import { Collapsible, CollapsibleTrigger, CollapsibleContent } from "@/components/ui/collapsible";

import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { expandRows } from "@tanstack/vue-table";

const events = ref(sampleEvents);
const loading = ref(false);
const error = ref("");
const expandedRows = ref<Record<number, boolean>>({});

const isCreateEventModalOpen = ref(false);

const closeCreateEventModal = () => {
  isCreateEventModalOpen.value = false;
};

const toggleRowExpansion = (rowId: number) => {
  expandedRows.value[rowId] = !expandedRows.value[rowId];
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
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead></TableHead>
          <TableHead class="font-bold text-lg text-black">Event Name</TableHead>
          <TableHead class="font-bold text-lg text-black">Game</TableHead>
          <TableHead class="font-bold text-lg text-black">Location</TableHead>
          <TableHead class="font-bold text-lg text-black">Date</TableHead>
          <TableHead class="font-bold text-lg text-black">Remaining Seats</TableHead>
          <TableHead class="font-bold text-lg text-black">Capacity</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        <template v-for="Event in sampleEvents" :key="Event.name">
          <TableRow>
            <TableCell>
              <Button
                variant="outline"
                class="p-2 border-none bg-transparent"
                @click="toggleRowExpansion(Event.id)"
              >
                <ChevronUp v-if="expandedRows[Event.id]" class="h-4 w-4" />
                <ChevronDown v-else class="h-4 w-4" />
              </Button>
            </TableCell>
            <TableCell class="text-start">{{ Event.name }}</TableCell>
            <TableCell class="text-start">{{ Event.game }}</TableCell>
            <TableCell class="text-start">{{ Event.location }}</TableCell>
            <TableCell class="text-start">{{ Event.date }}</TableCell>
            <TableCell class="text-start">{{ Event.remainingSeats }}</TableCell>
            <TableCell class="text-start">{{ Event.capacity }}</TableCell>
          </TableRow>
          <TableRow v-if="expandedRows[Event.id]">
            <TableCell colspan="7">
              <div class="flex justify-between items-center mb-4 px-20">
                <p><strong>Start Time:</strong> {{ Event.startTime }}</p>
                <p><strong>End Time:</strong> {{ Event.endTime }}</p>
                <Button variant="outline" size="sm"> <Pencil class="h-4 w-4" /> Edit </Button>
              </div>
            </TableCell>
          </TableRow>
        </template>
        <TableRow v-if="events.length === 0">
          <TableCell colspan="7" class="text-center py-8 text-muted-foreground">
            No events found. Create your first event by clicking the "Create Event" button.
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
    <CreateEventModal v-if="isCreateEventModalOpen" :close="closeCreateEventModal" />
  </div>
</template>
