<script setup>
import HelloWorld from './components/HelloWorld.vue'
</script>

<template>
  <div class="min-h-screen bg-slate-50 p-8 font-sans">
    <div class="max-w-md mx-auto bg-white rounded-2xl shadow-xl p-6 border border-slate-100">
      <h1 class="text-2xl font-bold text-teal-700 mb-6">Neuro-Wallet</h1>
      
      <div v-if="user">
        <div class="bg-teal-50 p-4 rounded-xl mb-6">
          <p class="text-sm text-teal-600 font-medium">Available Balance</p>
          <p class="text-3xl font-mono font-bold text-teal-900">RM {{ balance.toFixed(2) }}</p>
        </div>

        <div class="space-y-4">
          <input v-model="recipientId" type="number" placeholder="Recipient ID" 
                 class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-teal-500 outline-none" />
          <input v-model="amount" type="number" placeholder="Amount (RM)" 
                 class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-teal-500 outline-none" />
          <button @click="handleTransfer" 
                  class="w-full bg-teal-600 hover:bg-teal-700 text-white font-bold py-3 rounded-lg transition duration-200">
            Send Money
          </button>
        </div>
      </div>
      </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const user = ref(null);
const authForm = ref({ username: '', password: '' });
const walletBalance = ref(0);
const recipientId = ref('');
const amount = ref(0);

const login = async () => {
  const res = await axios.post('http://localhost:8080/api/auth/login', authForm.value);
  user.value = res.data;
  // Fetch wallet after login...
};

const handleTransfer = async () => {
  await axios.post('http://localhost:8080/api/wallet/transfer', {
    fromUserId: user.value.id,
    toUserId: recipientId.value,
    amount: amount.value
  });
  alert('Transfer Successful!');
};
</script>