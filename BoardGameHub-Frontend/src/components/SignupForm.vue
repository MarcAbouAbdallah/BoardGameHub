<script setup lang="ts">
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useRouter } from "vue-router";
import { playerService } from "@/services/PlayerService";
import { ref } from "vue";

const router = useRouter();
const name = ref("");
const email = ref("");
const password = ref("");

const handleSubmit = async () => {
  try {
    const player = {
      name: name.value,
      email: email.value,
      password: password.value,
    };
    await playerService.createPlayer(player);
    router.push("/");
  } catch (err: any) {
    console.log("Error Signing up the player", err);
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
              <h1 class="text-2xl font-bold">Welcome</h1>
              <p class="text-balance text-muted-foreground">Create your BoardGameHub account</p>
            </div>
            <div class="grid gap-2 text-left">
              <Label for="name">Username</Label>
              <Input
                v-model="name"
                placeholder="Enter your username"
                id="name"
                type="name"
                required
              />
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
                id="password"
                type="password"
                placeholder="Enter your password"
                required
              />
            </div>
            <Button type="submit" class="w-full"> Create an account </Button>
            <div class="text-center text-sm">
              Already have an account?
              <a href="#" class="underline underline-offset-4" @click.prevent="router.push('/')">
                Login now
              </a>
            </div>
          </div>
        </form>
        <div class="relative hidden bg-muted md:block">
          <img
            src="https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExc2ZwdG9wcnl5cnFtMzAya2JyMXVybWg2bWNzYmV1ejNhcTczMGh6cSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9cw/m9bsCbfsPJgfSZ9EN1/giphy.gif"
            alt="Image"
            class="absolute inset-0 h-full w-full object-contain dark:brightness-[0.2] dark:grayscale"
          />
        </div>
      </CardContent>
    </Card>
  </div>
</template>
