import { createI18n } from "vue-i18n";

const messages = {
  pt: {
    menu: {
      title: "Menu Polaris",
      tasks: "Minhas Tarefas",
      new_task: "Nova Tarefa",
      my_profile: "Meu Perfil",
      profile: "Perfil",
      visitor: "Visitante",
      manager: "Gerente",
      developer: "Desenvolvedor",
      logout: "Sair",
    },
    login: {
      title: "Polaris",
      subtitle: "Gerenciador de Tarefas",
      email_label: "Email",
      password_label: "Senha",
      btn_enter: "ENTRAR",
      error_fields: "Preencha todos os campos.",
      error_cred: "Credenciais inválidas.",
      error_conn: "Erro de conexão com o servidor.",
    },
    tasks: {
      page_title: "Minhas Tarefas",
      title: "Gerenciar Tarefas",
      btn_delete: "Excluir",
      btn_new: "Nova Tarefa",
      col_code: "Código",
      col_title: "Título",
      col_assignee: "Responsável",
      col_status: "Status",
      col_updated: "Atualizado em",
      col_actions: "Ações",
      empty: "Nenhuma tarefa encontrada.",
      filter: "Filtrar por",
      filter_all: "Todos",
      filter_me: "Atribuídas a mim",

      create_page_title: "Nova Tarefa",
      edit_page_title: "Editar Tarefa",
      details_page_title: "Detalhes da Tarefa",

      field_code: "Código",
      field_title: "Título",
      field_title_placeholder: "Ex: Reunião com cliente",
      field_desc: "Descrição",
      field_desc_placeholder: "Detalhes da tarefa...",
      field_assignee: "Responsável",
      field_assignee_placeholder: "Selecione um usuário",

      btn_create: "CRIAR TAREFA",
      btn_save: "SALVAR ALTERAÇÕES",
      btn_cancel: "Cancelar",
      btn_edit: "EDITAR",

      label_created: "Criado em",
      label_unassigned: "Não atribuído",
      id_copied: "ID copiado!",

      msg_created: "Tarefa criada com sucesso!",
      msg_updated: "Tarefa atualizada com sucesso!",
      msg_deleted: "Tarefa excluída.",
      err_create: "Erro ao criar tarefa.",
      err_update: "Erro ao atualizar tarefa.",
      err_generic: "Erro ao carregar dados.",

      confirm_delete_title: "Confirmar Exclusão",
      confirm_delete_msg:
        "Tem certeza que deseja excluir",
    },
    status: {
      PLANNED: "Planejado",
      IN_PROGRESS: "Em Andamento",
      IN_REVIEW: "Em Revisão",
      COMPLETED: "Concluído",
      BLOCKED: "Impedimento",
      CANCELLED: "Cancelado",
    },
  },
  en: {
    menu: {
      title: "Polaris Menu",
      tasks: "My Tasks",
      new_task: "New Task",
      my_profile: "My Profile",
      profile: "Profile",
      visitor: "Guest",
      manager: "Manager",
      developer: "Developer",
      logout: "Logout",
    },
    login: {
      title: "Polaris",
      subtitle: "Task Manager",
      email_label: "Email",
      password_label: "Password",
      btn_enter: "LOGIN",
      error_fields: "Please fill in all fields.",
      error_cred: "Invalid credentials.",
      error_conn: "Connection error.",
    },
    tasks: {
      page_title: "My Tasks",
      title: "Manage Tasks",
      btn_delete: "Delete",
      btn_new: "New Task",
      col_code: "Code",
      col_title: "Title",
      col_assignee: "Assignee",
      col_status: "Status",
      col_updated: "Updated at",
      col_actions: "Actions",
      empty: "No tasks found.",
      filter: "Filter by",
      filter_all: "All",
      filter_me: "Assigned to me",

      create_page_title: "New Task",
      edit_page_title: "Edit Task",
      details_page_title: "Task Details",

      field_code: "Code",
      field_title: "Title",
      field_title_placeholder: "Ex: Client Meeting",
      field_desc: "Description",
      field_desc_placeholder: "Task details...",
      field_assignee: "Assignee",
      field_assignee_placeholder: "Select a user",

      btn_create: "CREATE TASK",
      btn_save: "SAVE CHANGES",
      btn_cancel: "Cancel",
      btn_edit: "EDIT",

      label_created: "Created at",
      label_unassigned: "Unassigned",
      id_copied: "ID copied!",

      msg_created: "Task created successfully!",
      msg_updated: "Task updated successfully!",
      msg_deleted: "Task deleted.",
      err_create: "Error creating task.",
      err_update: "Error updating task.",
      err_generic: "Error loading data.",

      confirm_delete_title: "Confirm Delete",
      confirm_delete_msg:
        "Are you sure you want to permanently delete",
    },
    status: {
      PLANNED: "Planned",
      IN_PROGRESS: "In Progress",
      IN_REVIEW: "In Review",
      COMPLETED: "Completed",
      BLOCKED: "Blocked",
      CANCELLED: "Cancelled",
    },
  },
};

const savedLocale = localStorage.getItem("polaris_lang") || "pt";

const i18n = createI18n({
  legacy: false,
  locale: savedLocale,
  fallbackLocale: "en",
  messages,
});

export default i18n;
