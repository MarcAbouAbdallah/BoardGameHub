<script setup lang="ts">
import { isVNode } from "vue";
import { Toast, ToastClose, ToastDescription, ToastProvider, ToastTitle, ToastViewport } from ".";
import { useToast } from "./use-toast";

const { toasts } = useToast();
</script>

<template>
  <ToastProvider>
    <Toast v-for="toast in toasts" :key="toast.id" v-bind="toast">
      <div class="flex flex-col justify-start gap-1">
        <ToastTitle v-if="toast.title" class="text-start">
          {{ toast.title }}
        </ToastTitle>
        <template v-if="toast.description">
          <ToastDescription v-if="isVNode(toast.description)">
            <component :is="toast.description" />
          </ToastDescription>
          <ToastDescription v-else>
            {{ toast.description }}
          </ToastDescription>
        </template>
        <ToastClose class="bg-white" />
      </div>
      <component :is="toast.action" />
    </Toast>
    <ToastViewport />
  </ToastProvider>
</template>
