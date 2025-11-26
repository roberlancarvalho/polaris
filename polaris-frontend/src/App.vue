<template>
  <ion-app>
    <ion-menu
      menu-id="main-menu"
      content-id="main-content"
      type="overlay"
      :disabled="isLoginPage"
    >
      <ion-header>
        <ion-toolbar color="primary">
          <ion-title>Menu Polaris</ion-title>
        </ion-toolbar>
      </ion-header>

      <ion-content class="menu-content">
        <div class="user-info ion-padding">
          <h3 class="user-name">
            {{ userName || "Visitante" }}
          </h3>

          <div class="role-badge">
            {{ userRole === "ADMIN" ? "Gerente" : "Desenvolvedor" }}
          </div>
        </div>

        <ion-list lines="none" class="menu-list">
          <ion-menu-toggle :auto-hide="false">
            <ion-item
              router-link="/tasks"
              router-direction="root"
              :detail="false"
              class="menu-item"
              :class="{ selected: route.path === '/tasks' }"
            >
              <ion-icon slot="start" :icon="listOutline"></ion-icon>
              <ion-label>Minhas Tarefas</ion-label>
            </ion-item>
          </ion-menu-toggle>

          <ion-menu-toggle :auto-hide="false" v-if="userRole === 'ADMIN'">
            <ion-item
              router-link="/tasks/create"
              router-direction="forward"
              :detail="false"
              class="menu-item"
              :class="{ selected: route.path === '/tasks/create' }"
            >
              <ion-icon slot="start" :icon="addCircleOutline"></ion-icon>
              <ion-label>Nova Tarefa</ion-label>
            </ion-item>
          </ion-menu-toggle>
        </ion-list>
      </ion-content>
    </ion-menu>

    <ion-router-outlet id="main-content"></ion-router-outlet>
  </ion-app>
</template>

<script setup lang="ts">
import {
  IonApp,
  IonContent,
  IonHeader,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonMenu,
  IonMenuToggle,
  IonRouterOutlet,
  IonTitle,
  IonToolbar,
} from "@ionic/vue";
import { addCircleOutline, listOutline } from "ionicons/icons";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const userRole = ref("");
const userName = ref("");

const isLoginPage = computed(
  () => route.path === "/home" || route.path === "/"
);

const loadUserData = () => {
  userRole.value = localStorage.getItem("polaris_role") || "";
  const storedName = localStorage.getItem("polaris_username");

  if (storedName) {
    userName.value = storedName.charAt(0).toUpperCase() + storedName.slice(1);
  } else {
    userName.value = "";
  }
};

watch(
  () => route.path,
  () => {
    loadUserData();
  }
);

onMounted(() => {
  loadUserData();
  window.addEventListener("storage", loadUserData);
});
</script>

<style lang="scss">
.menu-content {
  --background: #ffffff;
}

.user-info {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 15px;
  padding-top: 25px;
  padding-bottom: 25px;
  display: flex;
  flex-direction: column;

  .user-name {
    margin: 0;
    font-size: 1.4rem;
    font-weight: 800;
    color: #2c3e50;
    letter-spacing: -0.5px;
  }

  .role-badge {
    display: inline-block;
    margin-top: 6px;
    font-size: 0.8rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 1px;
    color: var(--ion-color-primary);
    background: rgba(var(--ion-color-primary-rgb), 0.1);
    padding: 4px 8px;
    border-radius: 4px;
    align-self: flex-start;
  }
}

.menu-list {
  background: white;
}

.menu-item {
  --background: transparent;
  --color: #444;
  --min-height: 50px;
  margin: 0 0 8px 0 !important;
  border-radius: 0 !important;
  cursor: pointer;

  ion-icon {
    color: #888;
  }

  ion-label {
    font-weight: 500;
    font-size: 1rem;
  }

  &.selected {
    --background: var(--ion-color-primary) !important;
    --color: #ffffff !important;
    font-weight: 700;
    box-shadow: 0 4px 10px rgba(var(--ion-color-primary-rgb), 0.3);

    ion-icon {
      color: #ffffff !important;
      opacity: 1;
    }
  }
}
</style>
