<template>
  <Header />
  <Toaster position="top-right" />

  <div class="p-6 space-y-6 w-9/12 mx-auto">
    <CustomTableHeader title="My Events" />

    <DataTableCard :is-loading="loading" :error="error">
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
            <TableHead class="font-bold text-lg text-black">Action</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <template v-for="event in filteredEvents" :key="event.id">
            <TableRow>
              <TableCell>
                <Button
                  variant="outline"
                  class="p-2 border-none bg-transparent"
                  @click="toggleRowExpansion(event.id)"
                >
                  <ChevronUp v-if="expandedRows[event.id]" class="h-4 w-4" />
                  <ChevronDown v-else class="h-4 w-4" />
                </Button>
              </TableCell>
              <TableCell class="text-start">{{ event.name }}</TableCell>
              <TableCell class="text-start">
                {{ event.game && event.game.name ? event.game.name : event.gameName }}
              </TableCell>
              <TableCell class="text-start">{{ event.location }}</TableCell>
              <TableCell class="text-start">{{ event.date }}</TableCell>
              <TableCell class="text-start">
                {{ event.maxParticipants - event.participantsCount }}
              </TableCell>
              <TableCell class="text-start">{{ event.maxParticipants }}</TableCell>
              <TableCell class="text-start">
                <div class="flex items-center gap-2 w-48 justify-between">
                  <!-- Register or Opt Out based on isRegistered -->
                  <Button
                    variant="outline"
                    size="sm"
                    v-if="!isRegistered(event.id)"
                    @click="registerEvent(event.id)"
                  >
                    Register
                  </Button>
                  <Button variant="outline" size="sm" v-else @click="unregisterEvent(event.id)">
                    Opt Out
                  </Button>

                  <!-- Organizer-only buttons -->
                  <Button
                    variant="outline"
                    size="icon"
                    v-if="isOrganizer(event)"
                    @click="openEditEventModal(event)"
                  >
                    <Edit class="h-4 w-4" />
                  </Button>
                  <Button
                    variant="outline"
                    size="icon"
                    v-if="isOrganizer(event)"
                    @click="deleteEvent(event.id)"
                  >
                    <Trash2 class="h-4 w-4" />
                  </Button>
                </div>
              </TableCell>
            </TableRow>

            <!-- Expanded row for registrations -->
            <TableRow v-if="expandedRows[event.id]">
              <TableCell colspan="8" class="px-20">
                <div class="flex flex-col items-start gap-2">
                  <p class="text-start max-w-[900px]">
                    <strong>Description: </strong>
                    <span>{{ event.description }}</span>
                  </p>
                  <p class="text-start max-w-[900px]">
                    <strong>Start Time: </strong>
                    <span>{{ event.startTime }}</span>
                  </p>
                  <p class="text-start max-w-[900px]">
                    <strong>End Time: </strong>
                    <span>{{ event.endTime }}</span>
                  </p>
                  <p class="text-start max-w-[900px]">
                    <strong>Organizer: </strong>
                    <span v-if="isOrganizer(event)">You</span>
                    <span v-else>{{ event.organizerName }}</span>
                  </p>
                  <p class="text-start max-w-[900px]">
                    <strong>Registrations:</strong>
                    <span v-if="registrationsByEvent[event.id]?.length">
                      <ul>
                        <li
                          v-for="(registration, index) in registrationsByEvent[event.id]"
                          :key="index"
                        >
                          {{ registration.registrant }}
                        </li>
                      </ul>
                    </span>
                    <span v-else> None </span>
                  </p>
                </div>
              </TableCell>
            </TableRow>
          </template>

          <!-- No events found -->
          <TableRow v-if="!loading && events.length === 0">
            <TableCell colspan="8" class="text-center py-8 text-muted-foreground">
              No events found. Create your first event by clicking the "Create Event" button.
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </DataTableCard>

    <!-- Edit Event Modal -->
    <EditEventModal
      v-if="isEditEventModalOpen"
      :close="closeEditEventModal"
      :eventToEdit="editEventData"
      :updateEvent="updateEvent"
    />
  </div>
</template>

<script setup lang="ts">
import Header from "@/components/Header.vue";
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useToast } from "@/components/ui/toast/use-toast";
import { Toaster } from "@/components/ui/toast";
import { storeToRefs } from "pinia";
import { useAuthStore } from "@/stores/authStore";

import { Button } from "@/components/ui/button";
import DataTableCard from "@/components/DataTableCard.vue";
import { ChevronDown, ChevronUp, Edit, Trash2 } from "lucide-vue-next";
import CustomTableHeader from "@/components/TableHeader.vue";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

import EditEventModal from "@/components/popups/update/UpdateEventModal.vue";

// Services
import { eventService } from "@/services/EventService";
import { registrationService } from "@/services/RegistrationService";

const authStore = useAuthStore();
const { user } = storeToRefs(authStore);

const events = ref<any[]>([]);
const error = ref("");
const loading = ref(false);
const expandedRows = ref<Record<number, boolean>>({});
const registrationsByEvent = ref<Record<number, any[]>>({});

// Modal states
const isEditEventModalOpen = ref<boolean>(false);
const editEventData = ref<any | null>(null);

const { toast } = useToast();
let pollingInterval: number | undefined;

const filteredEvents = computed(() =>
  events.value.filter((event) => isRegistered(event.id) || isOrganizer(event)),
);

const softReloadEvents = async () => {
  try {
    error.value = "";
    const data = await eventService.getAllEvents();
    events.value = data;
  } catch (err: any) {
    error.value = "Failed to load events.";
    console.error(err);
  }
};

