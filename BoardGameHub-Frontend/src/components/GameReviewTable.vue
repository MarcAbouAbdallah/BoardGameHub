<script setup lang="ts">
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
import { ref, onMounted } from "vue";
import UpdateReviewModal from "./popups/update/UpdateReviewModal.vue";
import { reviewService } from "@/services/reviewService";
import { useAuthStore } from "../stores/authStore";
import { useToast } from "@/components/ui/toast/use-toast";

const loading = ref(false);
const error = ref("");
interface Review {
  id: number;
  gameName: string;
  rating: number;
  comment: string;
}

const reviews = ref<Review[]>([]);

const { toast } = useToast();
const authStore = useAuthStore();

onMounted(async () => {
  try {
    const playerId = authStore.user.userId;
    if (!playerId) throw new Error("Player ID not found");
    reviews.value = await reviewService.getReviewsByPlayerId(playerId);
    console.log(reviews.value);
  } catch (err: any) {
    error.value = err || "Error fetching reviews";
  } finally {
    loading.value = false;
  }
});

const handleDelete = async (reviewId: number) => {
  try {
    await reviewService.deleteReview(reviewId, authStore.user.userId!);
    reviews.value = reviews.value.filter((r: any) => r.id !== reviewId);

    toast({
      title: "Review Deleted",
      description: "The review has been removed.",
      variant: "destructive",
    });
  } catch (err: any) {
    const errorMsg = err.response?.data?.message || err.message || "Failed to delete review.";
    toast({
      title: "Delete Failed",
      description: errorMsg,
      variant: "destructive",
    });
  }
};

const handleUpdate = async (updatedReview: any) => {
  try {
    const res = await reviewService.updateReview(updatedReview.id, authStore.user.userId!, {
      rating: updatedReview.rating,
      comment: updatedReview.comment,
    });

    reviews.value = reviews.value.map((r: any) => (r.id === updatedReview.id ? res : r));

    toast({
      title: "Review updated!",
      description: "Your review was successfully updated.",
      variant: "default",
    });
  } catch (err: any) {
  toast({
    title: "Update Failed",
    description: err,
    variant: "destructive",
  });
}

};
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
        <TableBody v-if="reviews.length > 0">
          <template v-for="review in reviews" :key="review.id">
            <TableRow>
              <TableCell class="text-start">{{ review.gameName }}</TableCell>
              <TableCell class="text-start">
                <rating :ratingValue="review.rating.toString()" />
              </TableCell>
              <TableCell class="text-start max-w-[700px]">{{ review.comment }}</TableCell>
              <TableCell class="text-start">
                <Button variant="destructive" class="mr-2" @click="handleDelete(review.id)">
                  <Trash class="h-4 w-4" />
                  Remove
                </Button>
                <UpdateReviewModal :review="review" @update="handleUpdate" />
              </TableCell>
            </TableRow>
          </template>
        </TableBody>
        <TableBody v-else>
          <TableRow>
            <TableCell colspan="6" class="text-center"> No reviews found. </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
