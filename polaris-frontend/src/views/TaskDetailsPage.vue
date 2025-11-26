<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="primary">
        <ion-buttons slot="start">
          <ion-back-button default-href="/tasks"></ion-back-button>
        </ion-buttons>
        <ion-title>{{ $t("tasks.details_page_title") }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="content-bg">
      <div v-if="loading" class="loading-center">
        <ion-spinner name="crescent"></ion-spinner>
      </div>

      <div v-else class="content-wrapper ion-padding">
        <div class="page-actions" v-if="userRole === 'ADMIN'">
          <div class="spacer"></div>
          <div class="action-buttons">
            <ion-button color="danger" @click="confirmDelete">
              <ion-icon slot="start" :icon="trashBinOutline"></ion-icon>
              {{ $t("tasks.btn_delete") }}
            </ion-button>

            <ion-button class="btn-primary" @click="goToEdit">
              <ion-icon slot="start" :icon="createOutline"></ion-icon>
              {{ $t("tasks.btn_edit") }}
            </ion-button>
          </div>
        </div>

        <div class="details-card">
          <div class="details-header">
            <div class="ticket-id" @click="copyId">
              <span class="hash">#</span>{{ getShortId(task.id) }}
              <ion-icon :icon="copyOutline"></ion-icon>
            </div>

            <div :class="['status-badge', getStatusClass(task.status)]">
              {{ $t("status." + task.status) }}
            </div>
          </div>

          <h1 class="task-title">{{ task.title }}</h1>

          <div class="assignee-section">
            <span class="label">{{ $t("tasks.field_assignee") }}:</span>
            <div class="assignee-pill" v-if="task.assignedTo">
              <div class="avatar-circle">
                {{ getInitials(getUserName(task.assignedTo)) }}
              </div>
              <span class="name">{{ getUserName(task.assignedTo) }}</span>
            </div>
            <span v-else class="unassigned">{{
              $t("tasks.label_unassigned")
            }}</span>
          </div>

          <div class="divider"></div>

          <div class="description-box">
            <span class="label">{{ $t("tasks.field_desc") }}</span>
            <p>{{ task.description || "---" }}</p>
          </div>

          <div class="meta-info">
            <div class="meta-item">
              <ion-icon :icon="calendarOutline"></ion-icon>
              <span
                >{{ $t("tasks.label_created") }}:
                <strong>{{ formatDate(task.createdAt) }}</strong></span
              >
            </div>
            <div class="meta-item">
              <ion-icon :icon="timeOutline"></ion-icon>
              <span
                >{{ $t("tasks.col_updated") }}:
                <strong>{{ formatDate(task.updatedAt) }}</strong></span
              >
            </div>
          </div>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import api from "@/services/api";
import {
  alertController,
  IonBackButton,
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonIcon,
  IonPage,
  IonSpinner,
  IonTitle,
  IonToolbar,
  toastController,
} from "@ionic/vue";
import {
  calendarOutline,
  copyOutline,
  createOutline,
  timeOutline,
  trashBinOutline,
} from "ionicons/icons";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";

interface User {
  name: string;
  email: string;
}

const { t } = useI18n();

const route = useRoute();
const router = useRouter();
const taskId = route.params.id as string;

const loading = ref(true);
const task = ref<any>({});
const userRole = ref("");
const allUsers = ref<User[]>([]);

const loadData = async () => {
  try {
    userRole.value = localStorage.getItem("polaris_role") || "USER";
    const [taskRes, usersRes] = await Promise.all([
      api.get(`/tasks/${taskId}`),
      api.get("/auth/users"),
    ]);
    task.value = taskRes.data;
    allUsers.value = usersRes.data;
  } catch (error) {
    // console.error("Erro ao carregar", error);
    router.replace("/tasks");
  } finally {
    loading.value = false;
  }
};

const goToEdit = () => {
  router.push(`/tasks/edit/${taskId}`);
};

const confirmDelete = async () => {
  const alert = await alertController.create({
    header: t("tasks.confirm_delete_title"),
    message: t("tasks.confirm_delete_msg"),
    buttons: [
      { text: t("tasks.btn_cancel"), role: "cancel" },
      {
        text: t("tasks.btn_delete"),
        role: "confirm",
        cssClass: "danger",
        handler: async () => {
          try {
            await api.delete(`/tasks/${taskId}`);
            const toast = await toastController.create({
              message: t("tasks.msg_deleted"),
              duration: 2000,
            });
            toast.present();
            router.replace("/tasks");
          } catch (e) {
            console.error(e);
          }
        },
      },
    ],
  });
  await alert.present();
};

const getShortId = (id: string) => (id ? id.substring(0, 6).toUpperCase() : "");
const getInitials = (name: string) =>
  name ? name.substring(0, 2).toUpperCase() : "??";
const formatDate = (d?: string) =>
  d
    ? new Date(d).toLocaleDateString("pt-BR") +
      " " +
      new Date(d).toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      })
    : "-";
