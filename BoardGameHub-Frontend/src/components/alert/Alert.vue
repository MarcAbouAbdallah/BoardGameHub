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

import Button from "../ui/button/Button.vue";

const isOpen = ref(false);

const props = defineProps<{
  description: string;
  actionText: string;
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
      <slot />
    </AlertDialogTrigger>
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>Are you sure?</AlertDialogTitle>
        <AlertDialogDescription>{{ description }}</AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel>Cancel</AlertDialogCancel>
        <Button @click="onAction" variant="default" class="px-4 py-2 text-sm">
          {{ actionText }}
        </Button>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
</template>
