import React, { useState, createContext, FC } from "react"

import { Folder } from "../models/Directory";

interface IContext
{
  folder?   : Folder
  setFolder : (folder: Folder) => void
};

const DirectoryContext = createContext<IContext>({
  setFolder : (_folder: Folder) => {}
})

const DirectoryConsumer = DirectoryContext.Consumer;

const DirectoryProvider: FC = props =>
{
  const [folder, setFolder] = useState<Folder | undefined>();

  return (
    <DirectoryContext.Provider value={{ folder, setFolder }}>
      {props.children}
    </DirectoryContext.Provider>
  );
};

export { DirectoryContext, DirectoryConsumer, DirectoryProvider };