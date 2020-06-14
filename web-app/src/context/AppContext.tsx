import React, { createContext, FC, useState } from "react";

type IContext = {
  loading             : boolean
  errorLoadingApp     : boolean
  setLoading          : (loading: boolean) => void
  setErrorLoadingApp  : (err: boolean) => void
};

const AppContext = createContext<IContext>({
  loading             : true,
  errorLoadingApp     : false,
  setLoading          : (_loading: boolean) => {},
  setErrorLoadingApp  : (_err: boolean) => {}
})

const AppConsumer = AppContext.Consumer;

const AppProvider: FC = props =>
{
  const [loading, setLoading] = useState<boolean>(true);
  const [err, setErr]         = useState<boolean>(false);

  return (
    <AppContext.Provider
      value={{
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