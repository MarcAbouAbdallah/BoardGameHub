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
import { ref } from "vue";
import { NoSymbolIcon, CheckBadgeIcon } from "@heroicons/vue/24/solid";

import { sampleBorrowRequests } from "@/data/sampleGames";

const loading = ref(false);
const error = ref("");
const { toast } = useToast();

const acceptRequest = (requestId: number, gameId: number) => {
  //TODO: Implement the accept request logic
  toast({
    title: "Request Accepted",
    description: `Request ${requestId} for game ${gameId} has been accepted.`,
    variant: "default",
  });
};

const rejectRequest = (requestId: number, gameId: number) => {
  //TODO: Implement the accept request logic
  toast({
    title: "Request Rejected",
    description: `Request ${requestId} for game ${gameId} has been rejected.`,
    variant: "destructive",
  });
};
</script>

<template>
  <div class="p-6 w-9/12 space-y-6 mx-auto">
    <CustomTableHeader :title="'Received Borrow Requests For Your Games'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead class="font-bold text-lg text-black">Game Title</TableHead>
            <TableHead class="font-bold text-lg text-black">Requestee Name</TableHead>
            <TableHead class="font-bold text-lg text-black">Start Date</TableHead>
            <TableHead class="font-bold text-lg text-black">End Date</TableHead>
            <TableHead class="font-bold text-lg text-black">Status</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <template v-for="(request, index) in sampleBorrowRequests" :key="request.id">
            <TableRow :class="{ 'bg-gray-50': index % 2 === 0 }">
              <TableCell class="text-start">{{ request.game }}</TableCell>
              <TableCell class="text-start">{{ request.user }}</TableCell>
              <TableCell class="text-start">{{ request.startDate }}</TableCell>
              <TableCell class="text-start">{{ request.endDate }}</TableCell>
              <TableCell class="text-start"
                ><Badge variant="default">{{ request.comment }}</Badge></TableCell
              >
              <TableCell class="flex space-x-2">
                <Alert
                  :action-func="acceptRequest"
                  :action-text="'Accept'"
                  :description="'Are you sure you want to accept this request?'"
                >
                  <Button
                    variant="outline"
                    class="bg-green-700 hover:bg-green-900 text-white flex gap-2 items-center pl-2"
                  >
                    <CheckBadgeIcon class="h-4 w-4" />
                    Accept
                  </Button>
                </Alert>
                <Alert
                  :action-func="rejectRequest"
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
      </Table>
    </DataTableCard>
  </div>
</template>
