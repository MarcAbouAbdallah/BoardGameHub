<script setup lang="ts">
import { defineProps } from "vue";
import { Button } from "./ui/button";
import { ChevronDown, ChevronUp } from "lucide-vue-next";
import CustomTableHeader from "../components/TableHeader.vue";
import { Trash, Undo2 } from "lucide-vue-next";
import { Badge } from "./ui/badge";
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
import { useToast } from "./ui/toast";
import Alert from "./alert/Alert.vue";

const loading = ref(false);
const error = ref("");
const expandedRows = ref<Record<number, boolean>>({});
const { toast } = useToast();

const toggleRowExpansion = (rowId: number) => {
  expandedRows.value[rowId] = !expandedRows.value[rowId];
};

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

const props = defineProps({
  games: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <div class="p-6 space-y-6 w-9/12 mx-auto">
    <CustomTableHeader :title="'My Games'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead></TableHead>
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Type</TableHead>
            <TableHead class="font-bold text-lg text-black">Owner</TableHead>
            <TableHead class="font-bold text-lg text-black">minPlayers</TableHead>
            <TableHead class="font-bold text-lg text-black">maxPlayers</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          <template v-for="game in props.games" :key="game.id">
            <TableRow>
              <TableCell>
                <Button
                  variant="outline"
                  class="p-2 border-none bg-transparent"
                  @click="toggleRowExpansion(game.id)"
                >
                  <ChevronUp v-if="expandedRows[game.id]" class="h-4 w-4" />
                  <ChevronDown v-else class="h-4 w-4" />
                </Button>
              </TableCell>
              <TableCell class="text-start">{{ game.name }}</TableCell>
              <TableCell class="text-start">
                <Badge class="bg-blue-800" v-if="game.isBorrowed">Borrowed</Badge>
                <Badge class="bg-green-800 min-w-[75px] text-center" v-else>Owned</Badge>
              </TableCell>
              <TableCell class="text-start">{{ game.owner }} </TableCell>
              <TableCell class="text-start">{{ game.minPlayers }}</TableCell>
              <TableCell class="text-start">{{ game.maxPlayers }}</TableCell>
              <TableCell class="text-start">
                <Button variant="destructive" v-if="!game.isBorrowed">
                  <Trash class="h-4 w-4" />
                  Remove
                </Button>
                <Button variant="outline" class="ml-2" v-else>
                  <Undo2 class="h-4 w-4" />
                  Return
                </Button>
              </TableCell>
            </TableRow>
            <TableRow v-if="expandedRows[game.id]">
              <TableCell colspan="7" class="p-4 bg-gray-100">
                <div class="flex flex-col items-start space-y-2">
                  <p><strong>Game Description:</strong> {{ game.description }}</p>
                </div>
              </TableCell>
            </TableRow>
          </template>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>
