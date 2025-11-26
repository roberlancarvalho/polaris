<template>
  <ion-item lines="none" class="task-item">
    <ion-label>
      <div class="task-header">
        <h2>{{ task.title }}</h2>
        <span :class="['status-badge', getStatusClass(task.status)]">
          {{ formatStatus(task.status) }}
        </span>
      </div>
      <p>{{ task.description }}</p>
    </ion-label>
  </ion-item>
</template>

<script setup lang="ts">
import { IonItem, IonLabel } from "@ionic/vue";

const props = defineProps<{
  task: {
    id: string;
    title: string;
    description: string;
    status: string;
  };
}>();

const formatStatus = (status: string) => {
  const map: Record<string, string> = {
    PLANNED: "Planejado",
    IN_PROGRESS: "Em Andamento",
    IN_REVIEW: "Em Revisão",
    COMPLETED: "Concluído",
    BLOCKED: "Impedimento",
    CANCELLED: "Cancelado",
  };
  return map[status] || status;
};

const getStatusClass = (status: string) => {
  return status ? status.toLowerCase().replace("_", "-") : "";
};
</script>

<style lang="scss" scoped>
@use "sass:color";

@use "@/theme/_mixins.scss" as *;

.task-item {
  --background: #{$card-bg};
  --border-radius: #{$radius};
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  ion-label {
    margin: 12px 0;

    h2 {
      font-weight: 700;
      font-size: 1.1rem;
      color: $text-color;
    }

    p {
      color: color.adjust($text-color, $lightness: 30%);
      margin-top: 6px;
    }
  }
}
</style>
