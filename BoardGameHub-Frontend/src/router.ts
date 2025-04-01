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
    // meta: {
    //   requiresAuth: true,
    // },
  },
  {
    path: "/games",
    component: GamesPage,
    // meta: {
    //   requiresAuth: true,
    // },
  },
  {
    path: "/events",
    component: EventsPage,
    // meta: {
    //   requiresAuth: true,
    // },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

//To implement authentication
// router.beforeEach((to, from, next) => {
//   const isAuthenticated = !!localStorage.getItem(""); // Replace with your actual authentication check

//   if (to.matched.some((record) => record.meta.requiresAuth) && !isAuthenticated) {
//     next("/"); // Redirect to login if not authenticated
//   } else {
//     next(); // Proceed to the route
//   }
// });

export default router;
