import React, { useContext, useEffect } from 'react';

import MainLeftPanel  from './components/MainLeftPanel/MainLeftPanel';
import Main           from './components/Main/Main';

import { AppContext }       from './context/AppContext';
import { DirectoryContext } from './context/DirectoryContext';
import { ThemeProvider }    from './context/ThemeContext';

import FolderClient from './api/FolderClient';

import './App.css';

const App = () =>
{
  const { setLoading, setErrorLoadingApp } = useContext(AppContext);
  const { setFolder } = useContext(DirectoryContext);
  const folderClient  = FolderClient.instance();

  const onAppError = () =>
  {
    setLoading(false);
    setErrorLoadingApp(true);
  };

  useEffect(() =>
  {
    folderClient.getRoot(onAppError).then(root => root && setFolder(root));
  }, []);

  return (
    <div className="App">
      <ThemeProvider>
        <MainLeftPanel />
        <Main />
      </ThemeProvider>
    </div>
  );
};

export default App;