<script setup lang="ts">
import { ref } from "vue";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { useToast } from "../ui/toast/use-toast";

const isOpen = ref(false);
const { toast } = useToast();

const close = () => {
  isOpen.value = false;
  console.log("Dialog closed");
};

const formData = ref({
  comment: "",
  startDate: "",
  endDate: "",
});
const error = ref("");

const handleSubmit = async () => {
  try {
    console.log("Form Data:", formData.value);
    isOpen.value = false;
    toast({
      title: "Request Submitted",
      description: `Your request to borrow a game has been submitted.`,
      variant: "default",
      duration: 2000,
    });
    // Call the APID to create the event here
  } catch (err) {
    error.value = "An error occurred. Please try again.";
  }
};
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button>Request to Borrow</Button>
    </DialogTrigger>
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
