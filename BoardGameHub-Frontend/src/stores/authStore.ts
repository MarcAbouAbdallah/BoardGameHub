import { ref } from "vue";
import { defineStore } from "pinia";
import { playerService } from "@/services/PlayerService";

interface User {
  username: string | null;
  userEmail: string | null;
  userId: number | null;
  userPassword: string | null;
  isGameOwner: boolean;
  isAuthenticated: boolean;
}

export const useAuthStore = defineStore("auth", () => {
  const user = ref<User>({
    username: null,
    userEmail: null,
    userId: null,
    userPassword: null,
    isGameOwner: false,
    isAuthenticated: false,
  });

  const storedUser = localStorage.getItem("user");
  if (storedUser) {
    user.value = JSON.parse(storedUser);
  }

  function login(name: string, email: string, id: number, pwd: string, gameOwner: boolean) {
    if (!pwd) return;
    user.value.username = name;
    user.value.userEmail = email;
    user.value.userId = id;
    user.value.userPassword = pwd;
    user.value.isGameOwner = gameOwner;
    user.value.isAuthenticated = true;

    localStorage.setItem("user", JSON.stringify(user.value));
  }

  function logout() {
    user.value.username = null;
    user.value.userEmail = null;
    user.value.userId = null;
    user.value.userPassword = null;
    user.value.isGameOwner = false;
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

  function changeUserPassword(newUserPassword: string) {
    user.value.userPassword = newUserPassword;
    localStorage.setItem("user", JSON.stringify(user.value));
  }

  async function changeGameOwnerStatus(userId: number) {
    const updatedUser = await playerService.getPlayerById(userId, true);
    user.value.isGameOwner = updatedUser.isGameOwner;
    localStorage.setItem("user", JSON.stringify(user.value));
  }

  return { user, login, logout, changeUsername, changeUserEmail, changeUserPassword, changeGameOwnerStatus };
});
