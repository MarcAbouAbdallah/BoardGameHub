<script setup lang="ts">
import { ChevronDown, ChevronUp, ClipboardCheck, Trash, Undo } from "lucide-vue-next";
import CustomTableHeader from "@/components/TableHeader.vue";
import { ref } from "vue";
import { Badge } from "./ui/badge";
import { useToast } from "@/components/ui/toast/use-toast";
import updateEventModal from "./popups/update/UpdateEventModal.vue";
// import { Collapsible, CollapsibleTrigger, CollapsibleContent } from "@/components/ui/collapsible";
// import {
//   DropdownMenu,
//   DropdownMenuTrigger,
//   DropdownMenuContent,
//   DropdownMenuItem,
// } from "@/components/ui/dropdown-menu";
// import { expandRows } from "@tanstack/vue-table";
// import { set } from "@vueuse/core";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import DataTableCard from "./DataTableCard.vue";
import { Button } from "./ui/button";

import { defineProps } from "vue";

const { toast } = useToast();
const loading = ref(false);
const error = ref("");
const expandedRows = ref<Record<number, boolean>>({});
interface Event {
  id: number;
  name: string;
  game: string;
  location: string;
  date: string;
  remainingSeats: number;
  capacity: number;
  description: string;
  type: string;
  participants: string[];
}

const registerEvent = (eventId: number) => {
  //TO DO: Implement registration logic
  console.log("Registering for event with ID:", eventId);
  toast({
    title: "Registration Successful",
    description: `You have successfully registered for the event.`,
    variant: "default",
    duration: 2000,
  });
};

const props = defineProps<{
  events: Event[];
  isHomePage: boolean;
  title: string;
}>();

const toggleRowExpansion = (rowId: number) => {
  expandedRows.value[rowId] = !expandedRows.value[rowId];
};

// const fetchEvents = async () => {
//   try {
//     loading.value = true;
//     events.value = await fetch("/api/events").then((res) => res.json());
//   } catch (err) {
//     error.value = "Failed to load events.";
//     console.error(err);
//   } finally {
//     loading.value = false;
//   }
// };
</script>

<template>
  <div class="p-6 space-y-6 w-9/12 mx-auto">
    <CustomTableHeader :title="props.title" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead></TableHead>
            <TableHead class="font-bold text-lg text-black">Event Name</TableHead>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black" v-if="isHomePage">Type</TableHead>
            <TableHead class="font-bold text-lg text-black" v-else>Location</TableHead>
            <TableHead class="font-bold text-lg text-black">Date</TableHead>
            <TableHead class="font-bold text-lg text-black">Remaining Seats</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <template v-for="Event in props.events" :key="Event.name">
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
              <TableCell class="text-start" v-if="isHomePage">
                <Badge
                  :class="[
                    'min-w-[77px]',
                    Event.type === 'Created' ? 'bg-green-800' : 'bg-blue-800',
                  ]"
                >
                  {{ Event.type }}
                </Badge>
              </TableCell>
              <TableCell class="text-start" v-else>{{ Event.location }}</TableCell>
              <TableCell class="text-start">{{ Event.date }}</TableCell>
              <TableCell class="text-start">{{ Event.remainingSeats }}</TableCell>
              <TableCell class="text-start"
                ><div v-if="isHomePage">
                  <div v-if="Event.type == 'Created'" class="flex gap-6">
                    <Button variant="destructive" size="sm" class="border-black">
                      <Trash class="h-4 w-4" />
                      Delete Event
                    </Button>
                    <updateEventModal />
                  </div>
                  <Button v-else variant="destructive" size="sm" class="border-black min-w-[118px]">
                    <Undo class="h-4 w-4" />
                    Unregister
                  </Button>
                </div>
                <Button v-else variant="outline" size="sm" @click="registerEvent(Event.id)">
                  <ClipboardCheck class="h-4 w-4" />
                  Register
                </Button></TableCell
              >
            </TableRow>
            <TableRow v-if="expandedRows[Event.id]">
              <TableCell colspan="7" class="px-20">
                <div class="flex justify-between items-center mb-4">
                  <div class="flex flex-col items-start gap-2">
                    <p class="text-start max-w-[900px]">
                      <strong>Description:</strong> {{ Event.description }}
                    </p>
                    <p class="text-start max-w-[900px]">
                      <strong>Registrations: </strong>
                      <span v-if="Event.participants.length == 0">No Registrations Yet</span
                      >{{ Event.participants.join(", ") }}
                    </p>
                    <p v-if="isHomePage" class="text-start max-w-[900px]">
                      <strong>Location: </strong> {{ Event.location }}
                    </p>
                    <p class="text-start max-w-[900px]">
                      <strong>Capacity </strong> {{ Event.capacity }}
                    </p>
                  </div>
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
    </DataTableCard>
  </div>
</template>
