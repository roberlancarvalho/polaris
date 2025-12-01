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

            <div
              class="status-wrapper"
              :class="{ clickable: canChangeStatus }"
              @click="openStatusPopover"
            >
              <span :class="['status-badge', getStatusClass(task.status)]">
                {{ $t("status." + task.status) }}
              </span>

              <ion-icon
                v-if="canChangeStatus"
                :icon="chevronDownOutline"
                class="edit-icon"
              ></ion-icon>
            </div>
          </div>

          <h1 class="task-title">{{ task.title }}</h1>

          <div class="assignee-section">
            <span class="label">{{ $t("tasks.field_assignee") }}:</span>

            <div class="assignee-pill" v-if="task.assignedTo">
              <img
                v-if="getAssignee(task.assignedTo)?.avatarUrl"
                :src="getAssignee(task.assignedTo)?.avatarUrl"
                class="avatar-circle-img"
                @error="handleAvatarError(task.assignedTo)"
              />
              <div v-else class="avatar-circle">
                {{ getInitials(getUserName(task.assignedTo)) }}
              </div>
              <span class="name">{{ getUserName(task.assignedTo) }}</span>
            </div>

            <span v-else class="unassigned">
              {{ $t("tasks.label_unassigned") }}
            </span>
          </div>

          <div class="divider"></div>

          <div class="description-box">
            <span class="label">{{ $t("tasks.field_desc") }}</span>
            <p>{{ task.description || "---" }}</p>
          </div>

          <div class="meta-info">
            <div class="meta-item">
              <ion-icon :icon="calendarOutline"></ion-icon>
              <span>
                {{ $t("tasks.label_created") }}:
                <strong>{{ formatDate(task.createdAt) }}</strong>
              </span>
            </div>
            <div class="meta-item">
              <ion-icon :icon="timeOutline"></ion-icon>
              <span>
                {{ $t("tasks.col_updated") }}:
                <strong>{{ formatDate(task.updatedAt) }}</strong>
              </span>
            </div>
          </div>
        </div>
      </div>

      <ion-popover
        :is-open="popoverOpen"
        :event="popoverEvent"
        @didDismiss="popoverOpen = false"
        class="white-popover"
        :show-backdrop="false"
      >
        <ion-content class="ion-no-padding">
          <ion-list lines="full">
            <ion-item
              button
              v-for="status in availableStatuses"
              :key="status"
              @click="confirmStatusChange(status)"
            >
              <ion-label>{{ $t("status." + status) }}</ion-label>
            </ion-item>
          </ion-list>
        </ion-content>
      </ion-popover>
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
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonPage,
  IonPopover,
  IonSpinner,
  IonTitle,
  IonToolbar,
  alertController,
  toastController,
} from "@ionic/vue";
import {
  calendarOutline,
  chevronDownOutline,
  copyOutline,
  createOutline,
  timeOutline,
  trashBinOutline,
} from "ionicons/icons";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

// Interfaces
interface User {
  name: string;
  email: string;
  avatarUrl?: string;
}

const { t } = useI18n();
const route = useRoute();
const router = useRouter();
const taskId = route.params.id as string;

// Estado
const loading = ref(true);
const task = ref<any>({});
const userRole = ref("");
const currentUserEmail = ref("");
const allUsers = ref<User[]>([]);

// Estado do Popover
const popoverOpen = ref(false);
const popoverEvent = ref<Event | null>(null);
const availableStatuses = ref<string[]>([]);

// --- Helpers de Usuário ---
const getAssignee = (email: string) => {
  return allUsers.value.find((u) => u.email === email);
};

const handleAvatarError = (email: string) => {
  const user = getAssignee(email);
  if (user) user.avatarUrl = "";
};

const getUserName = (email: string) => {
  const user = getAssignee(email);
  return user ? user.name : email;
};

const getInitials = (name: string) => {
  const n = name || "??";
  return n.substring(0, 2).toUpperCase();
};

// --- Helpers de UI ---
const getShortId = (id: string) => (id ? id.substring(0, 6).toUpperCase() : "");

