<template>
  <ion-page>
    <ion-header class="ion-no-border">
      <ion-toolbar color="primary">
        <ion-buttons slot="start">
          <ion-menu-button></ion-menu-button>
        </ion-buttons>
        <ion-title>{{ t("menu.my_profile") }}</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="content-bg">
      <div class="profile-header">
        <div class="avatar-wrapper" @click="triggerUpload">
          <img
            :src="user.avatarUrl || getPlaceholderUrl()"
            class="avatar-img"
            @error="handleImageError"
          />
          <div class="camera-btn">
            <ion-icon :icon="camera"></ion-icon>
          </div>
          <input
            type="file"
            ref="fileInput"
            accept="image/*"
            style="display: none"
            @change="handleFileChange"
          />
        </div>

        <h2 class="username">{{ user.name }}</h2>
        <p class="email">{{ user.email }}</p>
        <ion-chip color="light" class="role-chip">{{
          userRole === "ADMIN" ? t("menu.manager") : t("menu.developer")
        }}</ion-chip>
      </div>

      <div class="stats-container">
        <ion-card class="stat-card">
          <div class="stat-number" style="color: #999">
            {{ myTasks.length }}
          </div>
          <div class="stat-label">Total</div>
        </ion-card>
        <ion-card class="stat-card success">
          <div class="stat-number">{{ completedTasks }}</div>
          <div class="stat-label">{{ t("status.COMPLETED") }}</div>
        </ion-card>
        <ion-card class="stat-card warning">
          <div class="stat-number">{{ pendingTasks }}</div>
          <div class="stat-label">{{ t("status.IN_PROGRESS") }}</div>
        </ion-card>
      </div>

      <div class="tasks-section ion-padding">
        <h3>{{ t("tasks.page_title") }}</h3>

        <div v-if="loading" class="loading-center">
          <ion-spinner></ion-spinner>
        </div>

        <div v-else-if="myTasks.length === 0" class="empty-state">
          <p>{{ t("tasks.empty") }}</p>
        </div>

        <ion-list v-else class="profile-task-list">
          <ion-item
            v-for="task in myTasks"
            :key="task.id"
            lines="none"
            class="task-item"
            @click="goToDetails(task.id)"
          >
            <ion-label>
              <h2>{{ task.title }}</h2>
              <p>{{ translateStatus(task.status) }}</p>
            </ion-label>
            <ion-badge :class="getStatusClass(task.status)" slot="end">
              {{ translateStatus(task.status) }}
            </ion-badge>
          </ion-item>
        </ion-list>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import api from "@/services/api";
