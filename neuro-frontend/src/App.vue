<script setup>
import { ref } from 'vue';
import axios from 'axios';

const user = ref(null);
const isLoginView = ref(true); // Toggles between Login and Register
const authForm = ref({ username: '', password: '' });
const balance = ref(0);
const recipientId = ref('');
const amount = ref(0);

// NEW: Registration function
const register = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/auth/register', authForm.value);
    alert('Registration Successful! Please log in.');
    isLoginView.value = true; // Switch back to login view after success
  } catch (error) {
    alert('Registration failed: ' + (error.response?.data || 'Server error'));
  }
};

const login = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', authForm.value);
    user.value = res.data;
    // Fetch wallet logic here...
  } catch (error) {
    alert('Invalid credentials');
  }
};
// ... other functions
</script>

<template>
  <div class="min-h-screen bg-slate-50 p-8 font-sans">
    <div class="max-w-md mx-auto bg-white rounded-2xl shadow-xl p-6 border border-slate-100">
      <h1 class="text-2xl font-bold text-teal-700 mb-6 text-center">Neuro-Wallet</h1>

      <div v-if="user">
        </div>

      <div v-else>
        <h2 class="text-lg font-semibold text-slate-700 mb-4 text-center">
          {{ isLoginView ? 'Login' : 'Create Account' }}
        </h2>
        
        <div class="space-y-4">
          <input v-model="authForm.username" type="text" placeholder="Username" 
                 class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg outline-none" />
          <input v-model="authForm.password" type="password" placeholder="Password" 
                 class="w-full p-3 bg-slate-50 border border-slate-200 rounded-lg outline-none" />
          
          <button v-if="isLoginView" @click="login" 
                  class="w-full bg-teal-600 text-white font-bold py-3 rounded-lg">
            Login
          </button>
          <button v-else @click="register" 
                  class="w-full bg-teal-600 text-white font-bold py-3 rounded-lg">
            Register
          </button>

          <p class="text-center text-sm text-slate-500 mt-4">
            {{ isLoginView ? "Don't have an account?" : "Already have an account?" }}
            <button @click="isLoginView = !isLoginView" class="text-teal-600 font-bold ml-1 hover:underline">
              {{ isLoginView ? 'Sign Up' : 'Log In' }}
            </button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>