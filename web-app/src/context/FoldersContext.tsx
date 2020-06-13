import React, { createContext, useState, FC } from "react";

import { Folder } from "../models/Directory";

import { findById, deleteById } from "../utils/array.utils";

type IContext = {
  folders     : Folder[]
  setFolders  : (folders: Folder[]) => void
  addFolder   : (folder: Folder)    => void
  deleteFolder: (id: string)        => void
};

const FoldersContext = createContext<IContext>({
  folders       : [],
  setFolders    : (_folders: Folder[])  => {},
  addFolder     : (_folder: Folder)     => {},
  deleteFolder  : (_id: string)         => {}
});

const FoldersConsumer = FoldersContext.Consumer;

const FoldersProvider: FC = props =>
{
  const [folders, setFolders] = useState<Folder[]>([]);

  const addFolder = async (folder: Folder) =>
  {
    if (findById<Folder>(folders, folder.id))
    {
      deleteById<Folder>(folders, folder.id);
    }
    folders.push(folder);

    setFolders([...folders]);
  };

  const deleteFolder = (id: string) =>
  {
    deleteById<Folder>(folders, id);

    setFolders([...folders]);
  };

  return (
    <FoldersContext.Provider value={{ folders, setFolders, addFolder, deleteFolder }}>
      {props.children}
    </FoldersContext.Provider>
  );
};

export { FoldersContext, FoldersConsumer, FoldersProvider };