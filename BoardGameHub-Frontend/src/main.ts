import { createApp } from "vue";
import "./style.css";
import App from "./App.vue";
import router from "./router";
import PrimeVue from "primevue/config";
import ToastService from "primevue/toastservice";
import ConfirmationService from "primevue/confirmationservice";

const app = createApp(App);

app.use(router);
app.use(PrimeVue, { unstyled: true });
app.use(ToastService);
app.use(ConfirmationService);
app.mount("#app");
