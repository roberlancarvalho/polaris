<template>
  <ion-page>
    <ion-header>
      <ion-toolbar color="primary">
        <ion-buttons slot="start">
          <ion-back-button default-href="/tasks"></ion-back-button>
        </ion-buttons>
        <ion-title>Nova Tarefa</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content :fullscreen="true" class="ion-padding content-bg">
      <div class="form-container">
        <ion-list lines="full" class="input-list">
          <ion-item>
            <ion-label position="stacked">Título</ion-label>
            <ion-input
              v-model="form.title"
              placeholder="Ex: Reunião com cliente"
              type="text"
            ></ion-input>
          </ion-item>

          <ion-item>
            <ion-label position="stacked">Descrição</ion-label>
            <ion-textarea
              v-model="form.description"
              placeholder="Detalhes da tarefa..."
              :rows="4"
            ></ion-textarea>
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

        <div class="ion-padding-top">
          <ion-button
            expand="block"
            @click="submitTask"
            :disabled="loading || !form.title"
          >
            <span v-if="!loading">CRIAR TAREFA</span>
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

const router = useRouter();
const loading = ref(false);

interface User {
  name: string;
  email: string;
}
const users = ref<User[]>([]);

const form = reactive({
  title: "",
  description: "",
  assignedTo: "",
});

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
    message: message,
    duration: 2000,
    color: color,
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

    await presentToast("Tarefa criada com sucesso!");
    router.replace("/tasks");
  } catch (error) {
    console.error(error);
    await presentToast("Erro ao criar tarefa.", "danger");
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
