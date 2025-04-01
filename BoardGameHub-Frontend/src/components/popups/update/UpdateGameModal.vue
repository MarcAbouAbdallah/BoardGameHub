<script setup lang="ts">
import { Button } from "@/components/ui/button";
import { Pen } from "lucide-vue-next";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ref } from "vue";
import DialogClose from "@/components/ui/dialog/DialogClose.vue";

const formData = ref({
  gameName: "",
  minPlayers: 0,
  maxPlayers: 0,
  gameDescription: "",
  gameImage: "",
});

const isOpen = ref(false);

const close = () => {
  isOpen.value = false;
  console.log("Dialog closed");
};

const handleSubmit = async () => {
  try {
    console.log("Form Data:", formData.value);
    // Call the API to create the game here
    isOpen.value = false;
  } catch (err) {
    console.error("An error occurred. Please try again.", err);
  }
};
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button variant="outline" class="absolute top-2 left-2 border-1 border-black">
        <Pen class="h-4 w-4" />
        Update Game
      </Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle>Update the game</DialogTitle>
        <DialogDescription>
          Modify the game details below. Leave any field blank if you want to keep the current
          value.
        </DialogDescription>
      </DialogHeader>
      <form class="flex flex-col gap-4 py-4" @submit.prevent="handleSubmit">
        <div class="flex gap-2 items-center">
          <Label for="game-name" class="w-full">Game Name</Label>
          <Input
            v-model="formData.gameName"
            id="game-name"
            type="text"
            placeholder="Enter game name"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="min-players" class="w-full">Minimum Playerse</Label>
          <Input v-model="formData.minPlayers" id="min-players" type="number" placeholder="0" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="max-players" class="w-full">Maximum Players</Label>
          <Input v-model="formData.maxPlayers" id="max-players" type="number" placeholder="0" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-description" class="w-full">Game Description</Label>
          <Input
            v-model="formData.gameDescription"
            id="game-description"
            type="text"
            placeholder="Enter game description"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-image" class="w-full">Game Image</Label>
          <Input
            v-model="formData.gameImage"
            id="game-image"
            type="text"
            placeholder="Enter game image link"
          />
        </div>
        <div class="flex gap-2 items-center flex-end">
          <Button type="submit" class="mt-4 w-fit">Add Game</Button>
          <DialogClose class="w-fit p-0 mt-4">
            <Button variant="outline">Cancel</Button>
          </DialogClose>
        </div>
      </form>
    </DialogContent>
  </Dialog>
</template>
