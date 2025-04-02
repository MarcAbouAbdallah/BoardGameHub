<script setup lang="ts">
import { Button } from "@/components/ui/button";
import { Plus } from "lucide-vue-next";
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
import DialogClose from "../ui/dialog/DialogClose.vue";

import type { Game } from "@/types/Game";
import gameService from "@/services/gameService";


const formData = ref({
  gameName: "",
  minPlayers: 0,
  maxPlayers: 0,
  gameDescription: "",
  gameImage: "",
});

const isOpen = ref(false);

const emit = defineEmits<{
  (e: 'game-created', newGame: Game): void;
}>();


const close = () => {
  isOpen.value = false;
  console.log("Dialog closed");
};

const handleSubmit = async () => {
  try {
    const gameToSend = {
      name: formData.value.gameName,
      description: formData.value.gameDescription,
      minPlayers: formData.value.minPlayers,
      maxPlayers: formData.value.maxPlayers,
    };

    const createdGame = await gameService.createGame(gameToSend);
    emit("game-created", createdGame); // Tell parent about it

    // Reset form and close modal
    formData.value = {
      gameName: "",
      minPlayers: 0,
      maxPlayers: 0,
      gameDescription: "",
      gameImage: "",
    };
    isOpen.value = false;

  } catch (err) {
    console.error("An error occurred. Please try again.", err);
  }
};
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button>
        <Plus class="h-4 w-4" />
        Add a new game
      </Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle>Add a new game</DialogTitle>
        <DialogDescription>
          Make changes to your profile here. Click save when you're done.
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
            required
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="min-players" class="w-full">Minimum Playerse</Label>
          <Input
            v-model="formData.minPlayers"
            id="min-players"
            type="number"
            placeholder="0"
            required
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="max-players" class="w-full">Maximum Players</Label>
          <Input
            v-model="formData.maxPlayers"
            id="max-players"
            type="number"
            placeholder="0"
            required
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-description" class="w-full">Game Description</Label>
          <Input
            v-model="formData.gameDescription"
            id="game-description"
            type="text"
            placeholder="Enter game description"
            required
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-image" class="w-full">Game Image</Label>
          <Input
            v-model="formData.gameImage"
            id="game-image"
            type="text"
            placeholder="Enter game image link"
            required
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