import {
  IonBadge,
  IonButtons,
  IonCard,
  IonChip,
  IonContent,
  IonHeader,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonMenuButton,
  IonPage,
  IonSpinner,
  IonTitle,
  IonToolbar,
  toastController,
} from "@ionic/vue";
import { camera } from "ionicons/icons";
import { computed, onMounted, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

const { t } = useI18n();
const router = useRouter();
const fileInput = ref<HTMLElement | null>(null);
const loading = ref(true);
const user = ref({ name: "", email: "", avatarUrl: "" });
const tasks = ref<any[]>([]);
const userRole = ref("");

const myTasks = computed(() =>
  tasks.value.filter((t) => t.assignedTo === user.value.email)
);
const completedTasks = computed(
  () => myTasks.value.filter((t) => t.status === "COMPLETED").length
);
const pendingTasks = computed(
  () => myTasks.value.length - completedTasks.value
);

const loadProfile = async () => {
  loading.value = true;
  try {
    userRole.value = localStorage.getItem("polaris_role") || "";

    const [userRes, tasksRes] = await Promise.all([
      api.get("/auth/me"),
      api.get("/tasks"),
    ]);

    user.value = userRes.data;
    tasks.value = tasksRes.data;

    if (userRes.data.avatarUrl) {
      localStorage.setItem("polaris_avatar", userRes.data.avatarUrl);
    } else {
      localStorage.removeItem("polaris_avatar");
    }
    window.dispatchEvent(new Event("storage"));
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const triggerUpload = () => fileInput.value?.click();

const handleFileChange = async (event: any) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await api.post("/auth/me/avatar", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    user.value.avatarUrl = res.data.avatarUrl;

    localStorage.setItem("polaris_avatar", res.data.avatarUrl);

    window.dispatchEvent(new Event("storage"));

    const toast = await toastController.create({
      message: "Foto atualizada!",
      duration: 2000,
      color: "success",
    });
    toast.present();
  } catch (error) {
    const toast = await toastController.create({
      message: "Erro no upload.",
      duration: 2000,
      color: "danger",
    });
    toast.present();
  }
};

const getPlaceholderUrl = () => {
  const name = user.value.name || "User";
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(
    name
  )}&background=3880ff&color=fff&size=128`;
};

const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement;
  const fallback = getPlaceholderUrl();

  if (target.src === fallback) {
    return;
  }

  target.src = fallback;
};

const goToDetails = (id: string) => router.push(`/tasks/details/${id}`);
const translateStatus = (s: string) => t(`status.${s}`);

const getStatusClass = (s: string) =>
  s ? s.toLowerCase().replace("_", "-") : "";

onMounted(() => loadProfile());
</script>

<style lang="scss" scoped>
.content-bg {
  --background: #f4f5f8;
}

.profile-header {
  background: var(--ion-color-primary);
  padding: 40px 20px 60px;
  text-align: center;
  color: white;
  border-bottom-left-radius: 30px;
  border-bottom-right-radius: 30px;
  margin-bottom: -40px;

  .username {
    margin: 10px 0 5px;
    font-weight: 800;
    font-size: 1.5rem;
  }
  .email {
    margin: 0 0 10px;
    opacity: 0.9;
    font-size: 0.9rem;
  }
  .role-chip {
    --background: rgba(255, 255, 255, 0.2);
    --color: white;
    font-weight: 600;
  }
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto;

  .avatar-img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
    border: 4px solid rgba(255, 255, 255, 0.3);
    background: white;
  }

  .camera-btn {
    position: absolute;
    bottom: 0;
    right: 0;
    background: var(--ion-color-tertiary);
    color: white;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid white;
    cursor: pointer;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  }
}

.stats-container {
  display: flex;
  gap: 10px;
  padding: 0 20px;

  .stat-card {
    flex: 1;
    margin: 0;
    text-align: center;
    padding: 15px 10px;
    border-radius: 16px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);

    .stat-number {
      font-size: 1.8rem;
      font-weight: 800;
      color: #333;
    }
    .stat-label {
      font-size: 0.75rem;
      color: #888;
      text-transform: uppercase;
      font-weight: 700;
      margin-top: 5px;
    }

    &.success .stat-number {
      color: var(--ion-color-success);
    }
    &.warning .stat-number {
      color: var(--ion-color-warning);
    }
  }
}

.tasks-section {
  margin-top: 20px;
  h3 {
    font-size: 1.1rem;
    font-weight: 700;
    color: #444;
    margin-bottom: 15px;
    padding-left: 5px;
  }

  .empty-state {
    text-align: center;
    color: #888;
    font-size: 1rem;
    margin-top: 30px;
  }

  .list-md {
    --ion-item-background: transparent !important;
  }

  .task-item {
    --background: white;
    --border-radius: 12px;
    margin-bottom: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
    cursor: pointer;

    ion-label {
      p {
        font-size: 0.8rem;
      }
    }

    ion-badge {
      padding: 6px 10px;
      border-radius: 20px;
      font-weight: 700;
      font-size: 0.75rem;
      text-transform: uppercase;

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
      }
    }

    h2 {
      font-weight: 600;
      font-size: 1rem;
      color: #333;
    }
  }
}
</style>
