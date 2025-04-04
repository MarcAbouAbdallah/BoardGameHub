<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useAuthStore } from "@/stores/authStore";
import { gameCopyService } from "@/services/GameCopyService";
import gameService from "@/services/gameService";
import { borrowRequestService } from "@/services/borrowService";

import { ChevronDown, ChevronUp, Trash } from "lucide-vue-next";
import CustomTableHeader from "@/components/TableHeader.vue";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import DataTableCard from "@/components/DataTableCard.vue";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import Alert from "@/components/alert/Alert.vue";

import { useToast } from "@/components/ui/toast/use-toast";

interface MyGame {
  id: number;
  name: string;
  isBorrowed: boolean;
  isAvailable?: boolean; // Owned games only
  owner: string;
  minPlayers: number;
  maxPlayers: number;
  description: string;
}

const games = ref<MyGame[]>([]);
const loading = ref(true);
const error = ref("");
const authStore = useAuthStore();
const { toast } = useToast();

const expandedRows = ref<Record<number, boolean>>({});
  const toggleRowExpansion = (rowId: number) => {
  expandedRows.value[rowId] = !expandedRows.value[rowId];
};

const handleRemoveGame = async (gameCopyId: number) => {
  try {
    loading.value = true;
    const userId = authStore.user?.userId;
    if (!userId) throw new Error("User not authenticated");

    await gameCopyService.removeGameCopy(gameCopyId, userId);

    // Remove from local list
    games.value = games.value.filter((g) => g.id !== gameCopyId);

    toast({
      title: "Game Removed",
      description: "The game has been successfully removed from your collection.",
      variant: "default",
    });

  } catch (err: any) {
    toast({
      title: "Failed to Remove Game",
      description: err?.message || "Something went wrong while removing the game.",
      variant: "destructive",
    });
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  try {
    loading.value = true;
    const userId = authStore.user.userId;
    if (!userId) throw new Error("Not logged in");

    // Owned games
    const ownedGamesRaw = await gameCopyService.getPlayerCollection(userId);
    const ownedGames: MyGame[] = await Promise.all(
      ownedGamesRaw.map(async (copy: any) => {
        const game = await gameService.getGameById(copy.gameId);
        return {
          id: copy.gameCopyId,
          name: copy.gameName,
          isBorrowed: false,
          isAvailable: copy.isAvailable,
          owner: "You",
          minPlayers: game.minPlayers,
          maxPlayers: game.maxPlayers,
          description: game.description,
        };
      })
    );

    // Borrowed Games
    const borrowedRaw = await borrowRequestService.getAcceptedRequestsByRequesterId(userId);
    const today = new Date().toISOString().split("T")[0];

    const validBorrowed = borrowedRaw.filter((req: any) => req.endDate >= today);

    const borrowedGames: MyGame[] = await Promise.all(
      validBorrowed.map(async (req: any) => {
        const copy = await gameCopyService.getGameCopyById(req.gameCopyId);
        const game = await gameService.getGameById(copy.gameId);
        return {
          id: copy.gameCopyId,
          name: game.name,
          isBorrowed: true,
          owner: req.requesteeName,
          minPlayers: game.minPlayers,
          maxPlayers: game.maxPlayers,
          description: game.description,
        };
      })
    );

    // Combine both owned and borrowed games
    games.value = [...ownedGames, ...borrowedGames];
  } catch (err: any) {
    console.error("Error loading My Games:", err);
    error.value = err.message || "Failed to load your games.";
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <div class="p-6 space-y-6 w-9/12 mx-auto">
    <CustomTableHeader :title="'My Games'" />
    <DataTableCard :is-loading="loading" :error="error">
      <Table class="w-full mt-4">
        <TableHeader>
          <TableRow>
            <TableHead />
            <TableHead class="font-bold text-lg text-black">Game</TableHead>
            <TableHead class="font-bold text-lg text-black">Type</TableHead>
            <TableHead class="font-bold text-lg text-black">Owner</TableHead>
            <TableHead class="font-bold text-lg text-black">Status</TableHead>
            <TableHead class="font-bold text-lg text-black">Min Players</TableHead>
            <TableHead class="font-bold text-lg text-black">Max Players</TableHead>
            <TableHead class="font-bold text-lg text-black">Actions</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody v-if="games.length > 0">
          <template v-for="game in games" :key="game.id">
            <TableRow>
              <TableCell>
                <Button variant="outline" class="p-2 border-none bg-transparent" @click="toggleRowExpansion(game.id)">
                  <ChevronUp v-if="expandedRows[game.id]" class="h-4 w-4" />
                  <ChevronDown v-else class="h-4 w-4" />
                </Button>
              </TableCell>
              <TableCell class="text-start">{{ game.name }}</TableCell>
              <TableCell class="text-start">
                <Badge class="bg-blue-800" v-if="game.isBorrowed">Borrowed</Badge>
                <Badge class="bg-green-800 min-w-[75px] text-center" v-else>Owned</Badge>
              </TableCell>
              <TableCell class="text-start">{{ game.owner }}</TableCell>
              <TableCell class="text-start">
                <Badge
                  class="bg-green-800 min-w-[75px] text-center"
                  v-if="game.isAvailable && !game.isBorrowed"
                >Available</Badge>
                <Badge
                  class="bg-red-800 min-w-[75px] text-center"
                  v-else-if="!game.isAvailable && !game.isBorrowed"
                >Unavailable</Badge>
              </TableCell>
              <TableCell class="text-start">{{ game.minPlayers }}</TableCell>
              <TableCell class="text-start">{{ game.maxPlayers }}</TableCell>
              <TableCell class="text-start">
                <Alert
                  v-if="!game.isBorrowed"
                  :description="'Are you sure you want to remove this game?'"
                  actionText="Remove"
                  :actionFunc="() => handleRemoveGame(game.id)"
                >
                  <Button variant="destructive">
                    <Trash class="h-4 w-4" />
                    Remove
                  </Button>
                </Alert>
              </TableCell>
            </TableRow>
            <TableRow v-if="expandedRows[game.id]">
              <TableCell colspan="8" class="p-4 bg-gray-100">
                <div class="flex flex-col items-start space-y-2">
                  <p><strong>Game Description:</strong> {{ game.description }}</p>
                </div>
              </TableCell>
            </TableRow>
          </template>
        </TableBody>

        <TableBody v-else>
          <TableRow>
            <TableCell colspan="8" class="text-center"> No borrowed or owned games found. </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </DataTableCard>
  </div>
</template>