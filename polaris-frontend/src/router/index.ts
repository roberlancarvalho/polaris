import EditTaskPage from "@/views/EditTaskPage.vue";
import TaskDetailsPage from "@/views/TaskDetailsPage.vue";
import { createRouter, createWebHistory } from "@ionic/vue-router";
import { RouteRecordRaw } from "vue-router";
import CreateTaskPage from "../views/CreateTaskPage.vue";
import HomePage from "../views/HomePage.vue";
import TasksPage from "../views/TasksPage.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    name: "Home",
    component: HomePage,
  },
  {
    path: "/tasks",
    name: "Tasks",
    component: TasksPage,
    beforeEnter: (to, from, next) => {
      if (!localStorage.getItem("polaris_token")) next("/home");
      else next();
    },
  },
  {
    path: "/tasks/create",
    name: "CreateTask",
    component: CreateTaskPage,
    beforeEnter: (to, from, next) => {
      if (!localStorage.getItem("polaris_token")) next("/home");
      else next();
    },
  },
  {
    path: "/tasks/edit/:id",
    name: "EditTask",
    component: EditTaskPage,
    beforeEnter: (to, from, next) => {
      if (!localStorage.getItem("polaris_token")) next("/home");
      else next();
    },
  },
  {
    path: "/tasks/details/:id",
    name: "TaskDetails",
    component: TaskDetailsPage,
    beforeEnter: (to, from, next) => {
      if (!localStorage.getItem("polaris_token")) next("/home");
      else next();
    },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
