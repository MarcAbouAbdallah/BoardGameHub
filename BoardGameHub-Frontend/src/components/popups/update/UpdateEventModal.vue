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
import { ref, watch, onMounted, nextTick } from "vue";
import { defineProps } from "vue";
import gameService from "@/services/gameService";
import { gameCopyService } from "@/services/GameCopyService";
import { useAuthStore } from "@/stores/authStore";
import { storeToRefs } from "pinia";
import { useToast } from "@/components/ui/toast";

const props = defineProps<{
  close: () => void;
  eventToEdit: any;
  updateEvent: (eventId: number, updatedData: any) => Promise<void>;
}>();

const authStore = useAuthStore();
const { toast } = useToast();
const { user } = storeToRefs(authStore);

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

// This function initializes the form fields and preselects the current game and copy.
const initializeForm = async () => {
  if (!props.eventToEdit) return;

  // Set basic form fields
  formData.value.eventName = props.eventToEdit.name || "";
  formData.value.eventLocation = props.eventToEdit.location || "";
  formData.value.eventDescription = props.eventToEdit.description || "";
  formData.value.noOfPlayers = props.eventToEdit.maxParticipants || 0;
  formData.value.eventDate = props.eventToEdit.date || "";
  formData.value.eventStartTime = props.eventToEdit.startTime?.substring(0, 5) || "";
  formData.value.eventEndTime = props.eventToEdit.endTime?.substring(0, 5) || "";

  // Determine the game copy:
  let copy = null;
  try {
    if (typeof props.eventToEdit.game === "object") {
      // If the event's game field is already an object, use it.
      copy = props.eventToEdit.game;
    } else {
      // Otherwise, assume it's an ID and fetch the game copy.
      const copyId = Number(props.eventToEdit.game);
      copy = await gameCopyService.getGameCopyById(copyId);
    }
  } catch (err) {
    console.error("Error fetching game copy:", err);
  }
  
  if (copy) {
    // Set the game copy dropdown using the returned game copy.
    selectedGameCopyId.value = String(copy.gameCopyId);
    // Assume the game copy object has a nested "game" object with an "id" property.
    if (copy.game && copy.game.id) {
      selectedGameId.value = String(copy.game.id);
    } else if (copy.gameId) {
      // Fallback if the game object is not nested.
      selectedGameId.value = String(copy.gameId);
    } else {
      selectedGameId.value = null;
    }
    // Fetch all game copies for this game to populate the dropdown.
    if (selectedGameId.value) {
      try {
        const copies = await gameCopyService.getGameCopiesForGame(Number(selectedGameId.value));
        gameCopies.value = copies;
      } catch (err) {
        console.error("Error fetching game copies:", err);
      }
    }
  }
};

// Load all games for the dropdown.
const fetchGames = async () => {
  try {
    const data = await gameService.getAllGames();
    games.value = data;
  } catch (err) {
    console.error("Error fetching games:", err);
  }
};

// When the selected game changes, load the copies for that game.
watch(selectedGameId, async (newGameId) => {
  if (newGameId) {
    try {
      const copies = await gameCopyService.getGameCopiesForGame(Number(newGameId));
      gameCopies.value = copies;
      // If the current selected copy doesn't exist in the new list, clear it.
      if (
        selectedGameCopyId.value &&
        !copies.some((c: { gameCopyId: number }) => String(c.gameCopyId) === selectedGameCopyId.value)
      ) {
        selectedGameCopyId.value = null;
      }
    } catch (err) {
      console.error("Error fetching game copies on game change:", err);
    }
  } else {
    gameCopies.value = [];
  }
});

// Submit the updated data.
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
    if (!user.value?.userId) {
      error.value = "Please log in.";
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
      game: Number(selectedGameCopyId.value),
    };

    await props.updateEvent(props.eventToEdit.id, payload);
    props.close();

  } catch (err: any) {
    console.error("Error creating event:", err);
    toast({
      title: "Edit Failed",
      description: err.response?.data?.errors?.[0] || err.message || "An error occurred. Please try again.",
      variant: "destructive",
      duration: 2000,
    });
    error.value = err.response?.data?.message || err.message || "An error occurred. Please try again.";
  }
};

onMounted(async () => {
  await fetchGames();
  // Wait for games to load and render before initializing the form.
  await nextTick();
  await initializeForm();
});
</script>

<template>
  <Dialog :default-open="true" @update:open="(isOpen) => !isOpen && props.close()">
    <DialogContent :close="props.close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Edit Event</DialogTitle>
        <DialogDescription>
          Update the event details below.
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
        <!-- Max Number of Players -->
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
          <Label for="gamecopy-select" class="w-full">Select Game Owner</Label>
          <select
            id="gamecopy-select"
            v-model="selectedGameCopyId"
            class="w-full border p-2 rounded"
            required
          >
            <option disabled value="">-- Choose a Game First --</option>
            <option v-for="copy in gameCopies" :key="copy.gameCopyId" :value="copy.gameCopyId">
              {{ copy.ownerName }}
            </option>
          </select>
        </div>
        <Button type="submit" class="mt-4">Update Event</Button>
      </form>
    </DialogContent>
  </Dialog>
</template>