<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();
</script>

<template>
  <header>
    <nav>
      <div
        class="flex w-screen pl-20 pr-20 justify-between p-4 bg-gray-800 text-white fixed top-0 left-0 z-50"
      >
        <div class="flex items-center space-x-4">
          <img
            src="../assets/logo.svg"
            alt="Logo"
            class="h-10 w-10 rounded-full"
          />
          <span class="text-2xl font-bold">BoardGameHub</span>
        </div>
        <ul class="flex space-x-10 items-center">
          <li>
            <a
              href="#"
              class="text-white hover:text-gray-300"
              @click.prevent="router.push('/home')"
              >Home</a
            >
          </li>
          <li>
            <a
              class="text-white hover:text-gray-300"
              href="#"
              @click.prevent="router.push('/games')"
              >Games</a
            >
          </li>
          <li>
            <a
              href="#"
              class="text-white hover:text-gray-300"
              @click.prevent="router.push('/events')"
              >Events</a
            >
          </li>
          <li>
            <a
              href="#"
              class="text-white hover:text-gray-300"
              @click.prevent="router.push('/')"
              >Logout</a
            >
          </li>
        </ul>
      </div>
    </nav>
  </header>
</template>
