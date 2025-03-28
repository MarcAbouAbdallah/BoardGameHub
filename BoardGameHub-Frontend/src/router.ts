import { createRouter, createWebHistory } from "vue-router";
import LoginPage from "./pages/authentication/LoginPage.vue";
import SignupPage from "./pages/authentication/SignupPage.vue";
import HomePage from "./pages/dashboard/HomePage.vue";
import GamesPage from "./pages/dashboard/GamesPage.vue";
import EventsPage from "./pages/dashboard/EventsPage.vue";

const routes = [
  {
    path: "/",
    component: LoginPage,
  },
  {
    path: "/signup",
    component: SignupPage,
  },
  {
    path: "/home",
    component: HomePage,
  },
  {
    path: "/games",
    component: GamesPage,
  },
  {
    path: "/events",
    component: EventsPage,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
