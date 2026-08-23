import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

document.documentElement.setAttribute('data-theme', 'light');

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
