<script setup lang="ts">
import { useRouter } from "vue-router";
import { HomeIcon } from "@heroicons/vue/24/solid";
import { PuzzlePieceIcon } from "@heroicons/vue/16/solid";
import { CalendarIcon } from "@heroicons/vue/24/solid";
import { LogOut } from "lucide-vue-next";

const router = useRouter();
</script>

<template>
  <header>
    <nav>
      <div
        class="flex w-screen pl-20 pr-20 justify-between p-4 bg-[#F4F4F5] border-b-2 border-l-sky-100 text-black fixed top-0 left-0 z-50"
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
              class="text-black hover:text-gray-500 flex justify-center items-center"
              @click.prevent="router.push('/home')"
            >
              <HomeIcon class="h-6 w-6 inline-block mr-2" />
              <div>Home</div>
            </a>
          </li>
          <li>
            <a
              class="text-black hover:text-gray-500 flex justify-center items-center"
              href="#"
              @click.prevent="router.push('/games')"
            >
              <PuzzlePieceIcon class="h-6 w-6 inline-block mr-2" />
              <div>Games</div>
            </a>
          </li>
          <li>
            <a
              href="#"
              class="text-black hover:text-gray-500 flex justify-center items-center"
              @click.prevent="router.push('/events')"
            >
              <CalendarIcon class="h-6 w-6 inline-block mr-2" />
              <div>Events</div>
            </a>
          </li>
          <li>
            <a
              href="#"
              class="text-black hover:text-gray-500 flex justify-center items-center"
              @click.prevent="router.push('/')"
            >
              <LogOut class="h-6 w-6 inline-block mr-2" />
              <div>Logout</div>
            </a>
          </li>
        </ul>
      </div>
    </nav>
  </header>
</template>
