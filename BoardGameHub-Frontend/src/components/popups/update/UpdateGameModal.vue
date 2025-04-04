<script setup lang="ts">
import { ref, watch } from "vue";
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
import { Button } from "@/components/ui/button";
import DialogClose from "@/components/ui/dialog/DialogClose.vue";
import type { Game } from "@/types/Game";
import gameService from "@/services/gameService";
import { useToast } from "@/components/ui/toast/use-toast";

const props = defineProps<{ game: Game }>();

const emit = defineEmits<{
  (e: "game-updated", updatedGame: Game): void;
}>();

const isOpen = ref(false);
const { toast } = useToast();
const error = ref("");

const formData = ref({
  gameName: "",
  minPlayers: 0,
  maxPlayers: 0,
  gameDescription: "",
  gameImage: "",
});

watch(isOpen, (newVal) => {
  if (newVal) {
    formData.value = {
      gameName: props.game.name,
      minPlayers: props.game.minPlayers,
      maxPlayers: props.game.maxPlayers,
      gameDescription: props.game.description,
      gameImage: props.game.photoURL ?? "",
    };
  }
});

const close = () => {
  isOpen.value = false;
};

const handleSubmit = async () => {
  try {
    const updatedFields = {
      name: formData.value.gameName,
      description: formData.value.gameDescription,
      maxPlayers: formData.value.maxPlayers,
      minPlayers: formData.value.minPlayers,
      photoURL: formData.value.gameImage,
    };

    const updatedGame = await gameService.updateGame(props.game.id, updatedFields);
    emit("game-updated", updatedGame);

    toast({
      title: "Game Updated!",
      description: `"${updatedGame.name}" has been updated successfully.`,
      variant: "default",
    });

    close();
  } catch (err: any) {
    toast({
      title: "Update Failed",
      description: err,
      variant: "destructive",
    });
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
        <DialogTitle>Edit Game</DialogTitle>
        <DialogDescription>
          Modify the game details. All fields are editable.
        </DialogDescription>
      </DialogHeader>
      <form class="flex flex-col gap-4 py-4" @submit.prevent="handleSubmit">
        <div class="flex gap-2 items-center">
          <Label for="game-name" class="w-full">Game Name</Label>
          <Input v-model="formData.gameName" id="game-name" type="text" placeholder="Enter game name" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="min-players" class="w-full">Minimum Players</Label>
          <Input v-model="formData.minPlayers" id="min-players" type="number" placeholder="0" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="max-players" class="w-full">Maximum Players</Label>
          <Input v-model="formData.maxPlayers" id="max-players" type="number" placeholder="0" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-description" class="w-full">Game Description</Label>
          <Input v-model="formData.gameDescription" id="game-description" type="text" placeholder="Description..." />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="game-image" class="w-full">Game Image</Label>
          <Input v-model="formData.gameImage" id="game-image" type="text" placeholder="Enter image URL" />
        </div>

        <div class="flex justify-end gap-2 mt-4">
          <DialogClose as-child>
            <Button variant="secondary">Cancel</Button>
          </DialogClose>
          <Button type="submit">Submit</Button>
        </div>

        <p v-if="error" class="text-red-500 text-sm mt-2">{{ error }}</p>
      </form>
    </DialogContent>
  </Dialog>
</template>