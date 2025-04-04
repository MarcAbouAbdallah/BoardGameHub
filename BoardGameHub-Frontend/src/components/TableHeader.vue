<script setup lang="ts">
import { defineProps } from "vue";
import { Button } from "./ui/button";
import { Plus } from "lucide-vue-next";
import { useRouter } from "vue-router";

const props = defineProps({
  title: {
    type: String,
    required: true,
  },
});

const router = useRouter();

const isGameHeader = props.title.toString().includes("My Games");
</script>

<template>
  <div class="flex justify-between items-center">
    <h1 class="text-2xl font-bold">{{ props.title }}</h1>
    <div class="flex-1 mx-10"></div>
    <Button v-if="isGameHeader" @click="router.push('/games')">
      <Plus class="w-4 h-4" />
      Add Games to your Collection
    </Button>
  </div>
</template>
