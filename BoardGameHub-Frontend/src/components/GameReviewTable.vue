<script setup lang="ts">
import { defineProps } from "vue";
import CustomTableHeader from "../components/TableHeader.vue";
import rating from "./ui/rating/rating.vue";
import { Button } from "./ui/button";
import { Trash } from "lucide-vue-next";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import DataTableCard from "./DataTableCard.vue";
import { ref } from "vue";
import UpdateReviewModal from "./popups/update/UpdateReviewModal.vue";
import { reviewService } from "@/services/reviewService";
import { useAuthStore } from "../stores/authStore";
import { onMounted } from "vue";

const loading = ref(false);
const error = ref("");
const reviews = ref([]);

const authStore = useAuthStore();

onMounted(async () => {
  try {
    const playerId = authStore.user.userId;
    reviews.value = await reviewService.getReviewsByPlayerId(playerId);
  } catch (err: any) {
    error.value = err.message || "Error fetching reviews";
  } finally {
    loading.value = false;
  }
});

/*
const props = defineProps({
  gameReviews: {
    type: Object,
    required: true,
  },
});
*/
</script>

<template>
  <div class="p-6 space-y-6 w-9/12 mx-auto">
    <CustomTableHeader :title="'My Game Reviews'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Rating</TableHead>
            <TableHead class="font-bold text-lg text-black">Review</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <template v-for="review in reviews" :key="review.id">
            <TableRow>
              <TableCell class="text-start">{{ review.gameName }}</TableCell>
              <TableCell class="text-start">
                <rating :ratingValue="review.rating.toString()" />
              </TableCell>
              <TableCell class="text-start max-w-[700px]">{{ review.comment }}</TableCell>
              <TableCell class="text-start">
                <Button variant="destructive">
                  <Trash class="h-4 w-4" />
                  Remove
                </Button>
                <UpdateReviewModal />
              </TableCell>
            </TableRow>
          </template>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
