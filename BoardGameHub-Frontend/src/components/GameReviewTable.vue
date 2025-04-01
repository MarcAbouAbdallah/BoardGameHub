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

const loading = ref(false);
const error = ref("");

const props = defineProps({
  gameReviews: {
    type: Object,
    required: true,
  },
});
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
          <template v-for="review in props.gameReviews" :key="review.id">
            <TableRow>
              <TableCell class="text-start">{{ review.game }}</TableCell>
              <TableCell class="text-start">
                <rating :ratingValue="review.reviewId.toString()" />
              </TableCell>
              <TableCell class="text-start max-w-[700px]">{{ review.reviewText }}</TableCell>
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
