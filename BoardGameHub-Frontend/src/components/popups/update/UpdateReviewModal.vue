<script setup lang="ts">
import { ref } from "vue";
//import { useToast } from "@/components/ui/toast/use-toast";
import { Pen } from "lucide-vue-next";
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


const props = defineProps({
  review: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(["update"]);

const isOpen = ref(false);
//const { toast } = useToast();

const close = () => {
  isOpen.value = false;
};

const formData = ref({
  rating: props.review.rating ?? 0,
  comment: props.review.comment ?? "",
});

const error = ref("");

const handleSubmit = () => {
  emit("update", {
    id: props.review.id,
    rating: formData.value.rating,
    comment: formData.value.comment,
  });
  close(); // Toast on success and failure is in the parent comp
};


</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button variant="outline" class="ml-2">
        <Pen class="h-4 w-4" />
        Update Review
      </Button>
    </DialogTrigger>
    <DialogContent :close="close">
      <DialogHeader>
        <DialogTitle class="text-2xl font-bold">Update Your Review</DialogTitle>
        <DialogDescription>
          Modify your review below. Leave any field blank if you want to keep the current value.
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
