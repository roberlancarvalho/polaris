<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="primary">
        <ion-buttons slot="start">
          <ion-back-button default-href="/tasks"></ion-back-button>
        </ion-buttons>
        <ion-title>{{ $t("tasks.create_page_title") }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="ion-padding content-bg">
      <div class="form-container">
        <ion-list lines="full" class="input-list">
          <ion-item>
            <ion-label position="stacked">{{
              $t("tasks.field_title")
            }}</ion-label>
            <ion-input
              v-model="form.title"
              :placeholder="$t('tasks.field_title_placeholder')"
              type="text"
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">{{
              $t("tasks.field_desc")
            }}</ion-label>
            <ion-textarea
              v-model="form.description"
              :placeholder="$t('tasks.field_desc_placeholder')"
              :rows="4"
            ></ion-textarea>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">{{
              $t("tasks.field_assignee")
            }}</ion-label>
            <ion-select
              v-model="form.assignedTo"
              :placeholder="$t('tasks.field_assignee_placeholder')"
              interface="popover"
            >
              <ion-select-option
                v-for="user in users"
                :key="user.email"
                :value="user.email"
              >
                {{ user.name }}
              </ion-select-option>
            </ion-select>
          </ion-item>
        </ion-list>

        <div class="ion-padding-top">
          <ion-button
            expand="block"
            @click="submitTask"
            :disabled="loading || !form.title"
          >
            <span v-if="!loading">{{ $t("tasks.btn_create") }}</span>
            <ion-spinner v-else name="crescent"></ion-spinner>
          </ion-button>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import api from "@/services/api";
import {
  IonBackButton,
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonInput,
  IonItem,
  IonLabel,
  IonList,
  IonPage,
  IonSelect,
  IonSelectOption,
  IonSpinner,
  IonTextarea,
  IonTitle,
  IonToolbar,
  toastController,
} from "@ionic/vue";
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const router = useRouter();
const loading = ref(false);

interface User {
  name: string;
  email: string;
}
const users = ref<User[]>([]);

const form = reactive({ title: "", description: "", assignedTo: "" });

const loadUsers = async () => {
  try {
    const res = await api.get("/auth/users");
    users.value = res.data;
  } catch (error) {
    console.error("Erro ao carregar usuários", error);
  }
};

const presentToast = async (message: string, color: string = "success") => {
  const toast = await toastController.create({
    message,
    duration: 2000,
    color,
    position: "bottom",
  });
  await toast.present();
};

const submitTask = async () => {
  if (!form.title) return;
  loading.value = true;

  try {
    await api.post("/tasks", {
      title: form.title,
      description: form.description,
      assignedTo: form.assignedTo,
    });

    await presentToast(t("tasks.msg_created"));
    router.replace("/tasks");
  } catch (error) {
    console.error(error);
    await presentToast(t("tasks.err_create"), "danger");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadUsers();
});
</script>

<style lang="scss" scoped>
.content-bg {
  --background: #f4f5f8;
}

.form-container {
  max-width: 600px;
  margin: 0 auto;
}

.input-list {
  border-radius: 12px;
  background: white;
  padding: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;

  ion-item {
    --background: transparent;
    --padding-start: 8px;
    margin-bottom: 8px;
    --color: #333 !important;
    --placeholder-color: #888 !important;
    --placeholder-opacity: 1;

    ion-input,
    ion-textarea,
    ion-select {
      color: #333 !important;
      --color: #333 !important;
      --placeholder-color: #888 !important;
    }
  }

  ion-label {
    font-weight: 600;
    color: #555 !important;
    margin-bottom: 8px;
  }
}

.loading-center {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80%;
}
</style>
