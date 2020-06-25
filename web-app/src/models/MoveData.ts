import { Folder, File } from "./Directory";

export type FolderMoveData = {
  folder        : Folder,
  targetFolder  : Folder
};

export type FileMoveData = {
  file    : File,
  folder  : Folder
};