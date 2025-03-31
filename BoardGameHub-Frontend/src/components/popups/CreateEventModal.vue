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
import { ref } from "vue";
import { defineProps } from "vue";

const props = defineProps<{
  close: () => void;
}>();

const formData = ref({
  eventName: "",
  gameName: "",
  gameCopy: "",
  eventLocation: "",
  eventDescription: "",
  noOfPlayers: 0,
  eventDate: "",
  eventStartTime: "",
  eventEndTime: "",
});
const error = ref("");

const handleSubmit = async () => {
  try {
    console.log("Form Data:", formData.value);
    props.close();
    // Call the APID to create the event here
  } catch (err) {
    error.value = "An error occurred. Please try again.";
  }
};
</script>

<template>
  <Dialog :default-open="true" @update:open="(isOpen) => !isOpen && close()">
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Create Event</DialogTitle>
        <DialogDescription>
          Create a new event by filling out the form below. Make sure to provide all the necessary
          details.
        </DialogDescription>
      </DialogHeader>
      <form class="flex flex-col gap-4" @submit.prevent="handleSubmit">
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
        <div class="flex gap-2 items-center">
          <Label for="game-name" class="w-full">Game</Label>
          <Input
            v-model="formData.gameName"
            id="game-name"
            type="text"
            placeholder="Enter game name"
            required
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-copy" class="w-full">Game Copy</Label>
          <Input
            v-model="formData.gameCopy"
            id="game-copy"
            type="text"
            placeholder="Enter game copy name"
            required
          />
        </div>
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
        <div class="flex gap-2 items-center">
          <Label for="no-of-players" class="w-full">Max Number of Players</Label>
          <Input
            v-model="formData.noOfPlayers"
            id="no-of-players"
            type="number"
            placeholder="Enter max number of players"
            required
          />
        </div>
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
        <div class="flex gap-2 items-center">
          <Label for="event-start-time" class="w-full">Start Time</Label>
          <Input
            v-model="formData.eventStartTime"
            id="event-start-time"
            type="time"
            placeholder="Select event time"
            required
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="event-end-time" class="w-full">End Time</Label>
          <Input
            v-model="formData.eventEndTime"
            id="event-end-time"
            type="time"
            placeholder="Select event time"
            required
          />
        </div>
        <Button type="submit" class="mt-4">Create Event</Button>
      </form>
    </DialogContent>
  </Dialog>
</template>
