<script setup lang="ts">
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { useAuthStore } from "@/stores/authStore";
import { Button } from "@/components/ui/button";
import { Check, Pen, X } from "lucide-vue-next";
import { computed, ref } from "vue";
import { Input } from "./ui/input";
import UpdatePasswordModal from "./popups/update/UpdatePasswordModal.vue";
import { playerService } from "@/services/PlayerService";

const isUserNameEditing = ref(false);
const isEmailEditing = ref(false);

const userName = ref("");
const email = ref("");

const user = useAuthStore().user;

const usernameTitle = computed(() => user.username ?? "");
const emailTitle = computed(() => user.userEmail ?? "");

const handleUserNameChange = async () => {
  try {
    if (user?.userId) {
      const player = {
        name: userName.value,
      };
      await playerService.updatePlayer(user.userId , player);

      useAuthStore().changeUsername(userName.value);
      isUserNameEditing.value = false;
    } 
  } catch (error) {
    console.error("Failed to update username:", error);
  }
};

const handleEmailChange = async () => {
  try {
    if (user?.userId) {
      const player = {
        email: email.value,
      };
      await playerService.updatePlayer(user.userId , player);
      useAuthStore().changeUserEmail(email.value);
      isEmailEditing.value = false;
    }
  } catch (error) {
    console.error("Failed to update email:", error);
  }
};
</script>

<template>
  <Sheet>
    <SheetTrigger
      class="text-black font-semibold gap-2 cursor-pointer hover:text-gray-500 flex justify-center items-center bg-transparent p-0 border-none active:border-none px-2"
    >
      <slot></slot>
    </SheetTrigger>
    <SheetContent class="">
      <SheetHeader class="flex items-center">
        <SheetTitle class="text-2xl">Your Profile</SheetTitle>
      </SheetHeader>
      <div class="flex flex-col justify-center items-center">
        <img
          src="https://img.freepik.com/premium-vector/vector-flat-illustration-black-color-avatar-user-profile-person-icon-gender-neutral-silhouette-profile-picture-suitable-social-media-profiles-icons-screensavers-as-templatex9xa_719432-859.jpg"
          alt="profile_icon"
          class="w-40 object-cover"
        />
      </div>
      <SheetDescription class="text-center text-ellipsis font-bold text-md my-2">
        Edit your profile information here.
      </SheetDescription>
      <div class="my-10 flex flex-col gap-5">
        <div class="flex flex-col items-start gap-2">
          <div class="text-lg font-semibold">Username:</div>
          <div class="flex justify-between w-full" v-if="!isUserNameEditing">
            <div class="text-lg truncate max-w-[300px] overflow-hidden whitespace-nowrap"
              :title="usernameTitle">{{ user.username }}</div>
            <Button
              class="flex"
              variant="outline"
              size="sm"
              :onClick="() => (isUserNameEditing = !isUserNameEditing)"
            >
              <Pen class="h-4 w-4" />
            </Button>
          </div>
          <div class="flex justify-between w-full" v-else>
            <Input
              v-if="isUserNameEditing"
              class="mr-2"
              type="text"
              placeholder="Enter new username"
              v-model="userName"
            />
            <div class="flex gap-2">
              <Button class="h-[36px]" variant="outline" size="sm" @click="handleUserNameChange">
                <Check class="h-4 w-4" />
              </Button>
              <Button
                class="h-[36px]"
                variant="outline"
                size="sm"
                @click="isUserNameEditing = false"
              >
                <X class="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
        <div class="flex flex-col items-start gap-2">
          <div class="text-lg font-semibold">Email:</div>
          <div class="flex justify-between w-full" v-if="!isEmailEditing">
            <div class="text-lg truncate max-w-[300px] overflow-hidden whitespace-nowrap"
              :title="emailTitle">{{ user.userEmail }}</div>
            <Button
              class="flex"
              variant="outline"
              size="sm"
              :onClick="() => (isEmailEditing = !isEmailEditing)"
            >
              <Pen class="h-4 w-4" />
            </Button>
          </div>
          <div class="flex justify-between w-full" v-else>
            <Input
              v-if="isEmailEditing"
              type="text"
              placeholder="Enter new email"
              v-model="email"
              class="mr-2"
            />
            <div class="flex gap-2">
              <Button class="h-[36px]" variant="outline" size="sm" @click="handleEmailChange">
                <Check class="h-4 w-4" />
              </Button>
              <Button class="h-[36px]" variant="outline" size="sm" @click="isEmailEditing = false">
                <X class="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>
        <UpdatePasswordModal />
      </div>
    </SheetContent>
  </Sheet>
</template>
