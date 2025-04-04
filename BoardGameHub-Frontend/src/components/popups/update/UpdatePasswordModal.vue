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
import { toast } from "@/components/ui/toast";
import { useAuthStore } from "@/stores/authStore";
import { playerService } from "@/services/PlayerService";

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

const user = useAuthStore().user;

const handleSubmit = async () => {
  try {
    if (formData.value.currentPassword == "" || formData.value.newPassword == "" || formData.value.confirmNewPassword == "") {
      toast({
        title: "Error",
        description: "Please fill in all the password fields.",
        variant: "destructive",
      });
      return;
    }

    if (formData.value.currentPassword !== user.userPassword) {
      toast({
        title: "Error",
        description: "Current password do not match user password.",
        variant: "destructive",
      });
      return;
    }

    if (formData.value.currentPassword == formData.value.newPassword) {
      toast({
        title: "Error",
        description: "New password can not be same as current password.",
        variant: "destructive",
      });
      return;
    }

    if (formData.value.newPassword !== formData.value.confirmNewPassword) {
      toast({
        title: "Error",
        description: "New password and confirmation do not match.",
        variant: "destructive",
      });
      return;
    }

    if (user?.userId) {
      const player = {
      password: formData.value.newPassword,
      };
      await playerService.updatePlayer(user.userId, player);
    toast({
      title: "Success",
      description: "Your password has been updated.",
      variant: "default",
    });

    useAuthStore().changeUserPassword(formData.value.newPassword);
    close();
    formData.value.currentPassword = "";
    formData.value.newPassword = "";
    formData.value.confirmNewPassword = "";
    }
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
