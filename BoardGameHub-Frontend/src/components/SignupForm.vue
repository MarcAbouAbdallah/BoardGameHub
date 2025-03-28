<script setup lang="ts">
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useRouter } from "vue-router";

const router = useRouter();
</script>

<template>
  <div class="flex flex-col gap-6">
    <Card class="overflow-hidden">
      <CardContent class="grid p-0 md:grid-cols-2">
        <form class="p-6 md:p-8">
          <div class="flex flex-col gap-6">
            <div class="flex flex-col items-center text-center">
              <h1 class="text-2xl font-bold">Welcome</h1>
              <p class="text-balance text-muted-foreground">
                Create your BoardGameHub account
              </p>
            </div>
            <div class="grid gap-2 text-left">
              <Label for="email">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="m@example.com"
                required
              />
            </div>
            <div class="grid gap-2">
              <div class="flex items-center">
                <Label for="password">Password</Label>
              </div>
              <Input
                id="password"
                type="password"
                required
              />
            </div>
            <Button
              type="submit"
              class="w-full"
              @click.prevent="router.push('/home')"
            >
              Login
            </Button>
            <div class="text-center text-sm">
              Already have an account?
              <a
                href="#"
                class="underline underline-offset-4"
                @click.prevent="router.push('/')"
              >
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
    <div
      class="text-balance text-center text-xs text-muted-foreground [&_a]:underline [&_a]:underline-offset-4 hover:[&_a]:text-primary"
    >
      By clicking continue, you agree to our
      <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.
    </div>
  </div>
</template>
