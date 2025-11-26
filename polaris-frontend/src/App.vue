<template>
  <ion-app>
    <ion-menu
      menu-id="main-menu"
      content-id="main-content"
      type="push"
      :disabled="isLoginPage"
    >
      <ion-header>
        <ion-toolbar color="primary">
          <ion-title>{{ $t("menu.title") }}</ion-title>
        </ion-toolbar>
      </ion-header>

      <ion-content class="menu-content">
        <div class="user-info ion-padding">
          <h3 class="user-name">
            {{ userName || $t("menu.visitor") }}
          </h3>
          <div class="role-badge">
            {{
              userRole === "ADMIN" ? $t("menu.manager") : $t("menu.developer")
            }}
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
              <ion-label>{{ $t("menu.tasks") }}</ion-label>
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
              <ion-label>{{ $t("menu.new_task") }}</ion-label>
            </ion-item>
          </ion-menu-toggle>

          <ion-menu-toggle :auto-hide="false">
            <ion-item button @click="logout" class="menu-item logout-item">
              <ion-icon slot="start" :icon="logOutOutline"></ion-icon>
              <ion-label>{{ $t("menu.logout") }}</ion-label>
            </ion-item>
          </ion-menu-toggle>
        </ion-list>
      </ion-content>

      <ion-footer class="ion-no-border menu-footer">
        <div class="lang-options">
          <span
            class="lang-btn"
            :class="{ active: locale === 'pt' }"
            @click="changeLang('pt')"
          >
            🇧🇷 PT
          </span>
          <span class="divider">|</span>
          <span
            class="lang-btn"
            :class="{ active: locale === 'en' }"
            @click="changeLang('en')"
          >
            🇺🇸 EN
          </span>
        </div>
      </ion-footer>
    </ion-menu>

    <ion-router-outlet id="main-content"></ion-router-outlet>
  </ion-app>
</template>

<script setup lang="ts">
import {
  IonApp,
  IonContent,
  IonFooter,
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
import { addCircleOutline, listOutline, logOutOutline } from "ionicons/icons";
import { computed, onMounted, ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";

const { t, locale } = useI18n();
const route = useRoute();
const router = useRouter();
const userRole = ref("");
const userName = ref("");

const isLoginPage = computed(
  () => route.path === "/home" || route.path === "/"
);

const loadUserData = () => {
  userRole.value = localStorage.getItem("polaris_role") || "";
  const storedName = localStorage.getItem("polaris_username");

  const savedLang = localStorage.getItem("polaris_lang");
  if (savedLang) locale.value = savedLang;

  if (storedName) {
    userName.value = storedName.charAt(0).toUpperCase() + storedName.slice(1);
  } else {
    userName.value = "";
  }
};

const changeLang = (lang: string) => {
  locale.value = lang;
  localStorage.setItem("polaris_lang", lang);
};

const logout = () => {
  localStorage.removeItem("polaris_token");
  localStorage.removeItem("polaris_role");
  localStorage.removeItem("polaris_username");
  localStorage.removeItem("polaris_email");
  localStorage.removeItem("polaris_lang");

  router.replace("/home");
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
  margin-bottom: 0;
  padding: 25px 20px;
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
  padding: 0 !important;
}

.menu-item {
  --background: transparent;
  --color: #444;
  --min-height: 55px;
  margin: 0 !important;
  border-radius: 0 !important;
  cursor: pointer;
  border-bottom: 1px solid #f9f9f9;
  ion-icon {
    color: #888;
    margin-right: 12px;
  }
  ion-label {
    font-weight: 500;
    font-size: 1rem;
  }
  &.selected {
    --background: var(--ion-color-primary) !important;
    --color: #ffffff !important;
    font-weight: 700;
    box-shadow: none;
    ion-icon {
      color: #ffffff !important;
      opacity: 1;
    }
  }
  &:hover:not(.selected) {
    --background: #f8f9fa;
  }
}

.menu-footer {
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  padding: 15px;
}

.lang-options {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  font-weight: 700;
  color: #999;
  cursor: default;

  .divider {
    color: #ddd;
    font-weight: normal;
  }

  .lang-btn {
    cursor: pointer;
    transition: color 0.2s;
    font-size: 0.9rem;

    &:hover {
      color: #666;
    }

    &.active {
      color: var(--ion-color-primary);
      text-decoration: underline;
      text-underline-offset: 4px;
    }
  }
}
</style>
