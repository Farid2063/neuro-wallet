<script setup>
import HelloWorld from './components/HelloWorld.vue'
</script>

<template>
  <div id="app" style="padding: 20px; font-family: sans-serif;">
    <h1>Neuro-Wallet Dashboard</h1>
    
    <div style="background: #f4f4f4; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
      <h3>My Balance</h3>
      <p style="font-size: 24px; font-weight: bold;">RM {{ balance }}</p>
    </div>

    <div style="border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
      <h3>Transfer Money</h3>
      <input v-model="recipientId" placeholder="Recipient User ID" type="number" /><br><br>
      <input v-model="amount" placeholder="Amount (RM)" type="number" /><br><br>
      <button @click="handleTransfer" style="background: #007bff; color: white; padding: 10px 20px; border: none; cursor: pointer;">
        Send Money
      </button>
    </div>
    
    <p v-if="message" style="margin-top: 20px; color: green;">{{ message }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const balance = ref(1000.00); // Temporary starting balance
const recipientId = ref('');
const amount = ref(0);
const message = ref('');

const handleTransfer = async () => {
  try {
    // This calls the Backend API we built in Spring Boot
    const response = await axios.post('http://localhost:8080/api/wallet/transfer', {
      fromUserId: 1, // Static for demo
      toUserId: recipientId.value,
      amount: amount.value
    });
    message.value = "Success: " + response.data;
    balance.value -= amount.value;
  } catch (error) {
    message.value = "Error: " + error.response.data;
  }
};
</script>