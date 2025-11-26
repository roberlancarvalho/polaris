<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="primary">
        <ion-buttons slot="start">
          <ion-button @click="toggleMenu">
            <ion-icon
              slot="icon-only"
              :icon="isMenuOpen ? closeOutline : menuOutline"
            ></ion-icon>
          </ion-button>
        </ion-buttons>

        <ion-title>Minhas Tarefas</ion-title>

        <ion-buttons slot="end">
          <ion-button @click="logout">
            <ion-icon slot="icon-only" :icon="logOutOutline"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="task-content">
      <div v-if="loading" class="loading-center">
        <ion-spinner name="crescent"></ion-spinner>
      </div>

      <div class="content-wrapper" v-else>
        <div class="page-actions">
          <h2>Gerenciar Tarefas</h2>

          <div class="action-buttons">
            <ion-button
              v-if="selectedTasks.length > 0 && userRole === 'ADMIN'"
              color="danger"
              @click="deleteSelected"
              class="btn-delete"
            >
              <ion-icon slot="start" :icon="trashBinOutline"></ion-icon>
              Excluir ({{ selectedTasks.length }})
            </ion-button>

            <ion-button
              class="btn-primary"
              v-if="userRole === 'ADMIN'"
              @click="goToCreate"
            >
              <ion-icon slot="start" :icon="add"></ion-icon>
              Nova Tarefa
            </ion-button>
          </div>
        </div>

        <TaskDataTable
          :tasks="tasks"
          :user-role="userRole"
          :all-users="allUsers"
          :current-user="currentUserEmail"
          @view="viewTask"
          @edit="editTask"
          @delete="deleteSingleTask"
          @selection-change="handleSelection"
          @status-change="handleChangeStatus"
        />
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import TaskDataTable from "@/components/Task/TaskDataTable.vue";
import api from "@/services/api";
import {
  alertController,
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonIcon,
  IonPage,
  IonSpinner,
  IonTitle,
  IonToolbar,
  menuController,
  onIonViewWillEnter,
  toastController,
} from "@ionic/vue";
import {
  add,
  closeOutline,
  logOutOutline,
  menuOutline,
  trashBinOutline,
} from "ionicons/icons";
import { onMounted, onUnmounted, ref } from "vue";
import { useRouter } from "vue-router";

interface User {
  name: string;
  email: string;
}

const router = useRouter();
const tasks = ref([]);
const loading = ref(true);
const isMenuOpen = ref(false);
const selectedTasks = ref<string[]>([]);
const userRole = ref("");
const allUsers = ref<User[]>([]);
const currentUsername = ref("");

const toggleMenu = async () => {
  await menuController.toggle("main-menu");
};

const onMenuOpen = () => {
  isMenuOpen.value = true;
};
const onMenuClose = () => {
  isMenuOpen.value = false;
};
const currentUserEmail = ref("");

const fetchData = async () => {
  loading.value = true;
  try {
    userRole.value = localStorage.getItem("polaris_role") || "USER";
    currentUsername.value = localStorage.getItem("polaris_username") || "";
    currentUserEmail.value = localStorage.getItem("polaris_email") || "";

    const [tasksRes, usersRes] = await Promise.all([
      api.get("/tasks"),
      api.get("/auth/users"),
    ]);

    // console.log("USUÁRIOS ENCONTRADOS:", usersRes.data);

    tasks.value = tasksRes.data;
    allUsers.value = usersRes.data;
    selectedTasks.value = [];
  } catch (error) {
    console.error(error);
    presentToast("Erro ao carregar dados", "danger");
  } finally {
    loading.value = false;
  }
};

const handleSelection = (ids: string[]) => {
  selectedTasks.value = ids;
};

const handleChangeStatus = async (payload: { id: string; status: string }) => {
  try {
    await api.patch(`/tasks/${payload.id}/status`, null, {
      params: { status: payload.status },
    });

    const statusMap: Record<string, string> = {
      PLANNED: "Planejado",
      IN_PROGRESS: "Em Andamento",
      IN_REVIEW: "Em Revisão",
      COMPLETED: "Concluído",
      BLOCKED: "Impedimento",
      CANCELLED: "Cancelado",
    };

    const statusTraduzido = statusMap[payload.status] || payload.status;

    presentToast(`Status atualizado para ${statusTraduzido}`);
    fetchData();
  } catch (error) {
    console.error(error);
    presentToast("Erro ao atualizar status", "danger");
  }
};

const viewTask = (task: any) => {
  router.push(`/tasks/details/${task.id}`);
};

const editTask = (task: any) => {
  if (userRole.value !== "ADMIN") {
    presentToast("Apenas administradores podem editar tarefas.", "warning");
    return;
  }
  router.push(`/tasks/edit/${task.id}`);
};

const deleteSingleTask = async (task: any) => {
  if (userRole.value !== "ADMIN") return;
  const alert = await alertController.create({
    header: "Confirmar Exclusão",
    message: `Deseja excluir "${task.title}"?`,
    buttons: [
      { text: "Cancelar", role: "cancel" },
      {
        text: "Excluir",
        role: "confirm",
        cssClass: "danger",
        handler: async () => {
          await api.delete(`/tasks/${task.id}`);
          presentToast("Tarefa excluída.");
          fetchData();
        },
      },
    ],
  });
  await alert.present();
};

const deleteSelected = async () => {
  if (userRole.value !== "ADMIN") return;
  const alert = await alertController.create({
    header: "Exclusão em Massa",
    message: `Tem certeza que deseja excluir ${selectedTasks.value.length} tarefas?`,
    buttons: [
      { text: "Cancelar", role: "cancel" },
      {
        text: "Sim, Excluir",
        role: "confirm",
        cssClass: "danger",
        handler: async () => {
          for (const id of selectedTasks.value) {
            await api.delete(`/tasks/${id}`);
          }
          presentToast(`${selectedTasks.value.length} itens excluídos.`);
          fetchData();
        },
      },
    ],
  });
  await alert.present();
};

const presentToast = async (msg: string, color: string = "dark") => {
  const toast = await toastController.create({
    message: msg,
    duration: 2000,
    color,
  });
  toast.present();
};

const logout = () => {
  localStorage.clear();
  router.replace("/home");
};

const goToCreate = () => {
  router.push("/tasks/create");
};

onIonViewWillEnter(() => {
  fetchData();
});

onMounted(() => {
  menuController.enable(true, "main-menu");
  document.addEventListener("ionMenuWillOpen", onMenuOpen);
  document.addEventListener("ionMenuWillClose", onMenuClose);
});

onUnmounted(() => {
  document.removeEventListener("ionMenuWillOpen", onMenuOpen);
  document.removeEventListener("ionMenuWillClose", onMenuClose);
});
</script>

<style lang="scss" scoped>
.task-content {
  --background: #f4f5f8;
}

.content-wrapper {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;

  h2 {
    margin: 0;
    font-weight: 800;
    color: #333;
    font-size: 1.5rem;
  }

  .action-buttons {
    display: flex;
    gap: 12px;

    ion-button {
      font-weight: 700;
      letter-spacing: 0.5px;
      --border-radius: 8px;
      --box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
      text-transform: uppercase;
      font-size: 0.85rem;
    }

    .btn-delete {
      --background: var(--ion-color-danger);
      --background-hover: var(--ion-color-danger-shade);
    }

    .btn-primary {
      color: #fff;
      --background: var(--ion-color-primary);
      --background-hover: var(--ion-color-primary-shade);
    }
  }
}

.loading-center {
  display: flex;
  justify-content: center;
  height: 80vh;
  align-items: center;
}
</style>
