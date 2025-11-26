<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="primary">
        <ion-buttons slot="start">
          <ion-back-button default-href="/tasks"></ion-back-button>
        </ion-buttons>
        <ion-title>{{ $t("tasks.edit_page_title") }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="content-bg ion-padding">
      <div v-if="loadingData" class="loading-center">
        <ion-spinner name="crescent"></ion-spinner>
      </div>

      <div v-else class="form-container">
        <ion-list lines="full" class="input-list">
          <ion-item class="readonly-item">
            <ion-label position="stacked">{{
              $t("tasks.field_code")
            }}</ion-label>
            <ion-input :value="formattedId" readonly></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">{{
              $t("tasks.col_status")
            }}</ion-label>
            <ion-select v-model="form.status" interface="popover">
              <ion-select-option
                v-for="(label, value) in statusOptions"
                :key="value"
                :value="value"
              >
                {{ $t("status." + value) }}
              </ion-select-option>
            </ion-select>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">{{
              $t("tasks.field_title")
            }}</ion-label>
            <ion-input v-model="form.title" type="text"></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">{{
              $t("tasks.field_desc")
            }}</ion-label>
            <ion-textarea v-model="form.description" :rows="6"></ion-textarea>
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

        <div class="ion-padding-top btn-group">
          <ion-button
            expand="block"
            @click="updateTask"
            :disabled="saving || !form.title"
          >
            <span v-if="!saving">{{ $t("tasks.btn_save") }}</span>
            <ion-spinner v-else name="crescent"></ion-spinner>
          </ion-button>

          <ion-button
            expand="block"
            fill="clear"
            color="medium"
            @click="router.back()"
          >
            {{ $t("tasks.btn_cancel") }}
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
import { computed, onMounted, reactive, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

const { t } = useI18n();
const route = useRoute();
const router = useRouter();
const taskId = route.params.id as string;
const loadingData = ref(true);
const saving = ref(false);

interface User {
  name: string;
  email: string;
}
const users = ref<User[]>([]);

const statusOptions = {
  PLANNED: "Planned",
  IN_PROGRESS: "In Progress",
  IN_REVIEW: "In Review",
  COMPLETED: "Completed",
  BLOCKED: "Blocked",
  CANCELLED: "Cancelled",
};

const form = reactive({
  title: "",
  description: "",
  assignedTo: "",
  status: "",
});
const formattedId = computed(() => "#" + taskId.substring(0, 6).toUpperCase());

const loadUsers = async () => {
  try {
    const res = await api.get("/auth/users");
    users.value = res.data;
  } catch (error) {
    console.error(error);
  }
};

const loadTask = async () => {
  try {
    const response = await api.get(`/tasks/${taskId}`);
    form.title = response.data.title;
    form.description = response.data.description;
    form.assignedTo = response.data.assignedTo;
    form.status = response.data.status;
  } catch (error) {
    presentToast(t("tasks.err_generic"), "danger");
    router.replace("/tasks");
  } finally {
    loadingData.value = false;
  }
};

const updateTask = async () => {
  if (!form.title) return;
  saving.value = true;
  try {
    await api.put(`/tasks/${taskId}`, {
      title: form.title,
      description: form.description,
      assignedTo: form.assignedTo,
      status: form.status,
    });
    await presentToast(t("tasks.msg_updated"));
    router.replace("/tasks");
  } catch (error) {
    presentToast(t("tasks.err_update"), "danger");
  } finally {
    saving.value = false;
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

onMounted(async () => {
  await loadUsers();
  await loadTask();
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

.readonly-item {
  background-color: #f9f9f9;
  border-radius: 8px;

  ion-input {
    --color: #666 !important;
    color: #666 !important;
    font-family: "Consolas", "Monaco", monospace;
    font-weight: bold;
    font-size: 1.1em;
  }
}

.loading-center {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80%;
}
</style>
