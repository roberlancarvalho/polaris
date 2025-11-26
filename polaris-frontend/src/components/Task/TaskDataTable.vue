<template>
  <div class="data-table-container">
    <div class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th class="col-checkbox" v-if="userRole === 'ADMIN'">
              <ion-checkbox
                class="custom-checkbox"
                :checked="isAllSelected"
                :indeterminate="isIndeterminate"
                @ionChange="toggleSelectAll"
              ></ion-checkbox>
            </th>
            <th @click="sortBy('id')">
              Cód <ion-icon :icon="getSortIcon('id')"></ion-icon>
            </th>
            <th @click="sortBy('title')">
              Título <ion-icon :icon="getSortIcon('title')"></ion-icon>
            </th>

            <th>
              <div
                class="header-filter"
                @click="openFilterPopover"
                :class="{ active: filterUser !== '' }"
              >
                <span>Responsável</span>
                <ion-icon
                  :icon="filterUser !== '' ? funnel : funnelOutline"
                ></ion-icon>
              </div>
            </th>

            <th @click="sortBy('status')">
              Status <ion-icon :icon="getSortIcon('status')"></ion-icon>
            </th>
            <th @click="sortBy('updatedAt')">
              Atualizado em
              <ion-icon :icon="getSortIcon('updatedAt')"></ion-icon>
            </th>
            <th class="col-actions">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="task in paginatedTasks"
            :key="task.id"
            :class="{ 'row-selected': selectedIds.has(task.id) }"
          >
            <td v-if="userRole === 'ADMIN'">
              <ion-checkbox
                class="custom-checkbox"
                :checked="selectedIds.has(task.id)"
                @ionChange="toggleSelection(task.id)"
              ></ion-checkbox>
            </td>
            <td class="col-code">
              <div
                class="code-badge"
                @click.stop="copyToClipboard('#' + getShortId(task.id))"
                title="Copiar Código"
              >
                <span class="hash">#</span
                ><span class="value">{{ getShortId(task.id) }}</span>
                <ion-icon :icon="copyOutline" class="copy-icon"></ion-icon>
              </div>
            </td>
            <td class="col-title">{{ task.title }}</td>

            <td>
              <div class="assignee-badge" v-if="task.assignedTo">
                <div class="avatar-circle">
                  {{ getInitials(resolveUserName(task.assignedTo)) }}
                </div>
                <span>{{ resolveUserName(task.assignedTo) }}</span>
              </div>
              <span v-else class="unassigned">-</span>
            </td>

            <td>
              <div
                class="status-wrapper"
                :class="{ clickable: canChangeStatus(task) }"
                @click="(e) => openStatusPopover(e, task)"
              >
                <span :class="['status-badge', getStatusClass(task.status)]">
                  {{ translateStatus(task.status) }}
                </span>
                <ion-icon
                  v-if="canChangeStatus(task)"
                  :icon="chevronDownOutline"
                  class="edit-icon"
                ></ion-icon>
              </div>
            </td>
            <td>{{ formatDate(task.updatedAt || task.createdAt) }}</td>
            <td class="col-actions">
              <ion-button
                fill="clear"
                size="small"
                color="medium"
                @click="$emit('view', task)"
                ><ion-icon slot="icon-only" :icon="eyeOutline"></ion-icon
              ></ion-button>
              <ion-button
                v-if="userRole === 'ADMIN'"
                fill="clear"
                size="small"
                color="primary"
                @click="$emit('edit', task)"
                ><ion-icon slot="icon-only" :icon="createOutline"></ion-icon
              ></ion-button>
              <ion-button
                v-if="userRole === 'ADMIN'"
                fill="clear"
                size="small"
                color="danger"
                @click="$emit('delete', task)"
                ><ion-icon slot="icon-only" :icon="trashOutline"></ion-icon
              ></ion-button>
            </td>
          </tr>
          <tr v-if="paginatedTasks.length === 0">
            <td :colspan="userRole === 'ADMIN' ? 8 : 7" class="text-center">
              {{
                filterUser ? "Nenhuma tarefa encontrada." : "Nenhum registro."
              }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-controls">
      <span>Página {{ currentPage }} de {{ totalPages }}</span>
      <div class="buttons">
        <ion-button
          size="small"
          fill="outline"
          :disabled="currentPage === 1"
          @click="currentPage--"
          ><ion-icon :icon="chevronBackOutline"></ion-icon
        ></ion-button>
        <ion-button
          size="small"
          fill="outline"
          :disabled="currentPage === totalPages"
          @click="currentPage++"
          ><ion-icon :icon="chevronForwardOutline"></ion-icon
        ></ion-button>
      </div>
    </div>

    <ion-popover
      :is-open="popoverOpen"
      :event="popoverEvent"
      @didDismiss="popoverOpen = false"
      class="white-popover"
    >
      <ion-content class="ion-no-padding">
        <ion-list lines="full">
          <ion-item
            button
            v-for="status in availableStatuses"
            :key="status"
            @click="confirmStatusChange(status)"
          >
            <ion-label>{{ translateStatus(status) }}</ion-label>
          </ion-item>
        </ion-list>
      </ion-content>
    </ion-popover>

    <ion-popover
      :is-open="filterPopoverOpen"
      :event="filterPopoverEvent"
      @didDismiss="filterPopoverOpen = false"
      class="white-popover"
    >
      <ion-content class="ion-no-padding">
        <ion-list lines="full">
          <ion-list-header>Filtrar por:</ion-list-header>

          <ion-item button @click="selectFilterUser('')">
            <ion-label>Todos</ion-label>
            <ion-icon
              v-if="filterUser === ''"
              slot="end"
              :icon="checkmarkOutline"
              color="primary"
            ></ion-icon>
          </ion-item>

          <ion-item button @click="selectFilterUser('ME')">
            <ion-label>Atribuídas a mim</ion-label>
            <ion-icon
              v-if="filterUser === 'ME'"
              slot="end"
              :icon="checkmarkOutline"
              color="primary"
            ></ion-icon>
          </ion-item>

          <ion-item
            button
            v-for="user in allUsers"
            :key="resolveUserEmail(user)"
            @click="selectFilterUser(resolveUserEmail(user))"
          >
            <ion-label>{{ resolveUserNameObj(user) }}</ion-label>
            <ion-icon
              v-if="filterUser === resolveUserEmail(user)"
              slot="end"
              :icon="checkmarkOutline"
              color="primary"
            ></ion-icon>
          </ion-item>
        </ion-list>
      </ion-content>
    </ion-popover>
  </div>
</template>

<script setup lang="ts">
import {
  IonButton,
  IonCheckbox,
  IonContent,
  IonIcon,
  IonItem,
  IonLabel,
  IonList,
  IonListHeader,
  IonPopover,
  toastController,
} from "@ionic/vue";
import {
  checkmarkOutline,
  chevronBackOutline,
  chevronDownOutline,
  chevronForwardOutline,
  chevronUpOutline,
  copyOutline,
  createOutline,
  eyeOutline,
  funnel,
  funnelOutline,
  removeOutline,
  trashOutline,
} from "ionicons/icons";
import { computed, defineEmits, defineProps, ref, watch } from "vue";

type User = { name: string; email: string } | string;

interface Task {
  id: string;
  title: string;
  description: string;
  status: string;
  assignedTo?: string;
  createdAt?: string;
  completedAt?: string;
  updatedAt?: string;
}

const props = defineProps<{
  tasks: Task[];
  userRole: string;
  allUsers: User[];
  currentUser: string;
}>();

const emit = defineEmits([
  "view",
  "edit",
  "delete",
  "selection-change",
  "status-change",
]);

const currentPage = ref(1);
const itemsPerPage = 5;
const sortCol = ref("");
const sortAsc = ref(true);
const selectedIds = ref(new Set<string>());
const filterUser = ref("");
const popoverOpen = ref(false);
const popoverEvent = ref<Event | null>(null);
const editingTask = ref<Task | null>(null);
const availableStatuses = ref<string[]>([]);
const filterPopoverOpen = ref(false);
const filterPopoverEvent = ref<Event | null>(null);

const resolveUserEmail = (user: User): string => {
  return typeof user === "string" ? user : user.email;
};

const resolveUserNameObj = (user: User): string => {
  return typeof user === "string" ? user : user.name;
};

const resolveUserName = (email: string) => {
  const user = props.allUsers.find((u) => resolveUserEmail(u) === email);
  if (!user) return email;
  return resolveUserNameObj(user);
};

const getInitials = (n: string) => (n ? n.substring(0, 2).toUpperCase() : "??");
const getShortId = (id: string) => (id ? id.substring(0, 6).toUpperCase() : "");
const copyToClipboard = async (t: string) => {
  await navigator.clipboard.writeText(t);
  const to = await toastController.create({
    message: "Copiado!",
    duration: 1000,
    color: "dark",
    position: "bottom",
  });
  to.present();
};
const translateStatus = (s: string) => {
  const m: any = {
    PLANNED: "Planejado",
    IN_PROGRESS: "Em Andamento",
    IN_REVIEW: "Em Revisão",
    COMPLETED: "Concluído",
    BLOCKED: "Impedimento",
    CANCELLED: "Cancelado",
  };
  return m[s] || s;
};
const getStatusClass = (s: string) =>
  s ? s.toLowerCase().replace("_", "-") : "";

const formatDate = (d?: string) => {
  if (!d) return "-";
  const date = new Date(d);
  return (
    date.toLocaleDateString("pt-BR") +
    " " +
    date.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" })
  );
};

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

const canChangeStatus = (task: Task) => {
  if (props.userRole === "ADMIN") return true;

  if (props.userRole === "USER") {
    return task.assignedTo === props.currentUser;
  }

  return false;
};

const openStatusPopover = (e: Event, task: Task) => {
  if (!canChangeStatus(task)) return;

  const opts = getAllowedStatuses(task.status, props.userRole);
  if (!opts.length) return;

  editingTask.value = task;
  availableStatuses.value = opts;
  popoverEvent.value = e;
  popoverOpen.value = true;
};

const confirmStatusChange = (s: string) => {
  if (editingTask.value)
    emit("status-change", { id: editingTask.value.id, status: s });
  popoverOpen.value = false;
};
const openFilterPopover = (e: Event) => {
  filterPopoverEvent.value = e;
  filterPopoverOpen.value = true;
};
const selectFilterUser = (email: string) => {
  filterUser.value = email;
  filterPopoverOpen.value = false;
  currentPage.value = 1;
  selectedIds.value.clear();
  emit("selection-change", []);
};

const filteredTasks = computed(() => {
  let list = [...props.tasks];
  if (filterUser.value) {
    if (filterUser.value === "ME") {
      list = list.filter((t) => t.assignedTo === props.currentUser);
    } else {
      list = list.filter((t) => t.assignedTo === filterUser.value);
    }
  }
  return list;
});

const sortedTasks = computed(() => {
  const list = [...filteredTasks.value];
  if (!sortCol.value) return list;
  return list.sort((a: any, b: any) => {
    let vA = a[sortCol.value] || "",
      vB = b[sortCol.value] || "";
    return vA < vB
      ? sortAsc.value
        ? -1
        : 1
      : vA > vB
      ? sortAsc.value
        ? 1
        : -1
      : 0;
  });
});

const totalPages = computed(
  () => Math.ceil(sortedTasks.value.length / itemsPerPage) || 1
);
const paginatedTasks = computed(() =>
  sortedTasks.value.slice(
    (currentPage.value - 1) * itemsPerPage,
    currentPage.value * itemsPerPage
  )
);
const sortBy = (c: string) => {
  sortCol.value === c
    ? (sortAsc.value = !sortAsc.value)
    : ((sortCol.value = c), (sortAsc.value = true));
};
const getSortIcon = (c: string) =>
  sortCol.value !== c
    ? removeOutline
    : sortAsc.value
    ? chevronUpOutline
    : chevronDownOutline;
const isAllSelected = computed(
  () =>
    filteredTasks.value.length > 0 &&
    selectedIds.value.size === filteredTasks.value.length
);
const isIndeterminate = computed(
  () =>
    selectedIds.value.size > 0 &&
    selectedIds.value.size < filteredTasks.value.length
);
const toggleSelectAll = (e: any) => {
  e.detail.checked
    ? filteredTasks.value.forEach((t) => selectedIds.value.add(t.id))
    : selectedIds.value.clear();
  emit("selection-change", Array.from(selectedIds.value));
};
const toggleSelection = (id: string) => {
  selectedIds.value.has(id)
    ? selectedIds.value.delete(id)
    : selectedIds.value.add(id);
  emit("selection-change", Array.from(selectedIds.value));
};

watch(
  () => props.tasks,
  () => {
    currentPage.value = 1;
  }
);
</script>

<style lang="scss" scoped>
@use "@/theme/_mixins.scss" as *;

.data-table-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.table-wrapper {
  overflow-x: auto;
  min-height: 300px;
}

table {
  width: 100%;
  border-collapse: collapse;
  min-width: 800px;

  thead {
    background-color: #f8f9fa;

    th {
      padding: 12px;
      text-align: left;
      font-size: 0.85rem;
      font-weight: 700;
      color: #666;
      cursor: pointer;
      white-space: nowrap;
      user-select: none;

      ion-icon {
        vertical-align: middle;
        margin-left: 4px;
      }
    }
  }

  tbody {
    tr {
      border-bottom: 1px solid #eee;
      transition: background 0.2s;

      &:hover {
        background-color: #fbfbfb;
      }

      &.row-selected {
        background-color: rgba(var(--ion-color-primary-rgb), 0.05);
      }
    }

    td {
      padding: 10px 12px;
      font-size: 0.9rem;
      color: #333;
      vertical-align: middle;
    }
  }
}

.header-filter {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;

  &:hover {
    background-color: #e9ecef;
  }

  &.active {
    color: var(--ion-color-primary);
    background-color: rgba(var(--ion-color-primary-rgb), 0.1);
  }
}

.assignee-badge {
  display: flex;
  align-items: center;
  gap: 8px;

  .avatar-circle {
    width: 26px;
    height: 26px;
    background: var(--ion-color-secondary);
    color: white;
    border-radius: 50%;
    font-size: 10px;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    text-transform: uppercase;
  }

  span {
    font-size: 0.85rem;
    font-weight: 500;
  }
}

.unassigned {
  color: #ccc;
  font-style: italic;
}

ion-checkbox {
  --size: 20px;
  --checkbox-background: #ffffff !important;
  --border-color: #c8c8c8 !important;
  --border-radius: 4px;
  --checkbox-background-checked: var(--ion-color-primary) !important;
  --border-color-checked: var(--ion-color-primary) !important;
  --checkmark-color: #ffffff !important;
  margin: 0;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.code-badge {
  display: inline-flex;
  align-items: center;
  background-color: #f1f3f5;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 4px 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  font-family: "Consolas", "Monaco", monospace;
  font-size: 0.85rem;
  color: #495057;

  .hash {
    color: #adb5bd;
    margin-right: 2px;
    font-weight: normal;
  }

  .value {
    font-weight: 700;
    color: #343a40;
  }

  .copy-icon {
    margin-left: 8px;
    font-size: 12px;
    color: #adb5bd;
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover {
    background-color: #e9ecef;
    border-color: #ced4da;

    .copy-icon {
      opacity: 1;
      color: var(--ion-color-primary);
    }
  }

  &:active {
    transform: scale(0.98);
  }
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
      font-size: 10px;
      color: #888;
    }
  }
}

.col-actions {
  white-space: nowrap;
  text-align: right;
}

.text-center {
  text-align: center;
  padding: 20px;
  color: #999;
}

.pagination-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-top: 1px solid #eee;
  font-size: 0.9rem;
  color: #666;
}
</style>

<style>
.white-popover {
  --color: #333 !important;
}

.white-popover .popover-content {
  background: #df3030 !important;
}
</style>
