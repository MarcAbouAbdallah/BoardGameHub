<script setup lang="ts">
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Pen } from "lucide-vue-next";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { ref } from "vue";

const isOpen = ref(false);

const close = () => {
  isOpen.value = false;
  console.log("Dialog closed");
};

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
    close();
    // Call the APID to create the event here
  } catch (err) {
    error.value = "An error occurred. Please try again.";
  }
};
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button class="border-black" variant="outline">
        <Pen class="h-4 w-4" />
        Update Event
      </Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Update Event</DialogTitle>
        <DialogDescription>
          Update the event details by filling out the form below. Leave any field blank if you want
          to keep the current value.
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
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-name" class="w-full">Game</Label>
          <Input
            v-model="formData.gameName"
            id="game-name"
            type="text"
            placeholder="Enter game name"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-copy" class="w-full">Game Copy</Label>
          <Input
            v-model="formData.gameCopy"
            id="game-copy"
            type="text"
            placeholder="Enter game copy name"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="event-location" class="w-full">Location</Label>
          <Input
            v-model="formData.eventLocation"
            id="event-location"
            type="text"
            placeholder="Enter event location"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="event-description" class="w-full">Description</Label>
          <Input
            v-model="formData.eventDescription"
            id="event-description"
            type="text"
            placeholder="Enter event description"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="no-of-players" class="w-full">Max Number of Players</Label>
          <Input
            v-model="formData.noOfPlayers"
            id="no-of-players"
            type="number"
            placeholder="Enter max number of players"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="event-date" class="w-full">Date</Label>
          <Input
            v-model="formData.eventDate"
            id="event-date"
            type="date"
            placeholder="Select event date"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="event-start-time" class="w-full">Start Time</Label>
          <Input
            v-model="formData.eventStartTime"
            id="event-start-time"
            type="time"
            placeholder="Select event time"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="event-end-time" class="w-full">End Time</Label>
          <Input
            v-model="formData.eventEndTime"
            id="event-end-time"
            type="time"
            placeholder="Select event time"
          />
        </div>
        <Button type="submit" class="mt-4">Create Event</Button>
      </form>
    </DialogContent>
  </Dialog>
</template>
