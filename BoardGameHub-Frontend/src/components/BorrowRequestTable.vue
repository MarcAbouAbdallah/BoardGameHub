<script setup lang="ts">
import { defineProps, ref } from "vue";
import CustomTableHeader from "../components/TableHeader.vue";
import { ChevronDown, ChevronUp, ClipboardCheck, Pen, Trash } from "lucide-vue-next";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import DataTableCard from "./DataTableCard.vue";

const loading = ref(false);
const error = ref("");
const expandedRows = ref<Record<number, boolean>>({});

// const props = defineProps({
//   BorrowRequests: {
//     type: Object,
//     required: true,
//   },
// });
</script>

<template>
  <div class="p-6 space-y-6 w-9/12 mx-auto">
    <CustomTableHeader :title="'Borrow Requests'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Owner</TableHead>
            <TableHead class="font-bold text-lg text-black">Borrower</TableHead>
            <TableHead class="font-bold text-lg text-black">Status</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody></TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
