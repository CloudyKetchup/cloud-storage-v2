import React, { createContext, useState, FC } from "react";

import { File } from "../models/Directory";

import { findById, deleteById } from "../utils/array.utils";

type IContext = {
  files     : File[]
  setFiles  : (files: File[]) => void
  addFile   : (file: File)    => void
  deleteFile: (id: string)    => void
};

const FilesContext = createContext<IContext>({
  files       : [],
  setFiles    : (_files: File[])  => {},
  addFile     : (_file: File)     => {},
  deleteFile  : (_id: string)     => {}
});

const FilesConsumer = FilesContext.Consumer;

const FilesProvider: FC = props =>
{
  const [files, setFiles] = useState<File[]>([]);

  const addFile = async (file: File) =>
  {
    if (findById<File>(files, file.id))
    {
      deleteById<File>(files, file.id);
    }
    files.push(file);

    setFiles([...files]);
  };

  const deleteFile = (id: string) =>
  {
    deleteById<File>(files, id);

    setFiles([...files]);
  };

  return (
    <FilesContext.Provider value={{ files, setFiles, addFile, deleteFile }}>
      {props.children}
    </FilesContext.Provider>
  );
};

export { FilesContext, FilesConsumer, FilesProvider };