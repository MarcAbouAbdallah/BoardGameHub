<script setup lang="ts">
import { ref } from "vue";
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
import { defineProps } from "vue";

const props = defineProps<{
  close: () => void;
}>();

const formData = ref({
  comment: "",
  startDate: "",
  endDate: "",
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
        <DialogTitle class="text-2xl font-bold">Request to Borrow</DialogTitle>
        <DialogDescription>
          Request to borrow a game by filling out the form below. Make sure to provide all the
          necessary details.
        </DialogDescription>
      </DialogHeader>
      <form class="flex flex-col gap-4" @submit.prevent="handleSubmit">
        <div class="flex gap-2 items-center">
          <Label for="comment" class="w-1/3">Comment:</Label>
          <Input id="comment" v-model="formData.comment" type="text" class="w-2/3" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="startDate" class="w-1/3">Start Date:</Label>
          <Input id="startDate" v-model="formData.startDate" type="date" class="w-2/3" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="endDate" class="w-1/3">End Date:</Label>
          <Input id="endDate" v-model="formData.endDate" type="date" class="w-2/3" />
        </div>
        <Button type="submit">Submit</Button>
      </form>
    </DialogContent>
    <div v-if="error" class="text-red-500 mt-2">
      {{ error }}
    </div>
  </Dialog>
</template>
