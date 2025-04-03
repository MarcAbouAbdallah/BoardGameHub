<script setup lang="ts">
import { ref } from "vue";
import { CalendarPlus } from "lucide-vue-next";
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

const props = defineProps<{
  gameCopyId: string;
  requesteeId: string;
}>();

const emit = defineEmits(["create"]);

const isOpen = ref(false);
const error = ref("");

const formData = ref({
  comment: "",
  startDate: "",
  endDate: "",
});

const close = () => {
  isOpen.value = false;
};

const handleSubmit = () => {
  if (!formData.value.startDate || !formData.value.endDate) {
    error.value = "Please fill all required fields.";
    return;
  }

  emit("create", {
    comment: formData.value.comment,
    startDate: formData.value.startDate,
    endDate: formData.value.endDate,
    gameCopyId: props.gameCopyId,
    requesteeId: props.requesteeId,
  });

  formData.value = { comment: "", startDate: "", endDate: "" };
  error.value = "";
  close();
};
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button variant="outline">
        <CalendarPlus class="h-4 w-4 mr-1" />
        Request to Borrow
      </Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Request to Borrow</DialogTitle>
        <DialogDescription>
          Fill in the details to request this game.
        </DialogDescription>
      </DialogHeader>

      <form class="flex flex-col gap-4" @submit.prevent="handleSubmit">
        <div class="flex gap-2 items-center">
          <Label for="startDate" class="w-1/3">Start Date:</Label>
          <Input id="startDate" v-model="formData.startDate" type="date" class="w-2/3" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="endDate" class="w-1/3">End Date:</Label>
          <Input id="endDate" v-model="formData.endDate" type="date" class="w-2/3" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="comment" class="w-1/3">Message:</Label>
          <Input id="comment" v-model="formData.comment" type="text" class="w-2/3" />
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <Button type="button" variant="secondary" @click="close">Cancel</Button>
          <Button type="submit">Submit</Button>
        </div>
        <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
      </form>
    </DialogContent>
  </Dialog>
</template>

