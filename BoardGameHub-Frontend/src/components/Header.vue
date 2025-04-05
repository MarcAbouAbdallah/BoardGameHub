<script setup lang="ts">
import { useRouter } from "vue-router";
import { HomeIcon } from "@heroicons/vue/24/solid";
import { PuzzlePieceIcon } from "@heroicons/vue/16/solid";
import { CalendarIcon } from "@heroicons/vue/24/solid";
import { LogOut } from "lucide-vue-next";
import { Avatar } from "./ui/avatar";
import { useAuthStore } from "../stores/authStore";
import EditProfileSheet from "./EditProfileSheet.vue";

const router = useRouter();
const authStore = useAuthStore();

const navItems = [
  { label: "Home", icon: HomeIcon, route: "/home" },
  { label: "Games", icon: PuzzlePieceIcon, route: "/games" },
  { label: "Events", icon: CalendarIcon, route: "/events" },
  { label: "Logout", icon: LogOut, route: "/" },
];
</script>

<template>
  <header>
    <nav>
      <div
        class="flex w-screen pl-20 pr-20 justify-between p-4 bg-[#F4F4F5] border-b-2 border-l-sky-100 text-black fixed top-0 left-0 z-50"
      >
        <div class="flex items-center space-x-4">
          <img src="../assets/logo.svg" alt="Logo" class="h-10 w-10 rounded-full" />
          <span class="text-2xl font-bold">BoardGameHub</span>
        </div>
        <ul class="flex space-x-10 items-center">
          <li v-for="(item, index) in navItems" :key="index">
            <a
              href="#"
              class="text-black hover:text-gray-500 flex justify-center items-center"
              @click.prevent="router.push(item.route)"
            >
              <component :is="item.icon" class="h-6 w-6 inline-block mr-2" />
              <div>{{ item.label }}</div>
            </a>
          </li>
          <li>
            <EditProfileSheet>
              <div class="flex items-center space-x-2">
                <span v-if="authStore.user.isGameOwner" 
                      class="text-xs font-bold px-1.5 py-0.5 rounded-full bg-green-200 text-green-900">
                  GameOwner
                </span>
                <span class="max-w-[300px] truncate block">
                  {{ authStore.user.username }}
                </span>
                <Avatar class="bg-[#F4F4F5] text-xl hover:text-gray-500 font-bold">
                  {{
                    authStore.user.username ? authStore.user.username.charAt(0).toUpperCase() : "U"
                  }}
                </Avatar>
              </div>
            </EditProfileSheet>
          </li>
        </ul>
      </div>
    </nav>
  </header>
</template>
