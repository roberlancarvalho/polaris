<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="primary">
        <ion-buttons slot="start">
          <ion-back-button default-href="/tasks"></ion-back-button>
        </ion-buttons>
        <ion-title>Editar Tarefa</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="content-bg ion-padding">
      <div v-if="loadingData" class="loading-center">
        <ion-spinner name="crescent"></ion-spinner>
      </div>

      <div v-else class="form-container">
        <ion-list lines="full" class="input-list">
          <ion-item class="readonly-item">
            <ion-label position="stacked">Código</ion-label>
            <ion-input :value="formattedId" readonly></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Status</ion-label>
            <ion-select v-model="form.status" interface="popover">
              <ion-select-option
                v-for="(label, value) in statusOptions"
                :key="value"
                :value="value"
              >
                {{ label }}
              </ion-select-option>
            </ion-select>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Título</ion-label>
            <ion-input v-model="form.title" type="text"></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Descrição</ion-label>
            <ion-textarea v-model="form.description" :rows="6"></ion-textarea>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Responsável</ion-label>
            <ion-select
              v-model="form.assignedTo"
              placeholder="Selecione um usuário"
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
            <span v-if="!saving">SALVAR ALTERAÇÕES</span>
            <ion-spinner v-else name="crescent"></ion-spinner>
          </ion-button>

          <ion-button
            expand="block"
            fill="clear"
            color="medium"
            @click="router.back()"
          >
            Cancelar
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
import { useRoute, useRouter } from "vue-router";

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
  PLANNED: "Planejado",
  IN_PROGRESS: "Em Andamento",
  IN_REVIEW: "Em Revisão",
  COMPLETED: "Concluído",
  BLOCKED: "Impedimento",
  CANCELLED: "Cancelado",
};

const form = reactive({
  title: "",
  description: "",
  assignedTo: "",
  status: "",
});

const formattedId = computed(() => {
  return "#" + taskId.substring(0, 6).toUpperCase();
});

const loadUsers = async () => {
  try {
    const res = await api.get("/auth/users");
    users.value = res.data;
  } catch (error) {
    console.error("Erro ao carregar usuários", error);
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
    console.error("Erro ao carregar tarefa", error);
    presentToast("Erro ao carregar dados.", "danger");
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

    await presentToast("Tarefa atualizada com sucesso!");
    router.replace("/tasks");
  } catch (error) {
    console.error("Erro ao salvar", error);
    presentToast("Erro ao atualizar tarefa.", "danger");
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
