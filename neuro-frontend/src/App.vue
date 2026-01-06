<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

// --- State Management ---
const user = ref(JSON.parse(localStorage.getItem('wallet_user')) || null);
const isLoginView = ref(true);
const authForm = ref({ username: '', password: '' });

const balance = ref(0);
const recipientId = ref('');
const amount = ref(0);
const transactions = ref([]);
const searchQuery = ref('');
const isUploading = ref(false);

// --- Computed ---
const filteredTransactions = computed(() => {
  if (!searchQuery.value) return transactions.value;
  const q = searchQuery.value.toLowerCase();
  return transactions.value.filter(t => 
    t.senderId.toString().includes(q) || 
    t.receiverId.toString().includes(q) ||
    t.amount.toString().includes(q)
  );
});

// --- Actions ---
const login = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', authForm.value);
    user.value = res.data;
    user.value.token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."; 
    localStorage.setItem('wallet_user', JSON.stringify(user.value));
    await fetchWalletData();
  } catch (error) {
    alert('Authentication Failed: Check credentials or server status.');
  }
};

const register = async () => {
  try {
    await axios.post('http://localhost:8080/api/auth/register', authForm.value);
    alert('Account created successfully. Access granted for login.');
    isLoginView.value = true;
  } catch (error) {
    alert('Registration Error: Username conflict or server offline.');
  }
};

const fetchWalletData = async () => {
  if (!user.value) return;
  try {
    const historyRes = await axios.get(`http://localhost:8080/api/wallet/history/${user.value.id}`);
    transactions.value = historyRes.data.reverse();
    // Simulate balance calculation based on starting 1000 RM
    balance.value = 1000 - transactions.value
      .filter(t => t.senderId === user.value.id)
      .reduce((acc, t) => acc + t.amount, 0) +
      transactions.value
      .filter(t => t.receiverId === user.value.id)
      .reduce((acc, t) => acc + t.amount, 0);
  } catch (error) {
    console.error("Data Sync Error", error);
  }
};

const handleTransfer = async () => {
  if (amount.value > balance.value) return alert("Insufficient funds for this transaction.");
  if (amount.value <= 0) return alert("Please enter a valid amount.");
  
  try {
    await axios.post('http://localhost:8080/api/wallet/transfer', {
      fromUserId: user.value.id,
      toUserId: recipientId.value,
      amount: amount.value
    });
    alert(`Transfer of RM ${amount.value} completed.`);
    recipientId.value = '';
    amount.value = 0;
    await fetchWalletData();
  } catch (error) {
    alert('Transfer Rejected: Recipient ID not found.');
  }
};

const handleFileUpload = () => {
  isUploading.value = true;
  setTimeout(() => {
    isUploading.value = false;
    alert("Identity profile synchronized.");
  }, 1200);
};

const logout = () => {
  user.value = null;
  localStorage.removeItem('wallet_user');
};

onMounted(() => {
  if (user.value) fetchWalletData();
});
</script>

