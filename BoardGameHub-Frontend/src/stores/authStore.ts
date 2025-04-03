import { ref } from "vue";
import { defineStore } from "pinia";

interface User {
  username: string | null;
  userEmail: string | null;
  userId: number | null;
  isAuthenticated: boolean;
}

export const useAuthStore = defineStore("auth", () => {
  const user = ref<User>({
    username: null,
    userEmail: null,
    userId: null,
    isAuthenticated: false,
  });

  const storedUser = localStorage.getItem("user");
  if (storedUser) {
    user.value = JSON.parse(storedUser);
  }

  function login(name: string, email: string, id: number, pwd: string) {
    if (!pwd) return;
    user.value.username = name;
    user.value.userEmail = email;
    user.value.userId = id;
    user.value.isAuthenticated = true;

    localStorage.setItem("user", JSON.stringify(user.value));
  }

  function logout() {
    user.value.username = null;
    user.value.userEmail = null;
    user.value.userId = null;
    user.value.isAuthenticated = false;

    localStorage.removeItem("user");
  }

  function changeUsername(newUsername: string) {
    user.value.username = newUsername;
    localStorage.setItem("user", JSON.stringify(user.value));
  }

  function changeUserEmail(newUserEmail: string) {
    user.value.userEmail = newUserEmail;
    localStorage.setItem("user", JSON.stringify(user.value));
  }

  return { user, login, logout, changeUsername, changeUserEmail };
});
