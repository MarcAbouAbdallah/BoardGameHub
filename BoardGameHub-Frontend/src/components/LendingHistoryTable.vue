<script setup lang="ts">
import CustomTableHeader from "@/components/TableHeader.vue";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import DataTableCard from "./DataTableCard.vue";
import { borrowRequestService } from "@/services/borrowService";
import { useAuthStore } from "../stores/authStore";
import { onMounted, ref } from "vue";

const authStore = useAuthStore();

interface BorrowRequest {
  id: string;
  gameTitle: string;
  requesterName: string;
  startDate: string;
  endDate: string;
  comment: string;
}

const loading = ref(false);
const error = ref("");
const borrowRequests = ref<BorrowRequest[]>([]);

onMounted(async () => {
  try {
    const userId = authStore.user?.userId;
    if (!userId) throw new Error("Requestee ID missing");
    borrowRequests.value = await borrowRequestService.getAcceptedRequestsByRequesterId(userId); // Only ACCEPTED
  } catch (err: any) {
    error.value = err.message || "Failed to load received requests.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="">
    <CustomTableHeader :title="'Lending History'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Requester</TableHead>
            <TableHead class="font-bold text-lg text-black">Start Date</TableHead>
            <TableHead class="font-bold text-lg text-black">End Date</TableHead>
            <TableHead class="font-bold text-lg text-black">Message</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody v-if="borrowRequests.length > 0">
          <template v-for="(request, index) in borrowRequests" :key="request.id">
            <TableRow :class="{ 'bg-gray-50': index % 2 === 0 }">
              <TableCell class="text-start">{{ request.gameTitle }}</TableCell>
              <TableCell class="text-start">{{ request.requesterName }}</TableCell>
              <TableCell class="text-start">{{ request.startDate }}</TableCell>
              <TableCell class="text-start">{{ request.endDate }}</TableCell>
              <TableCell class="text-start"> {{ request.comment || "No message" }} </TableCell>
            </TableRow>
          </template>
        </TableBody>
        <TableBody v-else>
          <TableRow>
            <TableCell colspan="6" class="text-center">
              You have not lent your games to anyone in the past
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
