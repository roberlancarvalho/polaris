import { config } from "@vue/test-utils";

const StubWithSlot = {
  template: '<div class="ionic-stub"><slot /></div>',
};

config.global.stubs = {
  "ion-card": StubWithSlot,
  "ion-card-header": StubWithSlot,
  "ion-card-title": StubWithSlot,
  "ion-card-subtitle": StubWithSlot,
  "ion-card-content": StubWithSlot,
  "ion-item": StubWithSlot,
  "ion-label": StubWithSlot,
  "ion-button": StubWithSlot,
  "ion-page": StubWithSlot,
  "ion-content": StubWithSlot,
  "ion-input": true,
  "ion-spinner": true,
  "ion-icon": true,
  "ion-select": true,
  "ion-select-option": true,
  "ion-textarea": true,
};
