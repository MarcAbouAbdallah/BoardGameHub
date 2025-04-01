<script setup lang="ts">
import { defineProps, ref } from "vue";
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

const loading = ref(false);
const error = ref("");

const props = defineProps({
  BorrowRequests: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <div class="p-6 space-y-6 w-9/12 mx-auto">
    <CustomTableHeader :title="'My Borrow Requests'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Owner</TableHead>
            <TableHead class="font-bold text-lg text-black">Start Date</TableHead>
            <TableHead class="font-bold text-lg text-black">End Date</TableHead>
            <TableHead class="font-bold text-lg text-black">Status</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <template v-for="request in props.BorrowRequests" :key="request.id">
            <TableRow>
              <TableCell class="text-start">{{ request.game }}</TableCell>
              <TableCell class="text-start">{{ request.owner }}</TableCell>
              <TableCell class="text-start">{{ request.startDate }}</TableCell>
              <TableCell class="text-start">{{ request.endDate }}</TableCell>
              <TableCell class="text-start">
                <Badge v-if="request.status === 'Pending'" class="bg-gray-500">Pending</Badge>
                <Badge class="bg-green-800" v-else-if="request.status === 'Accepted'"
                  >Accepted</Badge
                >
                <Badge v-else variant="destructive">Rejected</Badge>
              </TableCell>
            </TableRow>
          </template>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
