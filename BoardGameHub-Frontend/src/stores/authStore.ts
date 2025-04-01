import { ref } from 'vue'
import { defineStore } from 'pinia'

interface User {
    username: string | null;
    userEmail: string | null;
    userId: number | null;
    isAuthenticated: boolean;
  }

export const useAuthStore = defineStore('auth', () => {
    const user = ref<User>(
        {
            username: null,
            userEmail: null,
            userId: null,
            isAuthenticated: false,
        }
    )

    function login(name: string, email: string, id: number, pwd: string) {
        if (!pwd) return;
        user.value.username = name;
        user.value.userEmail = email;
        user.value.userId = id;
        user.value.isAuthenticated = true;
    }

    function logout() {
        user.value.username = null;
        user.value.userEmail = null;
        user.value.userId = null;
        user.value.isAuthenticated = false;
    }

    return { user, login, logout }
})