<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Button } from "./ui/button";
import CustomTableHeader from "../components/TableHeader.vue";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import DataTableCard from "./DataTableCard.vue";
import Badge from "./ui/badge/Badge.vue";

import { borrowRequestService } from "@/services/borrowService";
import { useAuthStore } from "../stores/authStore";
import { useToast } from "@/components/ui/toast/use-toast";

const loading = ref(false);
const error = ref("");
interface BorrowRequest {
  id: string;
  gameTitle: string;
  requesteeName: string;
  startDate: string;
  endDate: string;
  status: string;
  computedStatus: string; // Status itself or RETURNED (if accepted request has expired)
}

const borrowRequests = ref<BorrowRequest[]>([]);
const { toast } = useToast();
const authStore = useAuthStore();

onMounted(async () => {
  try {
    const playerId = authStore.user?.userId;
    if (!playerId) throw new Error("Player ID not found");

    const fetched = await borrowRequestService.getRequestsByRequesterId(playerId);
    const today = new Date().toISOString().split("T")[0];

    // Computed status is the same as status unless the request is accepted and the end date has passed (it is returned in that case)
    borrowRequests.value = fetched.map((req: any) => {
      const isReturned = req.status === "ACCEPTED" && req.endDate < today;
      return {
        ...req,
        computedStatus: isReturned ? "RETURNED" : req.status,
      };
    });
  } catch (err: any) {
    error.value = err || "Failed to load borrow requests";
  } finally {
    loading.value = false;
  }
});

const handleCancel = async (requestId: string) => {
  try {
    const userId = authStore.user?.userId;
    if (!userId) throw new Error("User ID not found");

    await borrowRequestService.cancelBorrowRequest(requestId, userId);

    // Remove request from table when cancelled
    borrowRequests.value = borrowRequests.value.filter((r: any) => r.id !== requestId);

    toast({
      title: "Request Cancelled",
      description: "Your borrow request has been successfully cancelled.",
      variant: "default",
    });
  } catch (err: any) {
    const errorMsg =
      err.response?.data?.message || err.message || "Failed to cancel borrow request.";

    toast({
      title: "Cancellation Failed",
      description: errorMsg,
      variant: "destructive",
    });
  }
};
</script>

<template>
  <div class="">
    <CustomTableHeader :title="'My Sent Requests'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Owner</TableHead>
            <TableHead class="font-bold text-lg text-black">Start Date</TableHead>
            <TableHead class="font-bold text-lg text-black">End Date</TableHead>
            <TableHead class="font-bold text-lg text-black">Status</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody v-if="borrowRequests.length > 0">
          <template v-for="request in borrowRequests" :key="request.id">
            <TableRow>
              <TableCell class="text-start">{{ request.gameTitle }}</TableCell>
              <TableCell class="text-start">{{ request.requesteeName }}</TableCell>
              <TableCell class="text-start">{{ request.startDate }}</TableCell>
              <TableCell class="text-start">{{ request.endDate }}</TableCell>
              <TableCell class="text-start">
                <Badge v-if="request.computedStatus === 'PENDING'" class="bg-gray-500"
                  >Pending</Badge
                >
                <Badge v-else-if="request.computedStatus === 'ACCEPTED'" class="bg-green-800"
                  >Accepted</Badge
                >
                <Badge v-else-if="request.computedStatus === 'RETURNED'" class="bg-blue-800"
                  >Returned</Badge
                >
                <Badge v-else variant="destructive">Rejected</Badge>
              </TableCell>
              <TableCell class="text-start">
                <Button
                  v-if="request.computedStatus === 'PENDING'"
                  variant="outline"
                  @click="handleCancel(request.id)"
                >
                  Cancel
                </Button>
              </TableCell>
            </TableRow>
          </template>
        </TableBody>
        <TableBody v-else>
          <TableRow>
            <TableCell colspan="6" class="text-center"> No outgoing requests found. </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