const fetchEventsInitially = async () => {
  try {
    loading.value = true;
    error.value = "";
    const data = await eventService.getAllEvents();
    events.value = data;
  } catch (err) {
    error.value = "Failed to load events initially.";
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const fetchRegistrationsForEvent = async (eventId: number) => {
  try {
    const regs = await registrationService.findRegistrationsByEvent(eventId);
    registrationsByEvent.value[eventId] = regs;
  } catch (err) {
    console.error(`Error fetching registrations for event ${eventId}:`, err);
    registrationsByEvent.value[eventId] = [];
  }
};

// New function to fetch registrations for all visible events
const fetchAllEventRegistrations = async () => {
  try {
    for (const event of events.value) {
      await fetchRegistrationsForEvent(event.id);
    }
  } catch (err) {
    console.error("Error fetching all event registrations:", err);
  }
};

const isOrganizer = (event: any) => {
  if (!user.value?.userId) return false;
  if (event.organizer && event.organizer.id) {
    return event.organizer.id === user.value.userId;
  } else if (event.organizerName) {
    return event.organizerName === user.value.username;
  }
  return false;
};

const isRegistered = (eventId: number) => {
  const regs = registrationsByEvent.value[eventId];
  if (!regs || !user.value?.userId) return false;
  // Adjust this condition to match your backend fields:
  return regs.some(
    (reg) => reg.registrantId === user.value.userId || reg.registrant === user.value.username,
  );
};

const registerEvent = async (eventId: number) => {
  if (!user.value?.userId) {
    toast({
      title: "Please Login",
      description: "You need to be logged in to register for an event.",
      variant: "destructive",
      duration: 2000,
    });
    return;
  }
  try {
    await registrationService.registerToEvent(eventId, user.value.userId);
    toast({
      title: "Registration Successful",
      description: "You have successfully registered for the event.",
      variant: "default",
      duration: 2000,
    });
    await softReloadEvents();
    // Update all event registrations to ensure buttons are reactive
    await fetchAllEventRegistrations();
  } catch (err: any) {
    toast({
      title: "Registration Failed",
      description: err.response?.data?.message || err.message,
      variant: "destructive",
      duration: 2000,
    });
  }
};

const unregisterEvent = async (eventId: number) => {
  if (!user.value?.userId) {
    toast({
      title: "Please Login",
      description: "You need to be logged in to unregister from an event.",
      variant: "destructive",
      duration: 2000,
    });
    return;
  }
  try {
    await registrationService.unregisterFromEvent(eventId, user.value.userId);
    toast({
      title: "Unregistered",
      description: "You have opted out of the event.",
      variant: "default",
      duration: 2000,
    });
    await softReloadEvents();
    // Update all event registrations to ensure buttons are reactive
    await fetchAllEventRegistrations();
  } catch (err: any) {
    toast({
      title: "Unregistration Failed",
      description: err.response?.data?.message || err.message,
      variant: "destructive",
      duration: 2000,
    });
  }
};

const openEditEventModal = (event: any) => {
  editEventData.value = { ...event };
  isEditEventModalOpen.value = true;
};

const closeEditEventModal = () => {
  isEditEventModalOpen.value = false;
  editEventData.value = null;
};

const updateEvent = async (eventId: number, updatedData: any) => {
  try {
    if (!user.value?.userId) throw new Error("No user logged in.");
    updatedData.userId = user.value.userId;
    await eventService.updateEvent(eventId, updatedData);
    toast({
      title: "Event Updated",
      description: "Event updated successfully.",
      variant: "default",
      duration: 2000,
    });
    await softReloadEvents();
  } catch (err: any) {
    toast({
      title: "Update Failed",
      description: err.response?.data?.message || err.message,
      variant: "destructive",
      duration: 2000,
    });
    throw err;
  }
};

const deleteEvent = async (eventId: number) => {
  if (!user.value?.userId) {
    toast({
      title: "Please Login",
      description: "You must be logged in to delete an event.",
      variant: "destructive",
      duration: 2000,
    });
    return;
  }
  const eventToDelete = events.value.find((e) => e.id === eventId);
  if (!eventToDelete || !isOrganizer(eventToDelete)) {
    toast({
      title: "Permission Denied",
      description: "Only the organizer can delete this event.",
      variant: "destructive",
      duration: 2000,
    });
    return;
  }
  try {
    await eventService.deleteEvent(eventId, user.value.userId);
    toast({
      title: "Event Deleted",
      description: "Event deleted successfully.",
      variant: "default",
      duration: 2000,
    });
    await softReloadEvents();
  } catch (err: any) {
    toast({
      title: "Deletion Failed",
      description: err.response?.data?.message || err.message,
      variant: "destructive",
      duration: 2000,
    });
  }
};

const toggleRowExpansion = async (eventId: number) => {
  expandedRows.value[eventId] = !expandedRows.value[eventId];
  // When expanding, fetch the registrations for that event.
  if (expandedRows.value[eventId]) {
    await fetchRegistrationsForEvent(eventId);
  }
};

/*
const refreshNoBlink = async () => {
  await softReloadEvents();
};
*/

onMounted(async () => {
  await fetchEventsInitially();
  await fetchAllEventRegistrations();

  pollingInterval = window.setInterval(softReloadEvents, 30000);
});

onUnmounted(() => {
  if (pollingInterval) clearInterval(pollingInterval);
});
</script>

<style scoped></style>
