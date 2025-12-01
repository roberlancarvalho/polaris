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
          <div class="avatar-container" @click="triggerAvatarUpload">
            <img v-if="userAvatar" :src="userAvatar" class="user-avatar-img" />
            <div v-else class="user-avatar-placeholder">
              {{ getUserInitials(userName) }}
            </div>
            <div class="camera-icon">
              <ion-icon :icon="cameraOutline"></ion-icon>
            </div>
          </div>

          <h3 class="user-name">{{ userName || "Visitante" }}</h3>
          <h4 class="role-badge">
            {{
              userRole === "ADMIN" ? $t("menu.manager") : $t("menu.developer")
            }}
          </h4>
          <input
            type="file"
            ref="avatarInput"
            style="display: none"
            accept="image/*"
            @change="handleAvatarChange"
          />
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
            <ion-item
              router-link="/profile"
              router-direction="forward"
              :detail="false"
              class="menu-item"
              :class="{ selected: route.path === '/profile' }"
            >
              <ion-icon slot="start" :icon="cameraOutline"></ion-icon>
              <ion-label>{{ $t("menu.profile") }}</ion-label>
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

import api from "@/services/api";
import { cameraOutline } from "ionicons/icons";

const userAvatar = ref("");
const avatarInput = ref<HTMLInputElement | null>(null);

const getUserInitials = (name: any) => {
  const str = typeof name === "string" ? name : name?.value ?? "";
  const cleaned = (str || "").trim();
  if (!cleaned) return "";
  const parts = cleaned.split(/\s+/);
  if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
  return (parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
};

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

  userAvatar.value = localStorage.getItem("polaris_avatar") || "";

  const storedName = localStorage.getItem("polaris_username");
  const savedLang = localStorage.getItem("polaris_lang");

  if (savedLang) locale.value = savedLang;

  if (storedName) {
    userName.value = storedName.charAt(0).toUpperCase() + storedName.slice(1);
  } else {
    userName.value = "";
  }

  if (!userAvatar.value && localStorage.getItem("polaris_token")) {
    fetchAvatarUrl();
  }
};

const triggerAvatarUpload = () => avatarInput.value?.click();

const fetchAvatarUrl = async () => {
  if (!localStorage.getItem("polaris_token")) return;

  try {
    const res = await api.get("/auth/me");

    if (res.data.avatarUrl) {
      userAvatar.value = res.data.avatarUrl;
      localStorage.setItem("polaris_avatar", res.data.avatarUrl);
    } else {
      userAvatar.value = "";
      localStorage.removeItem("polaris_avatar");
    }
  } catch (e) {
    console.error("Erro ao buscar avatar no menu:", e);
  }
};

const handleAvatarChange = async (event: any) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await api.post("/auth/me/avatar", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    userAvatar.value = res.data.avatarUrl;
    localStorage.setItem("polaris_avatar", userAvatar.value);

    window.dispatchEvent(new Event("storage"));
  } catch (e) {
    console.error(e);
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
  localStorage.removeItem("polaris_avatar");

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
  justify-content: center;
  align-items: center;
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
    align-self: center;
  }
}

.menu-list {
  background: white;
  padding: 0 !important;
}

.user-avatar-img {
  width: 120px;
  height: 120px;
  flex-direction: row;
  align-items: center;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(0, 0, 0, 0.1);
}

.user-avatar-placeholder {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: var(--ion-color-secondary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  font-weight: 700;
  border: 3px solid rgba(0, 0, 0, 0.1);
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
  // padding-bottom: 10%;
  cursor: default;

  .divider {
    color: #000000;
    font-weight: normal;
  }

  .lang-btn {
    cursor: pointer;
    transition: color 0.2s;
    font-size: 0.9rem;
    padding: 4px 8px;

    &:hover {
      color: #3d3c3c;
    }

    &.active {
      color: var(--ion-color-primary);
      text-decoration: underline;
      text-underline-offset: 5px;
      text-decoration-thickness: 5px;
    }
  }
}
</style>
