<script setup>
import { ref } from 'vue';
import axios from 'axios';

// --- State ---
const user = ref(null);
const isLoginView = ref(true); // Toggle between Login and Register
const authForm = ref({ username: '', password: '' });

const balance = ref(0);
const recipientId = ref('');
const amount = ref(0);
const transactions = ref([]);

// --- Actions ---

const register = async () => {
  try {
    await axios.post('http://localhost:8080/api/auth/register', authForm.value);
    alert('Registration successful! Now please log in.');
    isLoginView.value = true;
  } catch (error) {
    alert('Registration failed: ' + (error.response?.data?.message || 'Check if username exists'));
  }
};

const login = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', authForm.value);
    user.value = res.data;
    await fetchWalletData();
  } catch (error) {
    alert('Login failed. Please check your credentials.');
  }
};

const fetchWalletData = async () => {
  if (!user.value) return;
  try {
    // In a real app, you'd have an endpoint to get balance by userId
    // For now, we'll fetch transaction history as a placeholder
    const historyRes = await axios.get(`http://localhost:8080/api/wallet/history/${user.value.id}`);
    transactions.value = historyRes.data;
    
    // Manual balance update if you don't have a balance endpoint yet
    // balance.value = ...
  } catch (error) {
    console.error("Error fetching wallet data", error);
  }
};

const handleTransfer = async () => {
  try {
    await axios.post('http://localhost:8080/api/wallet/transfer', {
      fromWalletId: user.value.id, // Using user ID as wallet ID for simplicity
      toWalletId: recipientId.value,
      amount: amount.value
    });
    alert('Transfer successful!');
    recipientId.value = '';
    amount.value = 0;
    await fetchWalletData();
  } catch (error) {
    alert('Transfer failed. Ensure recipient exists and balance is sufficient.');
  }
};

const logout = () => {
  user.value = null;
  authForm.value = { username: '', password: '' };
};
</script>

<template>
  <div class="min-h-screen bg-slate-50 p-4 md:p-8 font-sans">
    <div class="max-w-md mx-auto bg-white rounded-2xl shadow-xl overflow-hidden border border-slate-100">
      
      <!-- Header -->
      <div class="bg-teal-600 p-6 text-white text-center">
        <h1 class="text-2xl font-bold">Neuro-Wallet</h1>
        <p v-if="user" class="text-teal-100">Welcome, {{ user.username }} (ID: {{ user.id }})</p>
      </div>

      <div class="p-6">
        <!-- 1. AUTHENTICATED VIEW -->
        <div v-if="user" class="space-y-6">
          <div class="bg-teal-50 p-5 rounded-xl border border-teal-100 text-center">
            <p class="text-sm text-teal-600 font-semibold uppercase tracking-wider">Balance</p>
            <p class="text-4xl font-mono font-bold text-teal-900">RM {{ balance.toFixed(2) }}</p>
          </div>

          <div class="space-y-4">
            <h3 class="font-bold text-slate-700">Send Money</h3>
            <input v-model="recipientId" type="number" placeholder="Recipient User ID" 
                   class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-teal-500 outline-none" />
            <input v-model="amount" type="number" placeholder="Amount (RM)" 
                   class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg focus:ring-2 focus:ring-teal-500 outline-none" />
            <button @click="handleTransfer" 
                    class="w-full bg-teal-600 hover:bg-teal-700 text-white font-bold py-3 rounded-lg transition">
              Confirm Transfer
            </button>
          </div>

          <hr class="border-slate-100" />
          
          <button @click="logout" class="w-full text-slate-400 text-sm hover:text-red-500 transition">
            Sign Out
          </button>
        </div>

        <!-- 2. LOGIN / REGISTER VIEW -->
        <div v-else class="space-y-4">
          <h2 class="text-xl font-bold text-slate-800 text-center">
            {{ isLoginView ? 'Sign In' : 'Create Account' }}
          </h2>
          
          <div class="space-y-3">
            <input v-model="authForm.username" type="text" placeholder="Username" 
                   class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg outline-none focus:ring-2 focus:ring-teal-500" />
            <input v-model="authForm.password" type="password" placeholder="Password" 
                   class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg outline-none focus:ring-2 focus:ring-teal-500" />
            
            <button v-if="isLoginView" @click="login" 
                    class="w-full bg-teal-600 hover:bg-teal-700 text-white font-bold py-3 rounded-lg shadow-lg shadow-teal-100 transition">
              Login
            </button>
            <button v-else @click="register" 
                    class="w-full bg-teal-600 hover:bg-teal-700 text-white font-bold py-3 rounded-lg shadow-lg shadow-teal-100 transition">
              Register Now
            </button>

            <p class="text-center text-sm text-slate-500 pt-4">
              {{ isLoginView ? "Don't have an account?" : "Already have an account?" }}
              <button @click="isLoginView = !isLoginView" class="text-teal-600 font-bold ml-1 hover:underline">
                {{ isLoginView ? 'Sign Up' : 'Log In' }}
              </button>
            </p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>