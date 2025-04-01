<script setup lang="ts">
import { ref } from "vue";
import {
  AlertDialog,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

const isOpen = ref(false);

const props = defineProps<{
  trigger: string;
  description: string;
  actionText: string;
  class?: string;
  actionFunc: any;
}>();

const onAction = () => {
  props.actionFunc();
  isOpen.value = false;
};
</script>

<template>
  <AlertDialog v-model:open="isOpen">
    <AlertDialogTrigger asChild>
      <button :class="props.class">{{ trigger }}</button>
    </AlertDialogTrigger>
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Are you sure?</AlertDialogTitle>
        <AlertDialogDescription>{{ description }}</AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel>Cancel</AlertDialogCancel>
        <button @click="onAction" class="text-white px-4 py-2 text-sm">
          {{ actionText }}
        </button>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
</template>
