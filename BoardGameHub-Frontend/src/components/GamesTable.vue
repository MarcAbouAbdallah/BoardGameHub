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
import Alert from "./alert/Alert.vue";

const loading = ref(false);
const error = ref("");
const expandedRows = ref<Record<number, boolean>>({});

const toggleRowExpansion = (rowId: number) => {
  expandedRows.value[rowId] = !expandedRows.value[rowId];
};

const props = defineProps({
  games: {
    type: Object,
    required: true,
  },
});

const handleRemoveGame = async (gameId: number) => {
  try {
    loading.value = true;
    //TODO: Call the API to remove the game
  } catch (err) {
    error.value = "Error removing game";
  } finally {
    loading.value = false;
  }
};

const handleReturnGame = async (gameId: number) => {
  try {
    loading.value = true;
    //TODO: Call the API to return the game
  } catch (err) {
    error.value = "Error returning game";
  } finally {
    loading.value = false;
  }
};
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
            <TableHead class="font-bold text-lg text-black">Status</TableHead>
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
              <TableCell class="text-start">
                <Badge
                  class="bg-green-800 min-w-[75px] text-center"
                  v-if="game.isAvailable && !game.isBorrowed"
                >
                  Available</Badge
                >
                <Badge
                  class="bg-red-800 min-w-[75px] text-center"
                  v-else-if="!game.isAvailable && !game.isBorrowed"
                  >Unavailable</Badge
                >
              </TableCell>
              <TableCell class="text-start">{{ game.minPlayers }}</TableCell>
              <TableCell class="text-start">{{ game.maxPlayers }}</TableCell>
              <TableCell class="text-start">
                <Alert
                  :description="'Are you sure you want to remove this game?'"
                  actionText="Remove"
                  :actionFunc="() => handleRemoveGame(game.id)"
                  v-if="!game.isBorrowed"
                >
                  <Button variant="destructive">
                    <Trash class="h-4 w-4" />
                    Remove
                  </Button>
                </Alert>
                <Alert
                  :description="'Are you sure you want to return this game?'"
                  actionText="Return"
                  :actionFunc="() => handleReturnGame(game.id)"
                  v-else
                >
                  <Button variant="outline" class="min-w-[109px]">
                    <Undo2 class="h-4 w-4" />
                    Return
                  </Button>
                </Alert>
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
