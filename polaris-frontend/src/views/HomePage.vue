<template>
  <ion-page>
    <ion-content :fullscreen="true" class="auth-content">
      <div class="login-container">
        <LoginCard />

        <div class="footer-text">
          <p>
            © {{ new Date().getFullYear() }} Roberlan Systems. All rights
            reserved.
          </p>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import LoginCard from "@/components/Auth/LoginCard.vue";
import { IonContent, IonPage } from "@ionic/vue";
</script>

<style lang="scss" scoped>
.auth-content {
  --background: #eef2f5;
}

.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100%;
  padding: 20px;
}

.footer-text {
  margin-top: 20px;
  p {
    color: #8898aa;
    font-size: 0.8rem;
  }
}
</style>
