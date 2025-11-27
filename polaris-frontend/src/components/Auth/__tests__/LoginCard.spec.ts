import { describe, it, expect, vi } from "vitest";
import { mount } from "@vue/test-utils";
import LoginCard from "../LoginCard.vue";

const mockPush = vi.fn();
const mockReplace = vi.fn();

vi.mock("vue-router", () => ({
  useRouter: () => ({
    push: mockPush,
    replace: mockReplace,
  }),
}));

vi.mock("vue-i18n", () => ({
  useI18n: () => ({
    t: (key: string) => key,
  }),
}));

describe("LoginCard.vue", () => {
  const mountComponent = () => {
    return mount(LoginCard, {
      global: {
        mocks: {
          $t: (key: string) => key,
        },
      },
    });
  };

  it("Renderiza o título corretamente", () => {
    const wrapper = mountComponent();

    expect(wrapper.text()).toContain("login.title");
    expect(wrapper.text()).toContain("login.subtitle");
  });

  it("Mostra erro se tentar logar com campos vazios", async () => {
    const wrapper = mountComponent();

    const button = wrapper.find(".login-btn");
    expect(button.exists()).toBe(true);

    await button.trigger("click");

    expect((wrapper.vm as any).errorMessage).toBe("login.error_fields");
  });
});
