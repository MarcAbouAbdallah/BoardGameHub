<script setup lang="ts">
import { ref } from "vue";
import { useToast } from "@/components/ui/toast/use-toast";
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
const { toast } = useToast();

const close = () => {
  isOpen.value = false;
  console.log("Dialog closed");
};

const formData = ref({
  rating: 0,
  comment: "",
});

const error = ref("");

const handleSubmit = async () => {
  try {
    console.log("Form Data:", formData.value);
    close();
    toast({
      title: "Review Submitted",
      description: `Your review has been submitted.`,
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
      <Button variant="outline" class="border-black">Add a Review</Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Create Review</DialogTitle>
        <DialogDescription>
          Create a new review by filling out the form below. Make sure to provide all the necessary
          details.
        </DialogDescription>
      </DialogHeader>
      <form class="flex flex-col gap-4" @submit.prevent="handleSubmit">
        <div class="flex gap-2 items-center">
          <Label for="rating" class="w-1/3">Rating:</Label>
          <Input type="number" id="rating" v-model="formData.rating" class="w-2/3" />
        </div>
        <div class="flex gap-2 items-center">
          <Label for="comment" class="w-1/3">Comment:</Label>
          <Input id="comment" v-model="formData.comment" type="text" class="w-2/3" />
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <Button type="button" variant="secondary" @click="close"> Cancel </Button>
          <Button type="submit">Submit</Button>
        </div>
        <p v-if="error" class="text-red-500">
          {{ error }}
        </p>
      </form>
    </DialogContent>
  </Dialog>
</template>
