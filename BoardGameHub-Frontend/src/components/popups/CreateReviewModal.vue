<script setup lang="ts">
import { ref } from "vue";
import { Plus } from "lucide-vue-next";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";

const isOpen = ref(false);
const emit = defineEmits(["create"]);

const formData = ref({
  rating: 0,
  comment: "",
});

const error = ref("");

const close = () => {
  isOpen.value = false;
};

const handleSubmit = () => {
  if (!formData.value.rating || !formData.value.comment) {
    error.value = "Both fields are required.";
    return;
  }

  emit("create", {
    rating: formData.value.rating,
    comment: formData.value.comment,
  });

  // Reset form + close modal
  formData.value = { rating: 0, comment: "" };
  error.value = "";
  close();
};
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button variant="outline" class="ml-2">
        <Plus class="h-4 w-4" />
        Create Review
      </Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Write a Review</DialogTitle>
        <DialogDescription>
          Add a new review for this game.
        </DialogDescription>
      </DialogHeader>
      <form class="flex flex-col gap-4" @submit.prevent="handleSubmit">
        <div class="flex gap-2 items-center">
          <Label for="rating" class="w-1/3">Rating:</Label>
          <Input
            type="number"
            id="rating"
            v-model="formData.rating"
            min="0"
            max="10"
            class="w-2/3"
          />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="comment" class="w-1/3">Comment:</Label>
          <Input id="comment" v-model="formData.comment" type="text" class="w-2/3" />
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <Button type="button" variant="secondary" @click="close">Cancel</Button>
          <Button type="submit">Submit</Button>
        </div>
        <p v-if="error" class="text-red-500 text-sm">
          {{ error }}
        </p>
      </form>
    </DialogContent>
  </Dialog>
</template>
