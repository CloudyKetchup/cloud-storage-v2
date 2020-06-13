import React from 'react';

import MainLeftPanel  from './components/MainLeftPanel/MainLeftPanel';
import Main           from './components/Main/Main';

import { ThemeProvider } from './context/ThemeContext';

import './App.css';

const App = () => (
  <div className="App">
    <ThemeProvider>
      <MainLeftPanel />
      <Main/>
    </ThemeProvider>
  </div>
);

export default App;