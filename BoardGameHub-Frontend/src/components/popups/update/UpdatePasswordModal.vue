<script setup lang="ts">
import { Button } from "@/components/ui/button";
import { Pen } from "lucide-vue-next";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogClose,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ref } from "vue";

const formData = ref({
  currentPassword: "",
  newPassword: "",
  confirmNewPassword: "",
});

const isOpen = ref(false);
const close = () => {
  isOpen.value = false;
  console.log("Dialog closed");
};

const handleSubmit = async () => {
  try {
    //TODO: Add change password logic here
    // Call the API to update the password
    // await gameService.updatePassword(formData.value);
    console.log("Password updated successfully", formData.value);
    close();
  } catch (error) {
    console.error("Error updating password", error);
  }
};
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogTrigger as-child>
      <Button variant="outline">
        <Pen class="mr-2 h-4 w-4" />
        Change your Password
      </Button>
    </DialogTrigger>
    <DialogContent :close="close" class="w-[450px]">
      <DialogHeader>
        <DialogTitle>Change Your Password</DialogTitle>
        <DialogDescription class="text-sm text-gray-500">
          Please enter your current password and the new password you want to set.
        </DialogDescription>
      </DialogHeader>
      <div class="grid gap-4">
        <div class="grid gap-2">
          <Label for="currentPassword">Current Password</Label>
          <Input
            required
            id="currentPassword"
            type="password"
            v-model="formData.currentPassword"
            placeholder="Enter your current password"
          />
        </div>
        <div class="grid gap-2">
          <Label for="newPassword">New Password</Label>
          <Input
            required
            id="newPassword"
            type="password"
            v-model="formData.newPassword"
            placeholder="Enter your new password"
          />
        </div>
        <div class="grid gap-2">
          <Label for="confirmNewPassword">Confirm New Password</Label>
          <Input
            required
            id="confirmNewPassword"
            type="password"
            v-model="formData.confirmNewPassword"
            placeholder="Confirm your new password"
          />
        </div>
      </div>
      <div class="flex gap-2 items-end flex-end justify-end">
        <Button variant="outline" @click="handleSubmit">Update Password</Button>
        <DialogClose class="w-fit p-0 mt-4">
          <Button>Cancel</Button>
        </DialogClose>
      </div>
    </DialogContent>
  </Dialog>
</template>
