import React from 'react';

import MainLeftPanel from './components/MainLeftPanel/MainLeftPanel';

import { ThemeProvider } from './context/ThemeContext';

import './App.css';

const App = () => (
  <div className="App">
    <ThemeProvider>
      <MainLeftPanel />
    </ThemeProvider>
  </div>
);

export default App;