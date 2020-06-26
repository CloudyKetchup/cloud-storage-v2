import React, { createContext, FC, useState } from "react";

import { Folder } from "../models/Directory";

type IContext = {
  rootFolder?         : Folder
  loading             : boolean
  errorLoadingApp     : boolean
  setLoading          : (loading: boolean) => void
  setRootFolder       : (root: Folder) => void
  setErrorLoadingApp  : (err: boolean) => void
};

const AppContext = createContext<IContext>({
  loading             : true,
  errorLoadingApp     : false,
  setLoading          : (_loading: boolean) => {},
  setRootFolder       : (_root: Folder) => {},
  setErrorLoadingApp  : (_err: boolean) => {}
})

const AppConsumer = AppContext.Consumer;

const AppProvider: FC = props =>
{
  const [rootFolder, setRootFolder] = useState<Folder>();
  const [loading, setLoading]       = useState<boolean>(true);
  const [err, setErr]               = useState<boolean>(false);

  return (
    <AppContext.Provider
      value={{
        rootFolder,
        setRootFolder,
        loading,
        errorLoadingApp: err,
        setLoading,
        setErrorLoadingApp: setErr
      }}
    >
      {props.children}
    </AppContext.Provider>
  )
};

export { AppContext, AppConsumer, AppProvider };