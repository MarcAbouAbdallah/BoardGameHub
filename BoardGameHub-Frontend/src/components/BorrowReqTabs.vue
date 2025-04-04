<script setup lang="ts">
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import BorrowRequestTable from "./BorrowRequestTable.vue";
import ReceivedBorrowReqTable from "./ReceivedBorrowReqTable.vue";
import LendingHistoryTable from "./LendingHistoryTable.vue";
</script>

<template>
  <Tabs default-value="recevied_requests" class="p-6 space-y-6 w-9/12 mx-auto mt-10">
    <TabsList>
      <TabsTrigger
        class="text-white w-56 h-10 border-black border-2 bg-blue-900"
        value="recevied_requests"
      >
        Received Requests
      </TabsTrigger>
      <TabsTrigger
        class="text-white w-56 h-10 border-black border-2 bg-blue-900"
        value="sent_requests"
      >
        Sent Requests
      </TabsTrigger>
      <TabsTrigger
        class="text-white w-56 h-10 border-black border-2 bg-blue-900"
        value="lending_history"
      >
        Lending History
      </TabsTrigger>
    </TabsList>
    <TabsContent value="recevied_requests">
      <ReceivedBorrowReqTable />
    </TabsContent>
    <TabsContent value="sent_requests">
      <BorrowRequestTable />
    </TabsContent>
    <TabsContent value="lending_history">
      <LendingHistoryTable />
    </TabsContent>
  </Tabs>
</template>
