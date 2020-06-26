import React, { useContext, useEffect } from 'react';
import { Router } from 'react-router';

import MainLeftPanel  from './components/MainLeftPanel/MainLeftPanel';
import Main           from './components/Main/Main';

import { AppContext }         from './context/AppContext';
import { DirectoryContext }   from './context/DirectoryContext';
import { ThemeProvider }      from './context/ThemeContext';
import { FileUploadProvider } from "./context/FileUploadContext";
import { FilesProvider }      from "./context/FilesContext";

import FolderClient from './api/FolderClient';

import { history } from "./utils/history";

import './App.css';

const App = () =>
{
  const { setLoading, setErrorLoadingApp, setRootFolder } = useContext(AppContext);
  const { setFolder } = useContext(DirectoryContext);
  const folderClient  = FolderClient.instance();

  useEffect(() =>
  {
    const fetchRoot = async () =>
    {
      const { data, error } = await folderClient.getRoot()

      setLoading(false);

      if (data)
      {
        setFolder(data)
        setRootFolder(data);
      } else setErrorLoadingApp(error !== null);
    };

    fetchRoot();
  }, []);

  return (
    <Router history={history}>
      <div className="App">
        <ThemeProvider>
          <FileUploadProvider>
            <FilesProvider>
              <MainLeftPanel />
              <Main />
            </FilesProvider>
          </FileUploadProvider>
        </ThemeProvider>
      </div>
    </Router>
  );
};

export default App;