const formatDate = (d?: string) => {
  if (!d) return "-";
  return (
    new Date(d).toLocaleDateString("pt-BR") +
    " " +
    new Date(d).toLocaleTimeString("pt-BR", {
      hour: "2-digit",
      minute: "2-digit",
    })
  );
};

const getStatusClass = (s?: string) =>
  s ? s.toLowerCase().replace("_", "-") : "";

const getAllowedStatuses = (s: string, r: string) => {
  const all = [
    "PLANNED",
    "IN_PROGRESS",
    "IN_REVIEW",
    "COMPLETED",
    "BLOCKED",
    "CANCELLED",
  ];

  if (r === "ADMIN") return all.filter((x) => x !== s);

  // Regras de User
  switch (s) {
    case "PLANNED":
      return ["IN_PROGRESS"];
    case "IN_PROGRESS":
      return ["COMPLETED", "BLOCKED", "IN_REVIEW"];
    case "BLOCKED":
      return ["IN_PROGRESS"];
    case "IN_REVIEW":
      return ["COMPLETED", "IN_PROGRESS"];
    default:
      return [];
  }
};

const isOwnerOrAdmin = computed(() => {
  if (userRole.value === "ADMIN") return true;
  return task.value.assignedTo === currentUserEmail.value;
});

const openStatusPopover = (e: Event) => {

  if (!isOwnerOrAdmin.value) return;

  const opts = getAllowedStatuses(task.value.status, userRole.value);

  if (!opts.length) return;

  availableStatuses.value = opts;
  popoverEvent.value = e;
  popoverOpen.value = true;
};

const canChangeStatus = computed(() => {
  if (!task.value.status) return false;
  if (!isOwnerOrAdmin.value) return false;
  const opts = getAllowedStatuses(task.value.status, userRole.value);
  return opts.length > 0;
});

const confirmStatusChange = async (newStatus: string) => {
  popoverOpen.value = false;

  try {
    await api.patch(`/tasks/${taskId}/status`, null, {
      params: { status: newStatus },
    });
    task.value.status = newStatus;

    const toast = await toastController.create({
      message: t("tasks.status_updated"),
      duration: 2000,
      color: "success",
    });
    toast.present();
  } catch (error) {
    console.error("Erro ao atualizar status:", error);
  }
};

const copyId = async () => {
  if (!task.value.id) return;
  await navigator.clipboard.writeText(task.value.id);
  const toast = await toastController.create({
    message: t("tasks.id_copied"),
    duration: 1000,
    color: "dark",
    position: "bottom",
  });
  toast.present();
};

const loadData = async () => {
  try {
    userRole.value = localStorage.getItem("polaris_role") || "USER";
    currentUserEmail.value = localStorage.getItem("polaris_email") || "";

    const [taskRes, usersRes] = await Promise.all([
      api.get(`/tasks/${taskId}`),
      api.get("/auth/users"),
    ]);

    task.value = taskRes.data;
    allUsers.value = usersRes.data;
  } catch (error) {
    console.error("Erro ao carregar detalhes:", error);
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
  margin-top: 10px;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.status-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 4px;

  &.clickable {
    cursor: pointer;
    transition: opacity 0.2s;

    &:hover {
      opacity: 0.8;
    }

    .edit-icon {
      font-size: 14px;
      color: #888;
      margin-left: 4px;
    }
  }
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  display: inline-block;

  &.planned {
    background: #8aadb1;
    color: white;
  }
  &.in-progress {
    background: #3498db;
    color: white;
  }
  &.in-review {
    background: #c76bfc;
    color: white;
  }
  &.completed {
    background: #2ecc71;
    color: white;
  }
  &.blocked {
    background: #ff8a1c;
    color: white;
  }
  &.cancelled {
    background: #313131;
    color: white;
    text-decoration: line-through;
  }
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
  .avatar-circle-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    border: 1px solid rgba(0, 0, 0, 0.1);
    background: white;
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

<style>
.white-popover {
  --color: #333 !important;
  --min-width: 150px;
}
.white-popover .popover-content {
  background: #ffffff !important;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}
</style>