const translateStatus = (s: string) => {
  const m: any = {
    PLANNED: "Planejado",
    IN_PROGRESS: "Em Andamento",
    IN_REVIEW: "Em Revisão",
    COMPLETED: "Concluído",
    BLOCKED: "Impedimento",
    CANCELLED: "Cancelado",
  };
  return m[s] || s;
};
const getStatusClass = (s: string) =>
  s ? s.toLowerCase().replace("_", "-") : "";
const getUserName = (email: string) => {
  const user = allUsers.value.find((u) => u.email === email);
  return user ? user.name : email;
};
const copyId = async () => {
  await navigator.clipboard.writeText(task.value.id);
  const toast = await toastController.create({
      message: t('tasks.id_copied'),
    duration: 1000, color: "dark", position: "bottom",
  });
  await toast.present();
};

onMounted(() => {
  loadData();
});
</script>

<style lang="scss" scoped>
@use "@/theme/_mixins.scss" as *;

.content-bg {
  --background: #f4f5f8;
}
.content-wrapper {
  max-width: 900px;
  margin: 0 auto;
}

.page-actions {
  display: flex;
  margin-bottom: 16px;
  .spacer {
    flex: 1;
  }

  .action-buttons {
    display: flex;
    gap: 10px;

    ion-button {
      font-weight: 600;
      letter-spacing: 0.5px;
    }
    .btn-primary {
      color: #fff;
      --background: var(--ion-color-primary);
      --background-hover: var(--ion-color-primary-shade);
    }
  }
}

.details-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.ticket-id {
  font-family: "Consolas", monospace;
  background: #f1f3f5;
  padding: 4px 8px;
  border-radius: 6px;
  color: #555;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  .hash {
    color: #999;
    font-weight: normal;
  }
  ion-icon {
    font-size: 12px;
    opacity: 0.5;
  }
  &:hover ion-icon {
    opacity: 1;
  }
}

.task-title {
  margin: 0 0 20px 0;
  font-size: 2rem;
  font-weight: 800;
  color: #2c3e50;
  line-height: 1.2;
}

.assignee-section {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 25px;
  .label {
    font-size: 0.9rem;
    color: #888;
    font-weight: 600;
  }
}

.assignee-pill {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #eef2f5;
  padding: 6px 16px 6px 6px;
  border-radius: 50px;
  .avatar-circle {
    width: 32px;
    height: 32px;
    background: var(--ion-color-secondary);
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 12px;
    text-transform: uppercase;
  }
  .name {
    color: #333;
    font-weight: 600;
    font-size: 0.95rem;
  }
}

.divider {
  height: 1px;
  background: #eee;
  margin: 25px 0;
}

.description-box {
  margin-bottom: 30px;
  .label {
    display: block;
    font-size: 0.8rem;
    color: #999;
    font-weight: 700;
    text-transform: uppercase;
    margin-bottom: 8px;
    letter-spacing: 1px;
  }
  p {
    color: #444;
    line-height: 1.6;
    font-size: 1.1rem;
    white-space: pre-wrap;
  }
}

.meta-info {
  display: flex;
  gap: 25px;
  flex-wrap: wrap;
  color: #777;
  font-size: 0.9rem;
  .meta-item {
    display: flex;
    align-items: center;
    gap: 8px;
    ion-icon {
      color: #aaa;
      font-size: 1.2rem;
    }
  }
}

.loading-center {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
}
</style>
