import { createRouter, createWebHistory } from "vue-router";
import LoginPage from "../pages/authentication/LoginPage.vue";
import SignupPage from "../pages/authentication/SignupPage.vue";

const routes = [
  {
    path: "/",
    component: LoginPage,
  },
  {
    path: "/signup",
    component: SignupPage,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
