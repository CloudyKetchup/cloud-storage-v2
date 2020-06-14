import React from 'react';
import ReactDOM from 'react-dom';

import App from './App';

import { DirectoryProvider } from './context/DirectoryContext';
import { AppProvider } from './context/AppContext';

import './css/index.css';

import * as serviceWorker from './serviceWorker';

ReactDOM.render(
  <DirectoryProvider>
    <AppProvider>
      <App />
    </AppProvider>
  </DirectoryProvider>
, document.getElementById('root'));

serviceWorker.unregister();
