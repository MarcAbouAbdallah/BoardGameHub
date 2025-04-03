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
import type { Game } from "@/types/Game";
import { defineProps, watch} from "vue";
import gameService from "@/services/gameService";


const props = defineProps<{ game: Game }>();

const formData = ref({
  gameName: "",
  minPlayers: 0,
  maxPlayers: 0,
  gameDescription: "",
  gameImage: "",
});

const isOpen = ref(false);

watch(isOpen, (newVal) => {
  if (newVal) {
    formData.value = {
      gameName: props.game.name,
      minPlayers: props.game.minPlayers,
      maxPlayers: props.game.maxPlayers,
      gameDescription: props.game.description,
      gameImage: props.game.photoURL, // optional – not used by backend right now
    };
  }
});


const close = () => {
  isOpen.value = false;
  console.log("Dialog closed");
};

const emit = defineEmits<{
  (e: "game-updated", updatedGame: Game): void;
}>();

const handleSubmit = async () => {
  try {
    const updatedFields = {
      name: formData.value.gameName,
      description: formData.value.gameDescription,
      minPlayers: formData.value.minPlayers,
      maxPlayers: formData.value.maxPlayers,
      photoURL: formData.value.gameImage,
    };

    //debug
    console.log("Sending updatedFields:", updatedFields);

    const updatedGame = await gameService.updateGame(props.game.id, updatedFields);
    emit("game-updated", updatedGame);
    isOpen.value = false;
  } catch (err) {
    console.error("Failed to update game:", err);
  }
};

</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button variant="outline" class="absolute top-2 left-2 border-1 border-black">
        <Pen class="h-4 w-4" />
        Edit Game
      </Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle>Edit the game</DialogTitle>
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
          <Button type="submit" class="mt-4 w-fit">Confirm</Button>
          <DialogClose class="w-fit p-0 mt-4">
            <Button variant="outline">Cancel</Button>
          </DialogClose>
        </div>
      </form>
    </DialogContent>
  </Dialog>
</template>
