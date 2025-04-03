<script setup lang="ts">
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { ref, onMounted, watch } from "vue";
import { defineProps } from "vue";
import { gameService } from "@/services/gameService";
import { gameCopyService } from "@/services/GameCopyService";
import { useAuthStore } from "@/stores/authStore";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const { user } = storeToRefs(authStore);

const props = defineProps<{
  close: () => void;
  createEvent: (eventData: any) => void;
}>();

const formData = ref({
  eventName: "",
  eventLocation: "",
  eventDescription: "",
  noOfPlayers: 0,
  eventDate: "",
  eventStartTime: "",
  eventEndTime: "",
});

const games = ref<any[]>([]);
const selectedGameId = ref<string | null>(null);
const gameCopies = ref<any[]>([]);
const selectedGameCopyId = ref<string | null>(null);

const error = ref("");

const fetchGames = async () => {
  try {
    const data = await gameService.getAllGames();
    games.value = data;
  } catch (err) {
    console.error("Error fetching games:", err);
  }
};

watch(selectedGameId, async (newGameId) => {
  if (newGameId) {
    try {
      const copies = await gameCopyService.getGameCopiesForGame(Number(newGameId));
      gameCopies.value = copies;
      selectedGameCopyId.value = null;
    } catch (err) {
      console.error("Error fetching game copies:", err);
    }
  } else {
    gameCopies.value = [];
  }
});

const handleSubmit = async () => {
  try {
    if (!selectedGameId.value) {
      error.value = "Please select a game.";
      return;
    }
    if (!selectedGameCopyId.value) {
      error.value = "Please select a game copy.";
      return;
    }
    if (!user.value.userId) {
      error.value = "Please log in to create an event.";
      return;
    }

    const payload = {
      name: formData.value.eventName,
      location: formData.value.eventLocation,
      description: formData.value.eventDescription,
      date: formData.value.eventDate,
      startTime: formData.value.eventStartTime + ":00",
      endTime: formData.value.eventEndTime + ":00",
      maxParticipants: Number(formData.value.noOfPlayers),
      organizer: user.value.userId,
      game: Number(selectedGameCopyId.value),
    };

    // We call the parent's createEvent prop to actually create it
    await props.createEvent(payload);

    // If the call is successful in the parent, close the modal
    props.close();
  } catch (err) {
    console.error("Error creating event:", err);
    error.value = "An error occurred while creating the event. Please try again.";
  }
};

onMounted(async () => {
  await fetchGames();
});
</script>

<template>
  <Dialog :default-open="true" @update:open="(isOpen) => !isOpen && props.close()">
    <DialogContent :close="props.close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Create Event</DialogTitle>
        <DialogDescription>
          Create a new event by filling out the form below. Provide all necessary details.
        </DialogDescription>
      </DialogHeader>
      <form class="flex flex-col gap-4" @submit.prevent="handleSubmit">
        <!-- Event Name -->
        <div class="flex gap-2 items-center">
          <Label for="event-name" class="w-full">Event Name</Label>
          <Input
            v-model="formData.eventName"
            id="event-name"
            type="text"
            placeholder="Enter event name"
            required
          />
        </div>
        <!-- Location -->
        <div class="flex gap-2 items-center">
          <Label for="event-location" class="w-full">Location</Label>
          <Input
            v-model="formData.eventLocation"
            id="event-location"
            type="text"
            placeholder="Enter event location"
            required
          />
        </div>
        <!-- Description -->
        <div class="flex gap-2 items-center">
          <Label for="event-description" class="w-full">Description</Label>
          <Input
            v-model="formData.eventDescription"
            id="event-description"
            type="text"
            placeholder="Enter event description"
            required
          />
        </div>
        <!-- Max Players -->
        <div class="flex gap-2 items-center">
          <Label for="no-of-players" class="w-full">Max Number of Players</Label>
          <Input
            v-model.number="formData.noOfPlayers"
            id="no-of-players"
            type="number"
            placeholder="Enter max number of players"
            required
          />
        </div>
        <!-- Date -->
        <div class="flex gap-2 items-center">
          <Label for="event-date" class="w-full">Date</Label>
          <Input
            v-model="formData.eventDate"
            id="event-date"
            type="date"
            placeholder="Select event date"
            required
          />
        </div>
        <!-- Start Time -->
        <div class="flex gap-2 items-center">
          <Label for="event-start-time" class="w-full">Start Time</Label>
          <Input
            v-model="formData.eventStartTime"
            id="event-start-time"
            type="time"
            placeholder="Select event start time"
            required
          />
        </div>
        <!-- End Time -->
        <div class="flex gap-2 items-center">
          <Label for="event-end-time" class="w-full">End Time</Label>
          <Input
            v-model="formData.eventEndTime"
            id="event-end-time"
            type="time"
            placeholder="Select event end time"
            required
          />
        </div>
        <!-- Game Dropdown -->
        <div class="flex gap-2 items-center">
          <Label for="game-select" class="w-full">Select Game</Label>
          <select
            id="game-select"
            v-model="selectedGameId"
            class="w-full border p-2 rounded"
            required
          >
            <option disabled value="">-- Choose a Game --</option>
            <option v-for="game in games" :key="game.id" :value="game.id">
              {{ game.name }}
            </option>
          </select>
        </div>
        <!-- Game Copy Dropdown -->
        <div class="flex gap-2 items-center">
          <Label for="gamecopy-select" class="w-full">Select Game Copy</Label>
          <select
            id="gamecopy-select"
            v-model="selectedGameCopyId"
            class="w-full border p-2 rounded"
            required
          >
            <option disabled value="">-- Choose a Game Copy --</option>
            <option v-for="copy in gameCopies" :key="copy.gameCopyId" :value="copy.gameCopyId">
              Copy #{{ copy.gameCopyId }} (Owner: {{ copy.ownerName }})
              ({{ copy.isAvailable ? "Available" : "Unavailable" }})
            </option>
          </select>
        </div>
        <Button type="submit" class="mt-4">Create Event</Button>
        <p v-if="error" class="text-red-600">{{ error }}</p>
      </form>
    </DialogContent>
  </Dialog>
</template>