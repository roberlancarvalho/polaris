<template>
  <ion-card class="login-card">
    <ion-card-header class="ion-text-center">
      <div class="logo-container">
        <img
          src="/assets/img/polaris-logo.png"
          alt="Polaris Logo"
          class="app-logo"
        />
      </div>

      <ion-card-title class="auth-title">{{
        $t("login.title")
      }}</ion-card-title>
      <ion-card-subtitle class="auth-subtitle">{{
        $t("login.subtitle")
      }}</ion-card-subtitle>
    </ion-card-header>

    <ion-card-content>
      <div class="input-group">
        <ion-item lines="full" class="custom-input">
          <ion-label position="stacked">{{
            $t("login.email_label")
          }}</ion-label>
          <ion-input
            v-model="email"
            type="email"
            placeholder="seu@email.com"
          ></ion-input>
        </ion-item>

        <ion-item lines="full" class="custom-input">
          <ion-label position="stacked">{{
            $t("login.password_label")
          }}</ion-label>
          <ion-input
            v-model="password"
            type="password"
            placeholder="******"
            @keyup.enter="login"
          ></ion-input>
        </ion-item>
      </div>

      <div class="ion-padding-top">
        <ion-button
          expand="block"
          size="large"
          class="login-btn"
          @click="login"
          :disabled="loading"
        >
          <span v-if="!loading">{{ $t("login.btn_enter") }}</span>
          <ion-spinner v-else name="crescent"></ion-spinner>
        </ion-button>
      </div>

      <p v-if="errorMessage" class="error-message ion-text-center">
        <ion-icon :icon="alertCircleOutline"></ion-icon>
        {{ errorMessage }}
      </p>
    </ion-card-content>
  </ion-card>
</template>

<script setup lang="ts">
import api from "@/services/api";
import {
  IonButton,
  IonCard,
  IonCardContent,
  IonCardHeader,
  IonCardSubtitle,
  IonCardTitle,
  IonIcon,
  IonInput,
  IonItem,
  IonLabel,
  IonSpinner,
} from "@ionic/vue";
import { alertCircleOutline } from "ionicons/icons";
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

const { t } = useI18n();
const router = useRouter();
const email = ref("");
const password = ref("");
const loading = ref(false);
const errorMessage = ref("");

const login = async () => {
  if (!email.value || !password.value) {
    errorMessage.value = t("login.error_fields");
    return;
  }

  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await api.post("/auth/login", {
      email: email.value,
      password: password.value,
    });

    const { token, role, name, email: userEmail } = response.data;

    localStorage.setItem("polaris_token", token);
    localStorage.setItem("polaris_role", role);
    localStorage.setItem("polaris_username", name);
    localStorage.setItem("polaris_email", userEmail);

    router.replace("/tasks");
  } catch (error: any) {
    console.error(error);
    if (error.response && error.response.status === 401) {
      errorMessage.value = t("login.error_cred");
    } else {
      errorMessage.value = t("login.error_conn");
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 16px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  background: #ffffff !important;
  margin: 0;
}

.logo-container {
  margin-bottom: 10px;
  margin-top: 10px;
  .app-logo {
    width: 90px;
    height: auto;
    display: block;
    margin: 0 auto;
  }
}

.auth-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #0054e9 !important;
  margin-bottom: 4px;
}

.auth-subtitle {
  font-size: 0.9rem;
  color: #666666 !important;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.custom-input {
  --background: #ffffff;
  --padding-start: 0;
  margin-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;

  ion-label {
    color: #555 !important;
    font-weight: 600 !important;
    font-size: 0.85rem;
  }

  ion-input {
    --color: #333 !important;
    --placeholder-color: #999 !important;
    font-size: 1rem;
  }
}

.login-btn {
  --border-radius: 8px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-top: 10px;
  color: #fff;
}

.error-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: var(--ion-color-danger);
  margin-top: 20px;
  font-size: 0.9em;
  font-weight: 500;
  background: rgba(var(--ion-color-danger-rgb), 0.1);
  padding: 10px;
  border-radius: 8px;
}
</style>
