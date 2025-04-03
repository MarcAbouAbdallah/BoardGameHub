<script lang="ts">
export const description = "A signup page with form and image.";
</script>

<script setup lang="ts">
import SignupForm from "../../components/SignupForm.vue";
import { Toaster } from "@/components/ui/toast";
</script>

<template>
  <div
    class="flex min-h-svh flex-col items-center justify-center bg-muted p-6 md:p-10">
    <Toaster />
    <div class="w-full max-w-sm md:max-w-3xl">
      <SignupForm />
    </div>
  </div>
</template>