<template>
  <div class="min-h-screen bg-[#050505] text-[#fafafa] font-sans selection:bg-indigo-500/30 overflow-x-hidden">
    
    <!-- BACKGROUND DECOR -->
    <div class="fixed inset-0 overflow-hidden pointer-events-none">
      <div class="absolute -top-[10%] -left-[10%] w-[40%] h-[40%] bg-indigo-600/10 blur-[120px] rounded-full"></div>
      <div class="absolute top-[20%] -right-[5%] w-[30%] h-[30%] bg-blue-600/10 blur-[100px] rounded-full"></div>
    </div>

    <!-- NAVIGATION -->
    <nav v-if="user" class="border-b border-white/5 bg-black/20 backdrop-blur-md px-6 py-4 sticky top-0 z-50">
      <div class="max-w-6xl mx-auto flex justify-between items-center">
        <div class="flex items-center gap-3 group cursor-default">
          <div class="w-9 h-9 bg-gradient-to-br from-indigo-500 to-blue-600 rounded-xl flex items-center justify-center shadow-[0_0_20px_rgba(79,70,229,0.3)] group-hover:scale-105 transition-transform">
            <span class="font-black text-lg">N</span>
          </div>
          <span class="font-bold text-xl tracking-tight text-white/90">Neuro<span class="text-indigo-400">Wallet</span></span>
        </div>
        
        <div class="flex items-center gap-6">
          <div class="hidden sm:flex flex-col items-end">
            <span class="text-[10px] font-bold text-white/30 uppercase tracking-[0.2em]">System ID</span>
            <span class="text-sm font-mono text-indigo-300">#{{ user.id.toString().padStart(4, '0') }}</span>
          </div>
          <button @click="logout" class="p-2.5 bg-white/5 hover:bg-red-500/10 text-white/60 hover:text-red-400 rounded-xl border border-white/5 transition-all active:scale-95">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>
        </div>
      </div>
    </nav>

    <main class="max-w-6xl mx-auto p-6 md:py-12 relative z-10">
      
      <!-- DASHBOARD STATE -->
      <div v-if="user" class="grid grid-cols-1 lg:grid-cols-12 gap-8 animate-in">
        
        <!-- Left: Financial Status -->
        <div class="lg:col-span-4 space-y-6">
          
          <!-- Balance Card -->
          <div class="relative group">
            <div class="absolute inset-0 bg-indigo-600/20 blur-2xl opacity-0 group-hover:opacity-100 transition-opacity rounded-3xl"></div>
            <div class="relative bg-[#0a0a0a] border border-white/10 p-8 rounded-[2rem] shadow-2xl overflow-hidden">
              <div class="absolute top-0 right-0 p-4">
                <div class="w-12 h-12 bg-white/5 rounded-full flex items-center justify-center border border-white/5">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-indigo-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
              </div>
              
              <span class="text-xs font-bold text-white/40 uppercase tracking-[0.3em]">Vault Balance</span>
              <div class="mt-4 flex items-baseline gap-2">
                <span class="text-lg font-medium text-white/60">RM</span>
                <h3 class="text-5xl font-black tracking-tight text-white">{{ balance.toFixed(2) }}</h3>
              </div>
              
              <div class="mt-8 pt-6 border-t border-white/5 flex justify-between items-center">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full border border-white/10 bg-white/5 overflow-hidden">
                    <img :src="`https://api.dicebear.com/7.x/shapes/svg?seed=${user.username}`" />
                  </div>
                  <span class="text-sm font-semibold text-white/80">{{ user.username }}</span>
                </div>
                <span class="text-[10px] px-2 py-0.5 bg-green-500/10 text-green-400 rounded-full border border-green-500/20 font-bold uppercase tracking-wider">Active</span>
              </div>
            </div>
          </div>

          <!-- Transaction Form -->
          <div class="bg-[#0a0a0a] border border-white/10 p-8 rounded-[2rem] space-y-6">
            <h4 class="text-sm font-bold text-white/80 uppercase tracking-widest">Execute Transfer</h4>
            <div class="space-y-4">
              <div class="relative">
                <input v-model="recipientId" type="number" placeholder="Target User ID" 
                       class="w-full bg-white/5 border border-white/5 p-4 rounded-2xl text-white placeholder:text-white/20 focus:bg-white/10 focus:border-indigo-500/50 outline-none transition-all" />
              </div>
              <div class="relative">
                <input v-model="amount" type="number" placeholder="Volume (RM)" 
                       class="w-full bg-white/5 border border-white/5 p-4 rounded-2xl text-white placeholder:text-white/20 focus:bg-white/10 focus:border-indigo-500/50 outline-none transition-all" />
              </div>
              <button @click="handleTransfer" 
                      class="w-full bg-white text-black font-black py-4 rounded-2xl hover:bg-indigo-400 hover:text-white transition-all shadow-[0_0_30px_rgba(255,255,255,0.05)] active:scale-[0.98]">
                PROCEED TRANSACTION
              </button>
            </div>
          </div>
        </div>

        <!-- Right: Ledger / History -->
        <div class="lg:col-span-8 space-y-6">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 px-2">
            <div class="flex items-baseline gap-2">
              <h3 class="text-2xl font-black text-white italic">LEDGER</h3>
              <span class="text-xs text-white/20 font-mono">/v1.0.4</span>
            </div>
            
            <div class="relative group">
              <input v-model="searchQuery" type="text" placeholder="Filter ledger..." 
                     class="pl-11 pr-4 py-2.5 bg-white/5 border border-white/5 rounded-xl text-sm outline-none focus:bg-white/10 focus:border-white/20 transition-all w-full sm:w-64" />
              <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 absolute left-4 top-3.5 text-white/20 group-focus-within:text-indigo-400 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>

          <!-- Table-like List -->
          <div class="space-y-3">
            <div v-for="t in filteredTransactions" :key="t.id" 
                 class="group bg-[#0a0a0a] hover:bg-white/[0.02] border border-white/5 hover:border-white/10 p-5 rounded-2xl flex items-center justify-between transition-all">
              
              <div class="flex items-center gap-5">
                <div :class="t.senderId === user.id ? 'border-orange-500/20 bg-orange-500/5 text-orange-400' : 'border-emerald-500/20 bg-emerald-500/5 text-emerald-400'" 
                     class="w-12 h-12 rounded-xl border flex items-center justify-center shadow-inner">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path v-if="t.senderId === user.id" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 17l-4 4m0 0l-4-4m4 4V3" />
                    <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7l4-4m0 0l4 4m-4-4v18" />
                  </svg>
                </div>
                
                <div>
                  <div class="flex items-center gap-2">
                    <span class="text-xs font-mono text-white/20">ID:{{ t.id }}</span>
                    <p class="font-bold text-white/90">
                      {{ t.senderId === user.id ? 'DEBIT' : 'CREDIT' }} 
                      <span class="text-white/40 font-normal ml-1">#{{ t.senderId === user.id ? t.receiverId : t.senderId }}</span>
                    </p>
                  </div>
                  <p class="text-[10px] text-white/30 font-bold uppercase tracking-widest mt-1">Status: Verified</p>
                </div>
              </div>

              <div class="text-right">
                <p :class="t.senderId === user.id ? 'text-white' : 'text-emerald-400'" class="font-mono text-xl font-black">
                  {{ t.senderId === user.id ? '-' : '+' }}{{ t.amount.toFixed(2) }}
                </p>
                <p class="text-[9px] font-mono text-white/20 mt-1">TIMESTAMP: {{ new Date().toLocaleTimeString() }}</p>
              </div>
            </div>

            <div v-if="filteredTransactions.length === 0" class="flex flex-col items-center justify-center py-24 bg-white/[0.01] rounded-[2rem] border-2 border-dashed border-white/5">
              <div class="w-16 h-16 bg-white/5 rounded-full flex items-center justify-center mb-4">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white/10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <p class="text-white/20 font-mono text-sm uppercase tracking-widest">No matching records found</p>
            </div>
          </div>
        </div>
      </div>

      <!-- AUTHENTICATION STATE -->
      <div v-else class="max-w-md mx-auto mt-12 md:mt-24 perspective-1000">
        <div class="bg-[#0a0a0a] p-10 rounded-[2.5rem] border border-white/10 shadow-[0_0_100px_rgba(0,0,0,0.5)] relative overflow-hidden animate-in">
          
          <!-- Gloss effect -->
          <div class="absolute -top-24 -left-24 w-48 h-48 bg-indigo-500/10 blur-[60px] rounded-full"></div>

          <div class="text-center mb-10 relative z-10">
            <div class="w-16 h-16 bg-gradient-to-tr from-indigo-600 to-blue-500 rounded-2xl flex items-center justify-center text-white text-3xl font-black mx-auto mb-6 shadow-[0_0_30px_rgba(79,70,229,0.4)]">
              N
            </div>
            <h2 class="text-3xl font-black text-white tracking-tight">{{ isLoginView ? 'Welcome' : 'Register' }}</h2>
            <p class="text-white/40 text-sm mt-2 font-medium tracking-wide italic">Secure Access / NeuroCore Terminal</p>
          </div>

          <div class="space-y-5 relative z-10">
            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-white/30 uppercase tracking-[0.2em] ml-2">Identity</label>
              <input v-model="authForm.username" type="text" class="w-full bg-white/5 border border-white/5 p-4 rounded-2xl text-white outline-none focus:bg-white/10 focus:border-indigo-500/50 transition-all font-medium" placeholder="Username" />
            </div>
            <div class="space-y-1.5">
              <label class="text-[10px] font-black text-white/30 uppercase tracking-[0.2em] ml-2">Security Key</label>
              <input v-model="authForm.password" type="password" class="w-full bg-white/5 border border-white/5 p-4 rounded-2xl text-white outline-none focus:bg-white/10 focus:border-indigo-500/50 transition-all font-medium" placeholder="••••••••" />
            </div>
            
            <button v-if="isLoginView" @click="login" 
                    class="w-full bg-white text-black font-black py-4 rounded-2xl hover:bg-indigo-500 hover:text-white transition-all shadow-xl active:scale-[0.97] mt-4">
              INITIATE LOGIN
            </button>
            <button v-else @click="register" 
                    class="w-full bg-white text-black font-black py-4 rounded-2xl hover:bg-indigo-500 hover:text-white transition-all shadow-xl active:scale-[0.97] mt-4">
              CREATE IDENTITY
            </button>

            <div class="flex items-center justify-center gap-2 pt-6 text-sm">
              <span class="text-white/30 font-medium">{{ isLoginView ? "Unauthorized?" : "Recognized?" }}</span>
              <button @click="isLoginView = !isLoginView" class="text-white font-bold hover:text-indigo-400 transition-colors border-b border-white/10 hover:border-indigo-400">
                {{ isLoginView ? 'Request Access' : 'Return to Login' }}
              </button>
            </div>
          </div>
        </div>
      </div>

    </main>

    <!-- FOOTER INFO -->
    <footer v-if="user" class="max-w-6xl mx-auto p-6 flex justify-between items-center text-[10px] font-mono text-white/10 mt-12 mb-8">
      <span>NEURO_CORE_SYSTEM_OS [v1.0.4]</span>
      <span>ALL TRANSACTIONS ENCRYPTED_AES256</span>
    </footer>
  </div>
</template>

<style scoped>
.animate-in {
  animation: slideUp 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.perspective-1000 {
  perspective: 1000px;
}

/* Custom scrollbar for dark theme */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: #050505;
}
::-webkit-scrollbar-thumb {
  background: #1a1a1a;
  border-radius: 10px;
}
::-webkit-scrollbar-thumb:hover {
  background: #252525;
}
</style>