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
import Alert from "./alert/Alert.vue";
import { useToast } from "./ui/toast";
import { NoSymbolIcon, CheckBadgeIcon } from "@heroicons/vue/24/solid";
import { borrowRequestService } from "@/services/borrowService";
import { useAuthStore } from "../stores/authStore";
import { onMounted, ref } from "vue";
import { Button } from "./ui/button";

const { toast } = useToast();
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
    borrowRequests.value = await borrowRequestService.getPendingRequestsByRequesteeId(userId); // Only PENDING
  } catch (err: any) {
    error.value = err.message || "Failed to load received requests.";
  } finally {
    loading.value = false;
  }
});

const updateRequestStatus = async (
  requestId: string,
  gameTitle: string,
  newStatus: "ACCEPTED" | "DECLINED",
) => {
  try {
    await borrowRequestService.respondToBorrowRequest(requestId, newStatus);

    borrowRequests.value = borrowRequests.value.filter((r: any) => r.id !== requestId); // Removing the request after action

    toast({
      title: `Request ${newStatus}`,
      description: `You have ${newStatus.toLowerCase()} the request for ${gameTitle}.`,
      variant: newStatus === "ACCEPTED" ? "default" : "destructive",
    });
  } catch (err: any) {
    toast({
      title: `Failed to ${newStatus.toLowerCase()} request`,
      description: err.message || "Unexpected error",
      variant: "destructive",
    });
  }
};
</script>

<template>
  <div class="">
    <CustomTableHeader :title="'My Received Requests'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Requester</TableHead>
            <TableHead class="font-bold text-lg text-black">Start Date</TableHead>
            <TableHead class="font-bold text-lg text-black">End Date</TableHead>
            <TableHead class="font-bold text-lg text-black">Message</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
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
              <TableCell class="flex space-x-2">
                <Alert
                  :action-func="
                    () => updateRequestStatus(request.id, request.gameTitle, 'ACCEPTED')
                  "
                  :action-text="'Accept'"
                  :description="'Are you sure you want to accept this request?'"
                >
                  <Button
                    variant="outline"
                    class="bg-green-700 hover:bg-green-900 text-white hover:text-white flex gap-2 items-center pl-2"
                  >
                    <CheckBadgeIcon class="h-4 w-4" />
                    Accept
                  </Button>
                </Alert>
                <Alert
                  :action-func="
                    () => updateRequestStatus(request.id, request.gameTitle, 'DECLINED')
                  "
                  :action-text="'Reject'"
                  :description="'Are you sure you want to reject this request?'"
                >
                  <Button
                    variant="destructive"
                    class="bg-red-700 hover:bg-red-900 text-white flex gap-2 items-center pl-2"
                  >
                    <NoSymbolIcon class="h-4 w-4" />
                    Reject
                  </Button>
                </Alert>
              </TableCell>
            </TableRow>
          </template>
        </TableBody>
        <TableBody v-else>
          <TableRow>
            <TableCell colspan="6" class="text-center py-8 text-muted-foreground">
              No incoming requests found.
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
