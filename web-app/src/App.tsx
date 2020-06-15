import React, { useContext, useEffect } from 'react';
import { Router } from 'react-router';

import { createBrowserHistory } from "history";

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

  useEffect(() =>
  {
    const fetchRoot = async () =>
    {
      const { data, error } = await folderClient.getRoot()

      setLoading(false);

      data ? setFolder(data) : setErrorLoadingApp(error !== null);
    };

    fetchRoot();
  }, []);

  return (
    <Router history={createBrowserHistory()}>
      <div className="App">
        <ThemeProvider>
          <MainLeftPanel />
          <Main />
        </ThemeProvider>
      </div>
    </Router>
  );
};

export default App;