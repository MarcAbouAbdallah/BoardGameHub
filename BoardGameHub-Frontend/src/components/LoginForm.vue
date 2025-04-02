<script setup lang="ts">
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/authStore";
import { playerService } from "../services/PlayerService";
import { useToast } from "@/components/ui/toast";
import { AxiosError } from "axios";

const router = useRouter();
const authStore = useAuthStore();
const email = ref("");
const password = ref("");
const { toast } = useToast();

const handleSubmit = async () => {
  try {
    const playerData = {
      email: email.value,
      password: password.value,
    };
    console.log(playerData);
    const response = await playerService.loginPlayer(playerData);
    console.log(response);

    if (response.status === 200) {
      authStore.login(response.data.name, response.data.email, response.data.id, playerData.password);
      console.log("User logged in:", authStore.user);
      
      toast({ title: "Success", description: "Login successful!", variant: "default", duration: 3000 });
      router.push("/home");
    } 
    else {
      toast({ title: "Error", description: "Invalid email or password.", variant: "destructive", duration: 5000 });
    }

  } 
  catch (err: unknown) {
    console.error("Error creating player:", err);
    if (err instanceof AxiosError) {
      if (err.response) {
        if (err.response.status === 404){
          toast({ title: "Error", description: "User does not exists. Please try again.", variant: "destructive", duration: 5000 });
        }
        else if (err.response.status === 500){
          toast({ title: "Error", description: "Invalid password.", variant: "destructive", duration: 5000 });
        }
        else {
        toast({ title: "Error", description: "Something went wrong. Please try again.", variant: "destructive", duration: 5000 });
        }
      }
    }
  }
};
</script>

<template>
  <div class="flex flex-col gap-6">
    <Card class="overflow-hidden">
      <CardContent class="grid p-0 md:grid-cols-2">
        <form class="p-6 md:p-8" @submit.prevent="handleSubmit">
          <div class="flex flex-col gap-6">
            <div class="flex flex-col items-center text-center">
              <h1 class="text-2xl font-bold">Welcome back</h1>
              <p class="text-balance text-muted-foreground">Login to your BoardGameHub account</p>
            </div>
            <div class="grid gap-2 text-left">
              <Label for="email">Email</Label>
              <Input v-model="email" id="email" type="email" placeholder="m@example.com" required />
            </div>
            <div class="grid gap-2">
              <div class="flex items-center">
                <Label for="password">Password</Label>
              </div>
              <Input
                v-model="password"
                placeholder="Enter your password"
                id="password"
                type="password"
                required
              />
            </div>
            <Button type="submit" class="w-full text-white"> Login </Button>

            <div class="text-center text-sm">
              Don&apos;t have an account?
              <a
                href="#"
                class="underline underline-offset-4"
                @click.prevent="router.push('/signup')"
              >
                Sign up
              </a>
            </div>
          </div>
        </form>
        <div class="relative hidden bg-muted md:block">
          <img
            src="https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzZ6dXh6dXA2dDZ5a3NtbmlndDc1and5NzZ4ZHY0YTlpODFzazY2MSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/YksKDfVpJOaz3Mrgb0/giphy.gif"
            alt="Image"
            class="absolute inset-0 h-full w-full object-contain dark:brightness-[0.2] dark:grayscale"
          />
        </div>
      </CardContent>
    </Card>
    <div
      class="text-balance text-center text-xs text-muted-foreground [&_a]:underline [&_a]:underline-offset-4 hover:[&_a]:text-primary"
    >
      By clicking continue, you agree to our
      <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.
    </div>
  </div>
</template>